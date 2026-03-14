package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("customerOnboardingDelegate")
public class CustomerOnboardingDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(CustomerOnboardingDelegate.class);

    @Override
    public void execute(DelegateExecution execution) {
        String customerId = (String) execution.getVariable("customerId");
        String verificationStatus = (String) execution.getVariable("verificationStatus");
        String kycProvider = (String) execution.getVariable("kycProvider");

        LOG.info("=== Customer Onboarding via Webhook ===");
        LOG.info("  Process Instance ID   : {}", execution.getProcessInstanceId());
        LOG.info("  Customer ID           : {}", customerId);
        LOG.info("  Verification Status   : {}", verificationStatus);
        LOG.info("  KYC Provider          : {}", kycProvider);
        LOG.info("  Customer onboarding completed successfully.");
    }
}
