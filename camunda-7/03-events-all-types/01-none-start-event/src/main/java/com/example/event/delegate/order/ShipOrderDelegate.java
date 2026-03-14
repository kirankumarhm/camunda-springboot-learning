package com.example.event.delegate.order;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Delegate to ship an order.
 * Creates shipment and generates tracking number.
 */
@Component("shipOrderDelegate")
public class ShipOrderDelegate implements JavaDelegate {
    
    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        String customerId = (String) execution.getVariable("customerId");
        
        System.out.println("🚚 Shipping order: " + orderId + " to customer: " + customerId);
        
        // Business logic: Create shipment
        String trackingNumber = "TRK-" + System.currentTimeMillis();
        String shippingStatus = "SHIPPED";
        
        execution.setVariable("trackingNumber", trackingNumber);
        execution.setVariable("shippingStatus", shippingStatus);
        
        System.out.println("   Tracking Number: " + trackingNumber);
    }
}
