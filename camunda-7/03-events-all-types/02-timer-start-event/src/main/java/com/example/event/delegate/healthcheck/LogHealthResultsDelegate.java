package com.example.event.delegate.healthcheck;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Logs the overall health check results.
 * Aggregates API and database status into a final health report.
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

        LOGGER.info("📋 Health Check Summary at: {}", checkTime);
        LOGGER.info("   API Status:      {}", apiStatus);
        LOGGER.info("   Database Status:  {}", dbStatus);
        LOGGER.info("   Overall Status:   {}", overallStatus);

        execution.setVariable("overallHealthStatus", overallStatus);
    }
}
