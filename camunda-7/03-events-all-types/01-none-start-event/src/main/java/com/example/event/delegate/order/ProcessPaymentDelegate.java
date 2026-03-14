package com.example.event.delegate.order;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to process payment for an order.
 * Handles payment processing and updates payment status.
 */
@Component("processPaymentDelegate")
public class ProcessPaymentDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        Double amount = (Double) execution.getVariable("totalAmount");
        String customerId = (String) execution.getVariable("customerId");
        
        System.out.println("💳 Processing payment for order: " + orderId 
                         + " - Amount: $" + amount 
                         + " - Customer: " + customerId);
        
        // Business logic: Process payment
        String paymentStatus = "SUCCESS"; // Simplified for demo
        String transactionId = "TXN-" + System.currentTimeMillis();
        
        execution.setVariable("paymentStatus", paymentStatus);
        execution.setVariable("transactionId", transactionId);
    }
}
