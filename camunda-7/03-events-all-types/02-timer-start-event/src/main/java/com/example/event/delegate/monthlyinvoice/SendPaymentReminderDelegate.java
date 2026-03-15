package com.example.event.delegate.monthlyinvoice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sends payment reminder emails to customers with generated invoices.
 *
 * <p>Real-world context: After invoice generation, each customer receives an email
 * with the invoice PDF attached and a secure payment link (e.g., Stripe Checkout
 * or PayPal). The email includes the billing period, amount due, and payment
 * deadline. Customers on auto-pay receive a notification that their card will
 * be charged, while manual-pay customers get a reminder with a pay-now button.</p>
 *
 * <p>Process variables read: {@code invoiceBatchId}, {@code invoicesGenerated}, {@code totalCharges}</p>
 * <p>Process variables set: {@code remindersSent}, {@code invoiceStatus}</p>
 */
@Component("sendPaymentReminderDelegate")
public class SendPaymentReminderDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendPaymentReminderDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String invoiceBatchId = (String) execution.getVariable("invoiceBatchId");
        final int invoicesGenerated = (int) execution.getVariable("invoicesGenerated");
        final double totalCharges = (double) execution.getVariable("totalCharges");

        LOGGER.info("Sending payment reminders for batch: {}", invoiceBatchId);
        LOGGER.info("  Emails sent: {}", invoicesGenerated);
        LOGGER.info("  Total billing amount: ${}", String.format("%.2f", totalCharges));

        execution.setVariable("remindersSent", invoicesGenerated);
        execution.setVariable("invoiceStatus", "REMINDERS_SENT");
    }
}
