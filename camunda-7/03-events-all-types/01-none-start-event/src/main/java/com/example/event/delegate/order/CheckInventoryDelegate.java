package com.example.event.delegate.order;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to check inventory availability.
 * Verifies if ordered items are in stock.
 */
@Component("checkInventoryDelegate")
public class CheckInventoryDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        String items = (String) execution.getVariable("items");
        
        System.out.println("📦 Checking inventory for order: " + orderId + " - Items: " + items);
        
        // Business logic: Check inventory availability
        boolean inventoryAvailable = true; // Simplified for demo
        
        execution.setVariable("inventoryAvailable", inventoryAvailable);
    }
}
