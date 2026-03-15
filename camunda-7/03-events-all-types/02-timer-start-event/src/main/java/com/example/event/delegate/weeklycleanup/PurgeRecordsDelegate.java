package com.example.event.delegate.weeklycleanup;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Purges expired records from the database.
 *
 * <p>Real-world context: After identifying stale data, this delegate performs batch
 * DELETE operations against the shopping cart and session tables. In production, this
 * would use soft-delete first (marking records as deleted), then hard-delete after
 * a grace period. The operation is typically wrapped in a database transaction with
 * batch size limits to avoid long-running locks on high-traffic tables.</p>
 *
 * <p>Process variables read: {@code expiredCartCount}, {@code expiredSessionCount}</p>
 * <p>Process variables set: {@code totalPurgedRecords}, {@code purgeStatus}</p>
 */
@Component("purgeRecordsDelegate")
public class PurgeRecordsDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeRecordsDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final int expiredCarts = (int) execution.getVariable("expiredCartCount");
        final int expiredSessions = (int) execution.getVariable("expiredSessionCount");
        final int totalPurged = expiredCarts + expiredSessions;

        LOGGER.info("Purging expired records...");
        LOGGER.info("  Deleted {} abandoned carts", expiredCarts);
        LOGGER.info("  Deleted {} expired sessions", expiredSessions);
        LOGGER.info("  Total records purged: {}", totalPurged);

        execution.setVariable("totalPurgedRecords", totalPurged);
        execution.setVariable("purgeStatus", "COMPLETED");
    }
}
