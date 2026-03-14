package com.example.event.controller;

import com.example.event.dto.DocumentWebhook;
import com.example.event.dto.PaymentWebhook;
import org.camunda.bpm.engine.RuntimeService;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller for webhook operations.
 * Handles external system webhooks (payment gateways, document storage, etc.).
 */
@RestController
@RequestMapping("/api/webhooks")
public class WebhookController {

    private final RuntimeService runtimeService;

    public WebhookController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    /**
     * Handles payment received webhook from payment gateway.
     *
     * @param webhook Payment webhook data
     * @return Webhook processing status
     */
    @PostMapping("/payment-received")
    public Map<String, String> handlePaymentWebhook(@RequestBody PaymentWebhook webhook) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("transactionId", webhook.transactionId());
        variables.put("amount", webhook.amount());
        variables.put("currency", webhook.currency());
        variables.put("paymentMethod", webhook.paymentMethod());
        variables.put("customerId", webhook.customerId());

        var instance = runtimeService.startProcessInstanceByKey(
            "payment-webhook-process",
            webhook.transactionId(),
            variables
        );

        return Map.of(
            "status", "accepted",
            "processInstanceId", instance.getId(),
            "transactionId", webhook.transactionId()
        );
    }

    /**
     * Handles document uploaded webhook from cloud storage.
     *
     * @param webhook Document webhook data
     * @return Webhook processing status
     */
    @PostMapping("/document-uploaded")
    public Map<String, String> handleDocumentUpload(@RequestBody DocumentWebhook webhook) {
        Map<String, Object> variables = new HashMap<>();
        variables.put("documentId", webhook.documentId());
        variables.put("documentType", webhook.documentType());
        variables.put("uploadedBy", webhook.uploadedBy());
        variables.put("fileUrl", webhook.fileUrl());

        var instance = runtimeService.startProcessInstanceByKey(
            "none-start-event-process",
            webhook.documentId(),
            variables
        );

        return Map.of(
            "status", "processing",
            "processInstanceId", instance.getId(),
            "documentId", webhook.documentId()
        );
    }


}
