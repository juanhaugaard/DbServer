package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
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
    private Queue<EventQueueItem> queue = new ConcurrentLinkedQueue<>();
    private boolean requestStop;
    private static EventAuditQueue instance = null;

    @PostConstruct
    public void setup() throws SQLException {
        instance = this;
        log.debug("Queue setup");
        requestStop = false;
        conn = dataSource.getConnection(username, password);
        thread = new Thread(this, this.getClass().getSimpleName());
        thread.setDaemon(true);
        thread.start();
    }

    public static synchronized EventAuditQueue get() {
        while (instance == null) {
            try {
                EventAuditQueue.class.wait();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
                return null;
            }
        }
        return instance;
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
            queue.notify();
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
                        queue.wait();
                    }
                } catch (InterruptedException ex) {
                    log.error(e.getMessage(), ex);
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void process(EventQueueItem item) {
        log.debug("Queue process");
        if (item != null) {
            TransactionIdentifier id = transactions.get();
            String sql = String.format("INSERT INTO AUDIT.EVENTS(TDATE, TSEQ, TSQUEMA, TTABLE, TACTION, PAYLOAD) VALUES(%s, %s, %s, %s, %s, %s)",
                    id.getDate().toString(), id.getSeq().toString(), item.getSchemaName(),
                    item.getTableName(), item.getAction(), item.getPayload().toString());
            try (Statement stat = conn.createStatement()) {
                stat.execute(sql);
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }
}
