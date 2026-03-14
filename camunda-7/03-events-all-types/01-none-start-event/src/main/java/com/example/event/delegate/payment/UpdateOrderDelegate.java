package com.example.event.delegate.payment;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to update order status after payment verification.
 * Marks order as paid in the system.
 */
@Component("updateOrderDelegate")
public class UpdateOrderDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String transactionId = (String) execution.getVariable("transactionId");
        String customerId = (String) execution.getVariable("customerId");
        
        System.out.println("📝 Updating order status for transaction: " + transactionId 
                         + " - Customer: " + customerId);
        
        // Business logic: Update order status in database
        String orderStatus = "PAID";
        
        execution.setVariable("orderStatus", orderStatus);
    }
}
