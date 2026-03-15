package com.example.event.delegate.monthlyinvoice;

import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Generates invoice documents for all active subscriptions.
 *
 * <p>Real-world context: For each active subscription, a PDF invoice is generated
 * containing line items (base plan fee, add-on charges, usage overages), applicable
 * taxes, and payment terms (Net 30). The invoices are stored in a document management
 * system and linked to the customer's billing account. A batch ID groups all invoices
 * for the same billing cycle for reconciliation purposes.</p>
 *
 * <p>Process variables read: {@code billingPeriod}, {@code activeSubscriptions}</p>
 * <p>Process variables set: {@code invoiceBatchId}, {@code invoicesGenerated}</p>
 */
@Component("generateInvoiceDelegate")
public class GenerateInvoiceDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateInvoiceDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String billingPeriod = (String) execution.getVariable("billingPeriod");
        final int activeSubscriptions = (int) execution.getVariable("activeSubscriptions");
        final String batchId = "INV-BATCH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LOGGER.info("Generating invoices for period: {}", billingPeriod);
        LOGGER.info("  Batch ID: {}", batchId);
        LOGGER.info("  Invoices generated: {}", activeSubscriptions);

        execution.setVariable("invoiceBatchId", batchId);
        execution.setVariable("invoicesGenerated", activeSubscriptions);
    }
}
