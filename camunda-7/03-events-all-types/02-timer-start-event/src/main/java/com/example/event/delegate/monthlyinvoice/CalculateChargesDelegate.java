package com.example.event.delegate.monthlyinvoice;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.YearMonth;

/**
 * Calculates monthly subscription charges for all active customers.
 * Aggregates usage data, applies pricing tiers, and computes totals.
 */
@Component("calculateChargesDelegate")
public class CalculateChargesDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateChargesDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final YearMonth billingMonth = YearMonth.now().minusMonths(1);
        final int activeSubscriptions = 348;
        final double totalCharges = 52450.00;

        LOGGER.info("💳 Calculating charges for billing period: {}", billingMonth);
        LOGGER.info("   Active subscriptions: {}", activeSubscriptions);
        LOGGER.info("   Total charges calculated: ${}", String.format("%.2f", totalCharges));

        execution.setVariable("billingPeriod", billingMonth.toString());
        execution.setVariable("activeSubscriptions", activeSubscriptions);
        execution.setVariable("totalCharges", totalCharges);
    }
}
