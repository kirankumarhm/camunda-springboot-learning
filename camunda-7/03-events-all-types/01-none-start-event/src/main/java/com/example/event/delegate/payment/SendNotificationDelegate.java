package com.example.event.delegate.payment;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to send payment confirmation notification.
 * Sends email to customer confirming payment received.
 */
@Component("sendNotificationDelegate")
public class SendNotificationDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String customerId = (String) execution.getVariable("customerId");
        String transactionId = (String) execution.getVariable("transactionId");
        Double amount = (Double) execution.getVariable("amount");
        
        System.out.println("📧 Sending payment confirmation to customer: " + customerId);
        System.out.println("   Transaction: " + transactionId + " - Amount: $" + amount);
        
        // Business logic: Send email notification
        boolean notificationSent = true; // Simplified for demo
        
        execution.setVariable("notificationSent", notificationSent);
    }
}
