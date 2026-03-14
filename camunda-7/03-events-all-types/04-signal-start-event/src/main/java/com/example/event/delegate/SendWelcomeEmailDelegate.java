package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("sendWelcomeEmailDelegate")
public class SendWelcomeEmailDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(SendWelcomeEmailDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String employeeName = (String) execution.getVariable("employeeName");
        String department = (String) execution.getVariable("department");

        LOGGER.info("📧 [IT PROVISIONING] Sending welcome email");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Employee: " + (employeeName != null ? employeeName : "UNKNOWN"));
        LOGGER.info("   Department: " + (department != null ? department : "UNKNOWN"));
        LOGGER.info("   Status: Welcome email sent with login credentials and onboarding guide");
    }
}
