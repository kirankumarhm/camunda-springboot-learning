package com.example.event.delegate.batch;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to send invoice via email.
 * Emails generated invoice to customer.
 */
@Component("sendInvoiceDelegate")
public class SendInvoiceDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String invoiceId = (String) execution.getVariable("invoiceId");
        String customerEmail = (String) execution.getVariable("customerEmail");
        Double invoiceAmount = (Double) execution.getVariable("invoiceAmount");
        
        System.out.println("📧 Sending invoice " + invoiceId + " to " + customerEmail);
        System.out.println("   Amount: $" + invoiceAmount);
        
        // Business logic: Send email with invoice attachment
        boolean invoiceSent = true;
        
        execution.setVariable("invoiceSent", invoiceSent);
    }
}
