package com.example.event.delegate.order;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to validate order data.
 * Checks if order information is complete and valid.
 */
@Component("validateOrderDelegate")
public class ValidateOrderDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        String customerId = (String) execution.getVariable("customerId");
        
        System.out.println("✅ Validating order: " + orderId + " for customer: " + customerId);
        
        // Business logic: Validate order data
        boolean isValid = orderId != null && !orderId.isEmpty() 
                       && customerId != null && !customerId.isEmpty();
        
        execution.setVariable("orderValid", isValid);
    }
}
