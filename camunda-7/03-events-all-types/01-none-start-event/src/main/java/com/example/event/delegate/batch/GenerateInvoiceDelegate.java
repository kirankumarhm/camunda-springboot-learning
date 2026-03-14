package com.example.event.delegate.batch;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to generate invoice for customer.
 * Creates invoice document with billing details.
 */
@Component("generateInvoiceDelegate")
public class GenerateInvoiceDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String customerId = (String) execution.getVariable("customerId");
        String billingPeriod = (String) execution.getVariable("billingPeriod");
        String customerName = (String) execution.getVariable("customerName");
        
        String invoiceId = "INV-" + customerId + "-" + billingPeriod;
        
        System.out.println("📄 Generating invoice: " + invoiceId);
        System.out.println("   Customer: " + customerName + " - Period: " + billingPeriod);
        
        // Business logic: Generate invoice PDF
        Double invoiceAmount = 1500.00;
        String invoiceStatus = "GENERATED";
        
        execution.setVariable("invoiceId", invoiceId);
        execution.setVariable("invoiceAmount", invoiceAmount);
        execution.setVariable("invoiceStatus", invoiceStatus);
    }
}
