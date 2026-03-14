package com.example.event.delegate;

import java.util.logging.Logger;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

/**
 * DOWNSTREAM DELEGATE (Back Task) — Notifies the on-call surgeon after ER is prepared.
 *
 * This is the SECOND service task in the emergency-er-preparation-process.
 * It runs AFTER ErPreparationDelegate completes.
 *
 * EXECUTION ORDER IN THE PROCESS:
 *   [📡 Signal Start Event]
 *       → [⚙️ ErPreparationDelegate]        ← first task (primary reaction)
 *       → [📟 NotifyOnCallSurgeonDelegate]   ← second task (this delegate — follow-up)
 *       → [● End Event]
 *
 * WHY A SEPARATE DELEGATE?
 *   Each service task in BPMN maps to one Java delegate. This keeps each delegate
 *   focused on a single responsibility (prepare ER vs. notify surgeon).
 *   Camunda executes them in sequence based on the BPMN sequence flows.
 *
 * VARIABLE SHARING:
 *   Both delegates in the same process instance share the same variable scope.
 *   Variables set by ErPreparationDelegate (via execution.setVariable()) are
 *   available here. Variables set at process start are also available.
 */
@Component("notifyOnCallSurgeonDelegate")
public class NotifyOnCallSurgeonDelegate implements JavaDelegate {

    private static final Logger LOGGER = Logger.getLogger(NotifyOnCallSurgeonDelegate.class.getName());

    @Override
    public void execute(DelegateExecution execution) {
        String severity = (String) execution.getVariable("severity");
        String location = (String) execution.getVariable("location");

        LOGGER.info("📟 [ER PREPARATION] Notifying on-call surgeon");
        LOGGER.info("   Process Instance ID: " + execution.getProcessInstanceId());
        LOGGER.info("   Severity: " + (severity != null ? severity : "UNKNOWN"));
        LOGGER.info("   Location: " + (location != null ? location : "UNKNOWN"));
        LOGGER.info("   Status: On-call surgeon paged and en route");
    }
}
