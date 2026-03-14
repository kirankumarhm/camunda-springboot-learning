package com.example.event.delegate.weeklycleanup;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Identifies expired data records eligible for cleanup.
 * Scans for abandoned carts older than 30 days and expired user sessions.
 */
@Component("identifyExpiredDataDelegate")
public class IdentifyExpiredDataDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentifyExpiredDataDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(final DelegateExecution execution) {
        final String timestamp = LocalDateTime.now().format(FORMATTER);
        final int expiredCarts = 237;
        final int expiredSessions = 1893;

        LOGGER.info("🔍 Identifying expired data at: {}", timestamp);
        LOGGER.info("   Abandoned carts (>30 days): {}", expiredCarts);
        LOGGER.info("   Expired sessions: {}", expiredSessions);

        execution.setVariable("expiredCartCount", expiredCarts);
        execution.setVariable("expiredSessionCount", expiredSessions);
        execution.setVariable("cleanupStartTime", timestamp);
    }
}
