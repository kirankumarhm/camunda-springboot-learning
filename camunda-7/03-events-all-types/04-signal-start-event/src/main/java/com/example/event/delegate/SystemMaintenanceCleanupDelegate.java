package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("systemMaintenanceCleanupDelegate")
public class SystemMaintenanceCleanupDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(SystemMaintenanceCleanupDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String maintenanceWindow = (String) execution.getVariable("maintenanceWindow");
        String initiatedBy = (String) execution.getVariable("initiatedBy");

        LOGGER.info("🔧 [SYSTEM MAINTENANCE] Maintenance mode activated");
        LOGGER.info("   Process Instance ID: " + processInstanceId);
        LOGGER.info("   Maintenance Window: " + (maintenanceWindow != null ? maintenanceWindow : "UNSCHEDULED"));
        LOGGER.info("   Initiated By: " + (initiatedBy != null ? initiatedBy : "SYSTEM"));
        LOGGER.info("   Action: Flushing caches, clearing expired sessions, suspending health checks");
    }
}
