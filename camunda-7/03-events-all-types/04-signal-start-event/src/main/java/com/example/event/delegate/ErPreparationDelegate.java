package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * RECEIVER DELEGATE — Prepares the ER when an emergency signal is received.
 *
 * HOW THIS DELEGATE GETS CALLED:
 *   1. Someone broadcasts "EmergencyAlert" signal (via REST API or Signal End Event)
 *   2. Camunda finds the subscription for emergency-er-preparation-process
 *   3. Camunda creates a NEW process instance starting from the Signal Start Event
 *   4. The process flows to the first service task: Task_PrepareEr
 *   5. Camunda resolves "${erPreparationDelegate}" → finds this Spring bean
 *   6. Camunda calls this execute() method
 *
 * ABOUT DelegateExecution:
 *   The DelegateExecution object is Camunda's runtime context for the current task.
 *   It provides:
 *     - execution.getProcessInstanceId()  → unique ID of this process instance
 *     - execution.getVariable("name")     → read a process variable
 *     - execution.setVariable("name", v)  → write a process variable (available to downstream tasks)
 *     - execution.getBusinessKey()        → business key (if set at start)
 *     - execution.getActivityId()         → current BPMN element ID ("Task_PrepareEr")
 *
 * PROCESS VARIABLE AVAILABILITY:
 *   When the signal is broadcast via REST API with variables:
 *     POST /engine-rest/signal {"name":"EmergencyAlert", "variables":{"severity":{"value":"CRITICAL"}}}
 *   Those variables are available in this delegate via execution.getVariable().
 *
 *   When the signal is broadcast via Signal End Event (from the sender process),
 *   the sender's process variables are NOT automatically passed to receiver processes.
 *   This is a Camunda 7 limitation — use the REST API approach if you need to pass data.
 */
@Component("erPreparationDelegate")
public class ErPreparationDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(ErPreparationDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String processInstanceId = execution.getProcessInstanceId();
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("🏥 [ER PREPARATION] Emergency Alert received");
        LOGGER.info("   Process Instance ID: " + processInstanceId);
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Action: Preparing trauma bay, alerting on-call surgeons, readying equipment");
    }
}
