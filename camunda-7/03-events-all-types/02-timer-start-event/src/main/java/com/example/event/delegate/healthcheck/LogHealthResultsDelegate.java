package com.example.event.delegate.healthcheck;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logs the overall health check results and determines system-wide status.
 *
 * <p>Real-world context: After checking individual components (APIs, databases),
 * this delegate aggregates the results into an overall health status. In production,
 * this would publish metrics to a monitoring platform (e.g., Prometheus, CloudWatch)
 * and trigger PagerDuty/OpsGenie alerts if the status is DEGRADED. The health
 * history is also stored for SLA reporting and incident post-mortems.</p>
 *
 * <p>Process variables read: {@code checkTime}, {@code apiStatus}, {@code dbStatus}</p>
 * <p>Process variables set: {@code overallHealthStatus}</p>
 */
@Component("logHealthResultsDelegate")
public class LogHealthResultsDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(LogHealthResultsDelegate.class);

    @Override
    public void execute(final DelegateExecution execution) {
        final String checkTime = (String) execution.getVariable("checkTime");
        final String apiStatus = (String) execution.getVariable("apiStatus");
        final String dbStatus = (String) execution.getVariable("dbStatus");
        final String overallStatus = "HEALTHY".equals(apiStatus) && "HEALTHY".equals(dbStatus)
                ? "ALL_SYSTEMS_HEALTHY" : "DEGRADED";

        LOGGER.info("Health Check Summary at: {}", checkTime);
        LOGGER.info("  API Status:      {}", apiStatus);
        LOGGER.info("  Database Status:  {}", dbStatus);
        LOGGER.info("  Overall Status:   {}", overallStatus);

        execution.setVariable("overallHealthStatus", overallStatus);
    }
}
