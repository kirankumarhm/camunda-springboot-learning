package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("paymentFulfillmentDelegate")
public class PaymentFulfillmentDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentFulfillmentDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String transactionId = (String) execution.getVariable("transactionId");
        String orderId = (String) execution.getVariable("orderId");
        Double amount = (Double) execution.getVariable("amount");
        String paymentProvider = (String) execution.getVariable("paymentProvider");

        LOG.info("=== Payment Notification Fulfillment ===");
        LOG.info("  Process Instance ID : {}", execution.getProcessInstanceId());
        LOG.info("  Transaction ID      : {}", transactionId);
        LOG.info("  Order ID            : {}", orderId);
        LOG.info("  Amount              : {}", amount);
        LOG.info("  Payment Provider    : {}", paymentProvider);
        LOG.info("  Fulfillment initiated successfully.");
    }
}
