package com.example.event.delegate.weeklycleanup;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Purges expired records from the database.
 * Deletes abandoned carts and expired sessions identified in the previous step.
 */
@Component("purgeRecordsDelegate")
public class PurgeRecordsDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(PurgeRecordsDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final int expiredCarts = (int) execution.getVariable("expiredCartCount");
        final int expiredSessions = (int) execution.getVariable("expiredSessionCount");
        final int totalPurged = expiredCarts + expiredSessions;

        LOGGER.info("🗑️ Purging expired records...");
        LOGGER.info("   Deleted {} abandoned carts", expiredCarts);
        LOGGER.info("   Deleted {} expired sessions", expiredSessions);
        LOGGER.info("   Total records purged: {}", totalPurged);

        execution.setVariable("totalPurgedRecords", totalPurged);
        execution.setVariable("purgeStatus", "COMPLETED");
    }
}
