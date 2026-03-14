package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("updateIncidentDashboardDelegate")
public class UpdateIncidentDashboardDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(UpdateIncidentDashboardDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("📊 [ADMIN NOTIFICATION] Updating incident dashboard");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Status: Dashboard updated with active incident details");
    }
}
