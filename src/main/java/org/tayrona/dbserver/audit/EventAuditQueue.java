package org.tayrona.dbserver.audit;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

@Slf4j
@Component
public class EventAuditQueue implements Runnable {
    private static final String CLASS_NAME = EventAuditQueue.class.getSimpleName();
    private static EventAuditQueue instance;
    private final Queue<EventQueueItem> queue;
    private TransactionIdFactory transactionIdFactory;
    private NamedParameterJdbcTemplate jdbcTemplate;
    private Thread thread;
    private volatile boolean requestStop;

    private static final String sql =
            "MERGE INTO AUDIT.EVENTS(TDAY, TSEQ, TCATALOG, TSQUEMA, TTABLE, TACTION, PAYLOAD)" +
                    " VALUES(:tday, :tseq, :tcatalog, :tsquema, :ttable, :taction, :payload)";

    private EventAuditQueue() {
        queue = new ConcurrentLinkedQueue<>();
        instance = this;
        log.debug("{} - constructed", CLASS_NAME);
    }

    @PostConstruct
    public void setup() {
        log.debug("{}.setup()", CLASS_NAME);
        requestStop = false;
        thread = new Thread(this, this.getClass().getSimpleName());
//        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void shutdown() {
        log.debug("{}.shutdown()", CLASS_NAME);
        requestStop = true;
        thread.interrupt();
    }

    void putItem(EventQueueItem item) {
        log.debug("{}.putItem(item)", CLASS_NAME);
        synchronized (queue) {
            queue.add(item);
            queue.notifyAll();
        }
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
        log.debug("{}.run() - start delay", CLASS_NAME);
        try {
            Thread.sleep(1000 * 30);
        } catch (InterruptedException ex) {
            log.error(ex.getMessage(), ex);
            return;
        }
        log.debug("{}.run() - running", CLASS_NAME);
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
                    requestStop = true;
                }
            } catch (Exception e) {
                log.error("{}.run() - {}", CLASS_NAME, e.getMessage(), e);
            }
        }
    }

    static EventAuditQueue get() {
        return instance;
    }

    private void process(EventQueueItem item) {
        if (item != null) {
            String payload = item.getPayload().toString();
            if (!requestStop) {
                log.debug("{}.process({})", CLASS_NAME, payload);
                try {
                    TransactionIdentifier id = transactionIdFactory.get();
                    Map<String, Object> paramMap = new HashMap<>();
                    paramMap.put("tday", id.getNumOfDays());
                    paramMap.put("tseq", id.getSeq());
                    paramMap.put("tcatalog", item.getCatalogName());
                    paramMap.put("tsquema", item.getSchemaName());
                    paramMap.put("ttable", item.getTableName());
                    paramMap.put("taction", item.getAction());
                    paramMap.put("payload", payload);
                    jdbcTemplate.update(sql, paramMap);
                } catch (Exception e) {
                    log.error("{}.process() - {}", CLASS_NAME, e.getMessage(), e);
                }
            } else {
                log.warn("{}.process(item) - request stop signaled!", CLASS_NAME);
            }
        } else {
            log.warn("{}.process(item) - item is null!", CLASS_NAME);
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
