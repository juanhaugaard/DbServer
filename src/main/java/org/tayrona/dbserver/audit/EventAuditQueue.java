package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class EventAuditQueue implements Runnable {
    private TransactionIdFactory transactionIdFactory;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Thread thread;
    private Queue<EventQueueItem> queue = null;
    private boolean requestStop;
    private static EventAuditQueue instance;

    private static String sql =
            "MERGE INTO AUDIT.EVENTS(TDATE, TSEQ, TSQUEMA, TTABLE, TACTION, PAYLOAD)" +
                    " VALUES(:tdate, :tseq, :tsquema, :ttable, :taction, :payload)";

    private EventAuditQueue() {
        queue = new ConcurrentLinkedQueue<>();
        instance = this;
        log.debug("Queue constructed");
    }

    @PostConstruct
    public void setup() throws SQLException {
        log.debug("Queue setup");
        requestStop = false;
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
            try {
                TransactionIdentifier id = transactionIdFactory.get();
                Map<String, Object> paramMap = new HashMap<>();
                paramMap.put("tdate", id.getDate());
                paramMap.put("tseq", id.getSeq());
                paramMap.put("tsquema", item.getSchemaName());
                paramMap.put("ttable", item.getTableName());
                paramMap.put("taction", item.getAction());
                paramMap.put("payload", item.getPayload());
                jdbcTemplate.update(sql, paramMap);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    @Autowired
    public void setTransactionIdFactory(TransactionIdFactory transactions) {
        this.transactionIdFactory = transactions;
    }

    @Autowired
    @Qualifier("NamedJdbcTemplate")
    public void setJdbcTemplate(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
}
