package com.example.event.delegate.dailyreport;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Generates a compliance report from the fetched transactions.
 * Simulates report creation and stores the report ID as a process variable.
 */
@Component("generateReportDelegate")
public class GenerateReportDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(GenerateReportDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String reportDate = (String) execution.getVariable("reportDate");
        final int transactionCount = (int) execution.getVariable("transactionCount");
        final double totalAmount = (double) execution.getVariable("totalAmount");
        final String reportId = "RPT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        LOGGER.info("📊 Generating compliance report: {}", reportId);
        LOGGER.info("   Report Date: {}", reportDate);
        LOGGER.info("   Transactions: {} | Total Amount: ${}", transactionCount, String.format("%.2f", totalAmount));

        execution.setVariable("reportId", reportId);
        execution.setVariable("reportStatus", "GENERATED");
    }
}
