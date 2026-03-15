package com.example.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Data Center Environmental Monitoring — Conditional Start Event Example.
 *
 * <p>Demonstrates a real-world use of BPMN Conditional Start Events using
 * Camunda 7 with Spring Boot. An IoT sensor gateway publishes temperature
 * readings; when a reading exceeds the ASHRAE threshold, this process
 * validates the data, routes by severity (WARNING vs DANGER), takes
 * appropriate action, and records an audit trail.</p>
 *
 * @see <a href="https://docs.camunda.org/manual/latest/reference/bpmn20/events/conditional-events/">
 *     Camunda Conditional Events</a>
 */
@SpringBootApplication
public class Application {

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        LOG.info("=== Data Center Temperature Alert Process Ready ===");
        LOG.info("Cockpit    : http://localhost:8080/camunda/app/cockpit (demo/demo)");
        LOG.info("Submit     : POST http://localhost:8080/api/sensor/readings");
        LOG.info("Alerts     : GET  http://localhost:8080/api/sensor/alerts");
        LOG.info("H2 Console : http://localhost:8080/h2-console");
    }
}
