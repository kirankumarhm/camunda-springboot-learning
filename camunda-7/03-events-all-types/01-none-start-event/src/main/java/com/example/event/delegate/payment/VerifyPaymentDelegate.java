package com.example.event.delegate.payment;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to verify payment from webhook.
 * Validates payment information received from payment gateway.
 */
@Component("verifyPaymentDelegate")
public class VerifyPaymentDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String transactionId = (String) execution.getVariable("transactionId");
        Double amount = (Double) execution.getVariable("amount");
        String paymentMethod = (String) execution.getVariable("paymentMethod");
        
        System.out.println("🔍 Verifying payment: " + transactionId 
                         + " - Amount: $" + amount 
                         + " - Method: " + paymentMethod);
        
        // Business logic: Verify payment with gateway
        boolean paymentVerified = true; // Simplified for demo
        
        execution.setVariable("paymentVerified", paymentVerified);
    }
}
