package com.example.event.delegate.dailyreport;

import java.util.UUID;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Generates a compliance report from the fetched daily transactions.
 *
 * <p>Real-world context: After fetching settled transactions, the compliance department
 * needs a structured report (e.g., PDF or CSV) containing transaction summaries,
 * suspicious activity flags, and aggregate totals. This report is archived for
 * regulatory audits (typically retained for 5-7 years under SOX/AML requirements).</p>
 *
 * <p>Process variables read: {@code reportDate}, {@code transactionCount}, {@code totalAmount}</p>
 * <p>Process variables set: {@code reportId}, {@code reportStatus}</p>
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

        LOGGER.info("Generating compliance report: {}", reportId);
        LOGGER.info("  Report Date: {} | Transactions: {} | Total: ${}",
                reportDate, transactionCount, String.format("%.2f", totalAmount));

        execution.setVariable("reportId", reportId);
        execution.setVariable("reportStatus", "GENERATED");
    }
}
