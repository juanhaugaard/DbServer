package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class EventAuditQueue implements Runnable {
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Autowired
    private DataSource dataSource;
    @Autowired
    private TransactionIdFactory transactions;
    private Connection conn;
    private Thread thread;
    private Queue<EventQueueItem> queue = null;
    private boolean requestStop;
    private static EventAuditQueue instance;

    private EventAuditQueue() {
        queue = new ConcurrentLinkedQueue<>();
        instance = this;
        log.debug("Queue constructed");
    }

    @PostConstruct
    public void setup() throws SQLException {
        log.debug("Queue setup");
        requestStop = false;
        conn = dataSource.getConnection(username, password);
        thread = new Thread(this, this.getClass().getSimpleName());
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void shutdown() {
        log.debug("Queue shutdown");
        requestStop = true;
        thread.interrupt();
    }

    public boolean put(EventQueueItem item) {
        log.debug("Queue put");
        boolean ret;
        synchronized (queue) {
            ret = queue.add(item);
            queue.notifyAll();
        }
        return ret;
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    @Override
    public void run() {
        log.debug("Queue run");
        EventQueueItem item;
        while (!requestStop) {
            try {
                item = queue.remove();
                process(item);
            } catch (NoSuchElementException e) {
                try {
                    synchronized (queue) {
                        queue.wait(1000);
                    }
                } catch (InterruptedException ex) {
                    log.error(e.getMessage(), ex);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    public static EventAuditQueue get() {
        return instance;
    }

    private void process(EventQueueItem item) {
        log.debug("Queue process");
        if (item != null) {
            TransactionIdentifier id = transactions.get();
            String payload = item.getPayload().toString();
            String sql = "MERGE INTO AUDIT.EVENTS(TDATE, TSEQ, TSQUEMA, TTABLE, TACTION, PAYLOAD) VALUES(?, ?, ?, ?, ?, ?)";
            try (PreparedStatement stat = conn.prepareStatement(sql)) {
                stat.setDate(1, id.getDate());
                stat.setLong(2, id.getSeq());
                stat.setString(3, item.getSchemaName());
                stat.setString(4, item.getTableName());
                stat.setString(5, item.getAction());
                stat.setString(6, payload);
                stat.execute();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
