package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("logSecurityIncidentDelegate")
public class LogSecurityIncidentDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(LogSecurityIncidentDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("📝 [SECURITY LOCKDOWN] Logging security incident");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Status: Incident logged in security management system");
    }
}
