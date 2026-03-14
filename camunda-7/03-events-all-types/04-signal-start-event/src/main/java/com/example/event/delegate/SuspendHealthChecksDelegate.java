package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("suspendHealthChecksDelegate")
public class SuspendHealthChecksDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(SuspendHealthChecksDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String maintenanceWindow = (String) execution.getVariable("maintenanceWindow");
        String initiatedBy = (String) execution.getVariable("initiatedBy");

        LOGGER.info("⏸️ [SYSTEM MAINTENANCE] Suspending external health checks");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Maintenance Window: " + (maintenanceWindow != null ? maintenanceWindow : "UNSCHEDULED"));
        LOGGER.info("   Initiated By: " + (initiatedBy != null ? initiatedBy : "SYSTEM"));
        LOGGER.info("   Status: Health check endpoints suspended to avoid false alerts");
    }
}
