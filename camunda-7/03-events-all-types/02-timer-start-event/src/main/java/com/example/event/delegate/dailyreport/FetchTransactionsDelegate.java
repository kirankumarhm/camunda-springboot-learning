package com.example.event.delegate.dailyreport;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Fetches all transactions from the previous business day.
 * Simulates querying a transaction database and storing the count as a process variable.
 */
@Component("fetchTransactionsDelegate")
public class FetchTransactionsDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(FetchTransactionsDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final LocalDate reportDate = LocalDate.now().minusDays(1);
        final int transactionCount = 1542;

        LOGGER.info("📥 Fetching transactions for date: {}", reportDate.format(DateTimeFormatter.ISO_LOCAL_DATE));
        LOGGER.info("   Found {} transactions to process", transactionCount);

        execution.setVariable("reportDate", reportDate.toString());
        execution.setVariable("transactionCount", transactionCount);
        execution.setVariable("totalAmount", 284750.75);
    }
}
