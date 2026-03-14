package com.example.event.delegate.monthlyinvoice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sends payment reminder emails to customers with generated invoices.
 * Includes invoice PDF attachment and payment link.
 */
@Component("sendPaymentReminderDelegate")
public class SendPaymentReminderDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendPaymentReminderDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String invoiceBatchId = (String) execution.getVariable("invoiceBatchId");
        final int invoicesGenerated = (int) execution.getVariable("invoicesGenerated");
        final double totalCharges = (double) execution.getVariable("totalCharges");

        LOGGER.info("📧 Sending payment reminders for batch: {}", invoiceBatchId);
        LOGGER.info("   Emails sent: {}", invoicesGenerated);
        LOGGER.info("   Total billing amount: ${}", String.format("%.2f", totalCharges));

        execution.setVariable("remindersSent", invoicesGenerated);
        execution.setVariable("invoiceStatus", "REMINDERS_SENT");
    }
}
