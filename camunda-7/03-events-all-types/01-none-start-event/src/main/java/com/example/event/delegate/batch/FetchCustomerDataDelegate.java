package com.example.event.delegate.batch;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to fetch customer data for invoice generation.
 * Retrieves customer information from database.
 */
@Component("fetchCustomerDataDelegate")
public class FetchCustomerDataDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String customerId = (String) execution.getVariable("customerId");
        
        System.out.println("📊 Fetching customer data for: " + customerId);
        
        // Business logic: Fetch customer data from database
        String customerName = "Customer-" + customerId;
        String customerEmail = customerId + "@example.com";
        String customerAddress = "123 Main St, City, Country";
        
        execution.setVariable("customerName", customerName);
        execution.setVariable("customerEmail", customerEmail);
        execution.setVariable("customerAddress", customerAddress);
    }
}
