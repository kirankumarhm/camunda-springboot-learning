package com.example.event.delegate.dailyreport;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Sends the generated compliance report via email to stakeholders.
 * Simulates email delivery and marks the report as delivered.
 */
@Component("sendReportEmailDelegate")
public class SendReportEmailDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(SendReportEmailDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String reportId = (String) execution.getVariable("reportId");
        final String reportDate = (String) execution.getVariable("reportDate");

        LOGGER.info("📧 Sending report {} via email", reportId);
        LOGGER.info("   Recipients: compliance-team@example.com, finance@example.com");
        LOGGER.info("   Subject: Daily Transaction Report - {}", reportDate);

        execution.setVariable("reportStatus", "DELIVERED");
        execution.setVariable("emailSent", true);
    }
}
