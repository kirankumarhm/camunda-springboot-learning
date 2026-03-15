package com.example.event.delegate.monthlyinvoice;

import java.time.YearMonth;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Calculates monthly subscription charges for all active customers.
 *
 * <p>Real-world context: A SaaS company (e.g., a project management tool like Jira or
 * Asana) bills customers monthly based on their subscription tier (Basic, Pro, Enterprise)
 * and usage metrics (number of seats, storage consumed, API calls). This delegate
 * aggregates usage data from the metering service, applies pricing tiers and volume
 * discounts, and computes the total charges for the billing period.</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code billingPeriod} - The month being billed (e.g., "2026-02")</li>
 *   <li>{@code activeSubscriptions} - Number of active paying customers</li>
 *   <li>{@code totalCharges} - Aggregate billing amount for the period</li>
 * </ul>
 */
@Component("calculateChargesDelegate")
public class CalculateChargesDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CalculateChargesDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final YearMonth billingMonth = YearMonth.now().minusMonths(1);
        final int activeSubscriptions = ThreadLocalRandom.current().nextInt(200, 600);
        final double totalCharges = activeSubscriptions * ThreadLocalRandom.current().nextDouble(80.00, 200.00);

        LOGGER.info("Calculating charges for billing period: {}", billingMonth);
        LOGGER.info("  Active subscriptions: {}", activeSubscriptions);
        LOGGER.info("  Total charges calculated: ${}", String.format("%.2f", totalCharges));

        execution.setVariable("billingPeriod", billingMonth.toString());
        execution.setVariable("activeSubscriptions", activeSubscriptions);
        execution.setVariable("totalCharges", totalCharges);
    }
}
