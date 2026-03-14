package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * Simple logger delegate for testing and demonstration.
 * Logs process execution information to console.
 */
@Component("loggerDelegate")
public class LoggerDelegate implements JavaDelegate {

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String activityName = execution.getCurrentActivityName();
        
        System.out.println("✅ Process started with None Start Event");
        System.out.println("   Process Instance ID: " + processInstanceId);
        System.out.println("   Current Activity: " + activityName);
        
        // Log all variables
        execution.getVariables().forEach((key, value) -> 
            System.out.println("   Variable: " + key + " = " + value)
        );
    }
}
