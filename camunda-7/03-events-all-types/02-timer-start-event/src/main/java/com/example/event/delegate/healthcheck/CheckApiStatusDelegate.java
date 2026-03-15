package com.example.event.delegate.healthcheck;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Checks the health status of API endpoints.
 *
 * <p>Real-world context: A cloud monitoring service (similar to Pingdom, UptimeRobot,
 * or Datadog Synthetics) performs periodic HTTP health checks against critical API
 * endpoints. Each endpoint is hit with a GET request, and the response status code
 * and latency are recorded. If any endpoint returns a non-2xx status or exceeds
 * the latency threshold (e.g., 500ms), it is flagged as DEGRADED.</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code checkTime} - Timestamp of the health check</li>
 *   <li>{@code apiStatus} - Overall API health (HEALTHY or DEGRADED)</li>
 *   <li>{@code apiResponseTimeMs} - Average response time across endpoints</li>
 * </ul>
 */
@Component("checkApiStatusDelegate")
public class CheckApiStatusDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckApiStatusDelegate.class);
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final int LATENCY_THRESHOLD_MS = 500;

    @Override
    public void execute(final DelegateExecution execution) {
        final String checkTime = LocalDateTime.now().format(FORMATTER);
        final int healthEndpointMs = ThreadLocalRandom.current().nextInt(20, 120);
        final int ordersEndpointMs = ThreadLocalRandom.current().nextInt(30, 200);
        final int usersEndpointMs = ThreadLocalRandom.current().nextInt(25, 150);
        final int avgResponseTimeMs = (healthEndpointMs + ordersEndpointMs + usersEndpointMs) / 3;
        final String apiStatus = avgResponseTimeMs < LATENCY_THRESHOLD_MS ? "HEALTHY" : "DEGRADED";

        LOGGER.info("Checking API endpoint status at: {}", checkTime);
        LOGGER.info("  /api/v1/health  -> 200 OK ({}ms)", healthEndpointMs);
        LOGGER.info("  /api/v1/orders  -> 200 OK ({}ms)", ordersEndpointMs);
        LOGGER.info("  /api/v1/users   -> 200 OK ({}ms)", usersEndpointMs);
        LOGGER.info("  Average response time: {}ms | Status: {}", avgResponseTimeMs, apiStatus);

        execution.setVariable("checkTime", checkTime);
        execution.setVariable("apiStatus", apiStatus);
        execution.setVariable("apiResponseTimeMs", avgResponseTimeMs);
    }
}
