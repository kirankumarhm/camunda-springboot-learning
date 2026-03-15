package com.example.event.delegate.notification;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sends a personalized welcome email to the newly registered user.
 *
 * <p>Real-world context: The welcome email is a critical part of user onboarding.
 * It typically includes a getting-started guide, links to documentation, and a
 * CTA to complete their profile. In production, this would integrate with a
 * transactional email service (e.g., SendGrid, Mailgun) using a pre-designed
 * HTML template. The email is only sent if the account is active and the email
 * address has been verified to avoid bounces and spam complaints.</p>
 *
 * <p>Process variables read: {@code accountActive}, {@code emailVerified}</p>
 * <p>Process variables set: {@code welcomeEmailSent}</p>
 */
@Component("sendWelcomeEmailDelegate")
public class SendWelcomeEmailDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendWelcomeEmailDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final boolean accountActive = (boolean) execution.getVariable("accountActive");
        final boolean emailVerified = (boolean) execution.getVariable("emailVerified");

        if (accountActive && emailVerified) {
            LOGGER.info("Sending welcome email to verified user");
            LOGGER.info("  Template: welcome-onboarding-v2");
            LOGGER.info("  Status: SENT");
            execution.setVariable("welcomeEmailSent", true);
        } else {
            LOGGER.warn("Skipping welcome email - Account not ready");
            LOGGER.warn("  Account Active: {} | Email Verified: {}", accountActive, emailVerified);
            execution.setVariable("welcomeEmailSent", false);
        }
    }
}
