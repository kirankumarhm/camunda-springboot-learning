package com.example.event.delegate.notification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sends a personalized welcome email to the newly registered user.
 * Only sends if account is active and email is verified.
 */
@Component("sendWelcomeEmailDelegate")
public class SendWelcomeEmailDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendWelcomeEmailDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final boolean accountActive = (boolean) execution.getVariable("accountActive");
        final boolean emailVerified = (boolean) execution.getVariable("emailVerified");

        if (accountActive && emailVerified) {
            LOGGER.info("📧 Sending welcome email to: user@example.com");
            LOGGER.info("   Template: welcome-onboarding-v2");
            LOGGER.info("   Status: SENT");
            execution.setVariable("welcomeEmailSent", true);
        } else {
            LOGGER.warn("⚠️ Skipping welcome email - Account not ready");
            LOGGER.warn("   Account Active: {} | Email Verified: {}", accountActive, emailVerified);
            execution.setVariable("welcomeEmailSent", false);
        }
    }
}
