package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("orderIntakeDelegate")
public class OrderIntakeDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(OrderIntakeDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String orderId = (String) execution.getVariable("orderId");
        String customerName = (String) execution.getVariable("customerName");
        String channel = (String) execution.getVariable("channel");

        LOG.info("=== E-Commerce Order Intake ===");
        LOG.info("  Process Instance ID : {}", execution.getProcessInstanceId());
        LOG.info("  Business Key        : {}", execution.getBusinessKey());
        LOG.info("  Order ID            : {}", orderId);
        LOG.info("  Customer            : {}", customerName);
        LOG.info("  Channel             : {}", channel);
        LOG.info("  Order intake completed successfully.");
    }
}
