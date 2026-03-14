package com.example.event.controller;

import com.example.event.dto.PaymentConfirmedRequest;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/webhook")
public class PaymentWebhookController {

    private static final Logger LOG = LoggerFactory.getLogger(PaymentWebhookController.class);

    private static final String PROCESS_KEY = "payment-fulfillment-process";
    private static final String MESSAGE_NAME = "Msg_PaymentConfirmed";

    private final RuntimeService runtimeService;

    public PaymentWebhookController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/payment-confirmed")
    public ResponseEntity<String> handlePaymentConfirmed(@RequestBody PaymentConfirmedRequest request) {
        String businessKey = request.getTransactionId();
        LOG.info("Webhook received: Payment [{}] confirmed by [{}]", businessKey, request.getPaymentProvider());

        ProcessInstance existing = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (existing != null) {
            LOG.warn("Fulfillment process already running for transactionId: {} (instance: {}). Skipping duplicate.", businessKey, existing.getId());
            return ResponseEntity.ok("Fulfillment process already running for transaction: " + businessKey);
        }

        runtimeService.createMessageCorrelation(MESSAGE_NAME)
                .processInstanceBusinessKey(businessKey)
                .setVariable("transactionId", request.getTransactionId())
                .setVariable("orderId", request.getOrderId())
                .setVariable("amount", request.getAmount())
                .setVariable("paymentProvider", request.getPaymentProvider())
                .correlate();

        return ResponseEntity.ok("Fulfillment process started for transaction: " + businessKey);
    }
}
