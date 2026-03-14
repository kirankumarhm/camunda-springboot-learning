package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("adminNotificationDelegate")
public class AdminNotificationDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(AdminNotificationDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("📢 [ADMIN NOTIFICATION] Emergency Alert received");
        LOGGER.info("   Process Instance ID: " + processInstanceId);
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Action: Notifying chief medical officer, updating incident dashboard, paging administrators");
    }
}
