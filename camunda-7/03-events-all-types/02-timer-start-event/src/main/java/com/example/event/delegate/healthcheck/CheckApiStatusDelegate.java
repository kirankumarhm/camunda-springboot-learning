package com.example.event.delegate.healthcheck;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Checks the health status of API endpoints.
 * Simulates HTTP health checks against configured service endpoints.
 */
@Component("checkApiStatusDelegate")
public class CheckApiStatusDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckApiStatusDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void execute(final DelegateExecution execution) {
        final String checkTime = LocalDateTime.now().format(FORMATTER);
        final String apiStatus = "HEALTHY";
        final int responseTimeMs = 45;

        LOGGER.info("🌐 Checking API endpoint status at: {}", checkTime);
        LOGGER.info("   Endpoint: /api/v1/health → {} ({}ms)", apiStatus, responseTimeMs);
        LOGGER.info("   Endpoint: /api/v1/orders → HEALTHY (62ms)");
        LOGGER.info("   Endpoint: /api/v1/users  → HEALTHY (38ms)");

        execution.setVariable("checkTime", checkTime);
        execution.setVariable("apiStatus", apiStatus);
        execution.setVariable("apiResponseTimeMs", responseTimeMs);
    }
}
