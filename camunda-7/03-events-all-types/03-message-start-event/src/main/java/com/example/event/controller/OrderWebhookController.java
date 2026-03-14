package com.example.event.controller;

import com.example.event.dto.OrderReceivedRequest;
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
public class OrderWebhookController {

    private static final Logger LOG = LoggerFactory.getLogger(OrderWebhookController.class);

    private static final String PROCESS_KEY = "ecommerce-order-intake-process";
    private static final String MESSAGE_NAME = "Msg_ECommerceOrderReceived";

    private final RuntimeService runtimeService;

    public OrderWebhookController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/order-received")
    public ResponseEntity<String> handleOrderReceived(@RequestBody OrderReceivedRequest request) {
        String businessKey = request.getOrderId();
        LOG.info("Webhook received: E-Commerce Order [{}] from channel [{}]", businessKey, request.getChannel());

        ProcessInstance existing = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (existing != null) {
            LOG.warn("Order process already running for orderId: {} (instance: {}). Skipping duplicate.", businessKey, existing.getId());
            return ResponseEntity.ok("Order process already running for: " + businessKey);
        }

        runtimeService.createMessageCorrelation(MESSAGE_NAME)
                .processInstanceBusinessKey(businessKey)
                .setVariable("orderId", request.getOrderId())
                .setVariable("customerName", request.getCustomerName())
                .setVariable("channel", request.getChannel())
                .correlate();

        return ResponseEntity.ok("Order process started for: " + businessKey);
    }
}
