package com.example.event.delegate.healthcheck;

import java.util.concurrent.ThreadLocalRandom;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Checks the health status of database connections.
 *
 * <p>Real-world context: Database health monitoring is critical for any production
 * system. This delegate simulates checking a connection pool (e.g., HikariCP) for
 * available connections, running a lightweight query (SELECT 1) to measure latency,
 * and verifying that the connection count hasn't reached the pool maximum (which
 * would indicate connection leaks or excessive load).</p>
 *
 * <p>Process variables set:</p>
 * <ul>
 *   <li>{@code dbStatus} - Database health (HEALTHY or DEGRADED)</li>
 *   <li>{@code activeConnections} - Current active connections in the pool</li>
 *   <li>{@code dbQueryTimeMs} - Latency of the health check query</li>
 * </ul>
 */
@Component("checkDbStatusDelegate")
public class CheckDbStatusDelegate implements JavaDelegate {

    private static final Logger LOGGER = LoggerFactory.getLogger(CheckDbStatusDelegate.class);
    private static final int MAX_CONNECTIONS = 50;
    private static final int CONNECTION_WARNING_THRESHOLD = 40;

    @Override
    public void execute(final DelegateExecution execution) {
        final int activeConnections = ThreadLocalRandom.current().nextInt(5, 30);
        final int queryTimeMs = ThreadLocalRandom.current().nextInt(2, 25);
        final String dbStatus = activeConnections < CONNECTION_WARNING_THRESHOLD ? "HEALTHY" : "DEGRADED";

        LOGGER.info("Checking database status...");
        LOGGER.info("  Connection Pool: {}/{} active", activeConnections, MAX_CONNECTIONS);
        LOGGER.info("  Query Response Time: {}ms", queryTimeMs);
        LOGGER.info("  Database Status: {}", dbStatus);

        execution.setVariable("dbStatus", dbStatus);
        execution.setVariable("activeConnections", activeConnections);
        execution.setVariable("dbQueryTimeMs", queryTimeMs);
    }
}
