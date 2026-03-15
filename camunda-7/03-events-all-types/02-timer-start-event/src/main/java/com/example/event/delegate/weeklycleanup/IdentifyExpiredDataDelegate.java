package com.example.event.delegate.weeklycleanup;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Identifies expired data records eligible for cleanup.
 *
 * <p>Real-world context: An e-commerce platform like Shopify or WooCommerce accumulates
 * abandoned shopping carts (users who added items but never checked out) and expired
 * HTTP sessions. GDPR and data retention policies require periodic purging of stale
 * personal data. This delegate scans the database for carts older than 30 days and
 * sessions that exceeded their TTL.</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code expiredCartCount} - Number of abandoned carts older than 30 days</li>
 *   <li>{@code expiredSessionCount} - Number of expired user sessions</li>
 *   <li>{@code cleanupStartTime} - Timestamp when the scan began</li>
 * </ul>
 */
@Component("identifyExpiredDataDelegate")
public class IdentifyExpiredDataDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(IdentifyExpiredDataDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(final DelegateExecution execution) {
        final String timestamp = LocalDateTime.now().format(FORMATTER);
        final int expiredCarts = ThreadLocalRandom.current().nextInt(100, 500);
        final int expiredSessions = ThreadLocalRandom.current().nextInt(1000, 5000);

        LOGGER.info("Identifying expired data at: {}", timestamp);
        LOGGER.info("  Abandoned carts (>30 days): {}", expiredCarts);
        LOGGER.info("  Expired sessions: {}", expiredSessions);

        execution.setVariable("expiredCartCount", expiredCarts);
        execution.setVariable("expiredSessionCount", expiredSessions);
        execution.setVariable("cleanupStartTime", timestamp);
    }
}
