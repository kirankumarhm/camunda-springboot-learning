package com.example.event;

import org.camunda.bpm.spring.boot.starter.annotation.EnableProcessApplication;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Camunda 7 + Spring Boot application.
 *
 * HOW THE TWO ANNOTATIONS WORK TOGETHER:
 *
 * 1. @SpringBootApplication
 *    - Enables Spring Boot auto-configuration (DataSource, web server, etc.)
 *    - Enables component scanning: Spring finds all @Component classes in this package
 *      and sub-packages (e.g., com.example.event.delegate.*) and registers them as beans.
 *    - This is how delegates like ErPreparationDelegate become available in the Spring context.
 *
 * 2. @EnableProcessApplication("signal-start-event-app")
 *    - Registers this Spring Boot app as a Camunda Process Application.
 *    - A Process Application is Camunda's concept of "a deployable unit that contains BPMN processes."
 *    - What it does at startup:
 *      a) Scans src/main/resources/ for .bpmn files
 *      b) Deploys them to the Camunda Process Engine (stored in the H2 database)
 *      c) Registers signal subscriptions — the engine now knows which processes listen to which signals
 *      d) Enables lifecycle callbacks (e.g., processes are undeployed when the app shuts down)
 *    - Without this annotation: BPMNs are still deployed (via auto-configuration), but you lose
 *      process application lifecycle management and the META-INF/processes.xml configuration.
 *
 * STARTUP SEQUENCE (what happens when you run this app):
 *   1. Spring Boot starts → creates DataSource → connects to H2 database
 *   2. Camunda auto-configuration kicks in → creates Process Engine
 *   3. Process Engine creates/updates database tables (ACT_RE_*, ACT_RU_*, ACT_HI_*)
 *   4. @EnableProcessApplication triggers BPMN deployment:
 *      - emergency-signal-broadcaster-process.bpmn  → deployed
 *      - emergency-er-preparation-process.bpmn      → deployed (subscribes to "EmergencyAlert")
 *      - emergency-security-lockdown-process.bpmn   → deployed (subscribes to "EmergencyAlert")
 *      - emergency-admin-notification-process.bpmn  → deployed (subscribes to "EmergencyAlert")
 *      - system-maintenance-cleanup-process.bpmn    → deployed (subscribes to "SystemMaintenanceActivated")
 *      - employee-it-provisioning-process.bpmn      → deployed (subscribes to "EmployeeOnboarded")
 *   5. REST API is available at /engine-rest/*
 *   6. Webapp (Cockpit/Tasklist/Admin) is available at /camunda/app/*
 *   7. App is ready to receive signal broadcasts
 */
@SpringBootApplication
@EnableProcessApplication("signal-start-event-app")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
