package com.example.event.delegate.dailyreport;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Fetches all transactions from the previous business day for compliance reporting.
 *
 * <p>Real-world context: A financial institution (e.g., a retail bank or payment processor)
 * must generate end-of-day transaction reports for regulatory compliance (e.g., PCI-DSS,
 * SOX, or AML regulations). This delegate simulates querying a core banking transaction
 * ledger for the previous day's settled transactions.</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code reportDate} - The business date being reported (ISO format)</li>
 *   <li>{@code transactionCount} - Number of transactions found</li>
 *   <li>{@code totalAmount} - Aggregate monetary value of all transactions</li>
 * </ul>
 */
@Component("fetchTransactionsDelegate")
public class FetchTransactionsDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTransactionsDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final LocalDate reportDate = LocalDate.now().minusDays(1);
        final int transactionCount = ThreadLocalRandom.current().nextInt(800, 3500);
        final double totalAmount = ThreadLocalRandom.current().nextDouble(150000.00, 500000.00);

        LOGGER.info("Fetching transactions for date: {}", reportDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        LOGGER.info("  Found {} settled transactions totaling ${}", transactionCount, String.format("%.2f", totalAmount));

        execution.setVariable("reportDate", reportDate.toString());
        execution.setVariable("transactionCount", transactionCount);
        execution.setVariable("totalAmount", totalAmount);
    }
}
