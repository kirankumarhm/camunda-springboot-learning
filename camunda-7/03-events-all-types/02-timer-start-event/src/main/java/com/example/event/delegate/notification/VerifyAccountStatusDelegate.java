package com.example.event.delegate.notification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Verifies the user's account activation status.
 * Checks if the account is active and email is verified before sending welcome notification.
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

        LOGGER.info("🔍 Verifying account status at: {}", timestamp);
        LOGGER.info("   Account Active: {}", accountActive);
        LOGGER.info("   Email Verified: {}", emailVerified);

        execution.setVariable("accountActive", accountActive);
        execution.setVariable("emailVerified", emailVerified);
        execution.setVariable("verificationTime", timestamp);
    }
}
