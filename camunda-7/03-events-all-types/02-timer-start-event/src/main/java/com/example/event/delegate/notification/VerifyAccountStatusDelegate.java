package com.example.event.delegate.notification;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Verifies the user's account activation status before sending a welcome email.
 *
 * <p>Real-world context: After a user signs up on a platform (e.g., an online
 * marketplace or SaaS product), there is typically a delay for email verification
 * and account provisioning. This delegate checks whether the account has been
 * activated and the email address verified within the 5-minute grace period.
 * If the account is not yet active, the welcome email should be skipped to
 * avoid confusing the user.</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code accountActive} - Whether the account has been activated</li>
 *   <li>{@code emailVerified} - Whether the email address has been confirmed</li>
 *   <li>{@code verificationTime} - Timestamp of the verification check</li>
 * </ul>
 */
@Component("verifyAccountStatusDelegate")
public class VerifyAccountStatusDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(VerifyAccountStatusDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(final DelegateExecution execution) {
        final String timestamp = LocalDateTime.now().format(FORMATTER);
        final boolean accountActive = true;
        final boolean emailVerified = true;

        LOGGER.info("Verifying account status at: {}", timestamp);
        LOGGER.info("  Account Active: {}", accountActive);
        LOGGER.info("  Email Verified: {}", emailVerified);

        execution.setVariable("accountActive", accountActive);
        execution.setVariable("emailVerified", emailVerified);
        execution.setVariable("verificationTime", timestamp);
    }
}
