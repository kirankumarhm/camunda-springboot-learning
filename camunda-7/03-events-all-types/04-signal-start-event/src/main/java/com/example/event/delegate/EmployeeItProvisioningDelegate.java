package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component("employeeItProvisioningDelegate")
public class EmployeeItProvisioningDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(EmployeeItProvisioningDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String employeeName = (String) execution.getVariable("employeeName");
        String department = (String) execution.getVariable("department");

        LOGGER.info("💻 [IT PROVISIONING] New employee onboarded");
        LOGGER.info("   Process Instance ID: " + processInstanceId);
        LOGGER.info("   Employee: " + (employeeName != null ? employeeName : "UNKNOWN"));
        LOGGER.info("   Department: " + (department != null ? department : "UNKNOWN"));
        LOGGER.info("   Action: Creating email account, provisioning VPN access, assigning laptop, setting up AD groups");
    }
}
