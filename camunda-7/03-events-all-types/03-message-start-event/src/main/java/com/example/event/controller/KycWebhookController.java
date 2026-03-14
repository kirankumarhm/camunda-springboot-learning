package com.example.event.controller;

import com.example.event.dto.KycVerificationRequest;
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
public class KycWebhookController {

    private static final Logger LOG = LoggerFactory.getLogger(KycWebhookController.class);

    private static final String PROCESS_KEY = "customer-onboarding-process";
    private static final String MESSAGE_NAME = "Msg_KycVerificationCompleted";

    private final RuntimeService runtimeService;

    public KycWebhookController(RuntimeService runtimeService) {
        this.runtimeService = runtimeService;
    }

    @PostMapping("/kyc-verified")
    public ResponseEntity<String> handleKycVerified(@RequestBody KycVerificationRequest request) {
        String businessKey = request.getCustomerId();
        LOG.info("Webhook received: KYC verification [{}] for customer [{}]", request.getVerificationStatus(), businessKey);

        ProcessInstance existing = runtimeService.createProcessInstanceQuery()
                .processDefinitionKey(PROCESS_KEY)
                .processInstanceBusinessKey(businessKey)
                .singleResult();

        if (existing != null) {
            LOG.warn("Onboarding process already running for customerId: {} (instance: {}). Skipping duplicate.", businessKey, existing.getId());
            return ResponseEntity.ok("Onboarding process already running for customer: " + businessKey);
        }

        runtimeService.createMessageCorrelation(MESSAGE_NAME)
                .processInstanceBusinessKey(businessKey)
                .setVariable("customerId", request.getCustomerId())
                .setVariable("verificationStatus", request.getVerificationStatus())
                .setVariable("kycProvider", request.getKycProvider())
                .correlate();

        return ResponseEntity.ok("Onboarding process started for customer: " + businessKey);
    }
}
