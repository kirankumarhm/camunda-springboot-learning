package com.example.event.delegate.weeklycleanup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logs the cleanup summary with metrics for auditing and monitoring.
 *
 * <p>Real-world context: After purging stale data, the summary is logged for operational
 * dashboards (e.g., Grafana, Datadog) and audit trails. The metrics help the ops team
 * track data growth trends and tune retention policies. If the purge count exceeds
 * thresholds, alerts can be triggered to investigate unusual data accumulation.</p>
 *
 * <p>Process variables read: {@code cleanupStartTime}, {@code totalPurgedRecords}</p>
 * <p>Process variables set: {@code cleanupEndTime}</p>
 */
@Component("logCleanupSummaryDelegate")
public class LogCleanupSummaryDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogCleanupSummaryDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(final DelegateExecution execution) {
        final String startTime = (String) execution.getVariable("cleanupStartTime");
        final String endTime = LocalDateTime.now().format(FORMATTER);
        final int totalPurged = (int) execution.getVariable("totalPurgedRecords");

        LOGGER.info("Weekly Cleanup Summary");
        LOGGER.info("  Start Time: {}", startTime);
        LOGGER.info("  End Time: {}", endTime);
        LOGGER.info("  Total Records Purged: {}", totalPurged);
        LOGGER.info("  Status: COMPLETED SUCCESSFULLY");

        execution.setVariable("cleanupEndTime", endTime);
    }
}
