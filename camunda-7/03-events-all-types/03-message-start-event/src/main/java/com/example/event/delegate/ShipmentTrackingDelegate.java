package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("shipmentTrackingDelegate")
public class ShipmentTrackingDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ShipmentTrackingDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String trackingNumber = (String) execution.getVariable("trackingNumber");
        String carrier = (String) execution.getVariable("carrier");
        String status = (String) execution.getVariable("status");
        String orderId = (String) execution.getVariable("orderId");

        LOG.info("=== Shipment Tracking Update ===");
        LOG.info("  Process Instance ID : {}", execution.getProcessInstanceId());
        LOG.info("  Tracking Number     : {}", trackingNumber);
        LOG.info("  Carrier             : {}", carrier);
        LOG.info("  Status              : {}", status);
        LOG.info("  Order ID            : {}", orderId);
        LOG.info("  Shipment update processed successfully.");
    }
}
