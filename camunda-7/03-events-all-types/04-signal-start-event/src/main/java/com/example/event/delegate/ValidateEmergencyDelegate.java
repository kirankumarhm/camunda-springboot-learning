package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * SENDER DELEGATE — Validates emergency details before the signal is broadcast.
 *
 * HOW CAMUNDA FINDS THIS CLASS:
 *   1. The BPMN file (emergency-signal-broadcaster-process.bpmn) has a service task with:
 *        camunda:delegateExpression="${validateEmergencyDelegate}"
 *
 *   2. At runtime, when the process reaches this service task, Camunda evaluates
 *      the expression "${validateEmergencyDelegate}" using Spring's Expression Language (EL).
 *
 *   3. Spring looks for a bean named "validateEmergencyDelegate" in its application context.
 *      This class is registered as that bean because of:
 *        @Component("validateEmergencyDelegate")
 *
 *   4. Camunda calls the execute() method, passing a DelegateExecution object that provides
 *      access to process variables, process instance ID, and other runtime context.
 *
 * WHAT HAPPENS AFTER THIS DELEGATE RUNS:
 *   The process continues to the next element in the BPMN — which is a Signal End Event.
 *   The Signal End Event broadcasts "EmergencyAlert" to all listening processes.
 *   This delegate does NOT send the signal — the BPMN engine handles that automatically.
 *
 * PROCESS VARIABLES:
 *   Variables are set when the process is started via REST API:
 *     POST /engine-rest/process-definition/key/emergency-signal-broadcaster-process/start
 *     { "variables": { "severity": {"value": "CRITICAL", "type": "String"}, ... } }
 *   They are stored in the Camunda database (ACT_RU_VARIABLE table) and accessible
 *   via execution.getVariable("variableName").
 */
@Component("validateEmergencyDelegate")
public class ValidateEmergencyDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(ValidateEmergencyDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        // Read process variables set when the process was started
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("🚨 [EMERGENCY BROADCASTER] Validating emergency details");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Status: Validated — broadcasting EmergencyAlert signal to all listeners");
    }
}
