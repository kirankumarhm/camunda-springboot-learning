package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.event.model.AlertSeverity;
import com.example.event.model.SensorReading;
import com.example.event.service.AuditService;

/**
 * Back task: Records the completed alert in the audit trail.
 *
 * <p>This is the last task before the process ends. Both the WARNING
 * and DANGER paths converge here. It writes a compliance-ready audit
 * record that can be used for ISO 27001 / SOC 2 evidence.</p>
 */
@Component("recordAuditTrailDelegate")
public class RecordAuditTrailDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(RecordAuditTrailDelegate.class);

    private final AuditService auditService;

    public RecordAuditTrailDelegate(AuditService auditService) {
        this.auditService = auditService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        SensorReading reading = (SensorReading) execution.getVariable("sensorReading");
        String severityName = (String) execution.getVariable("severity");
        String actionTaken = (String) execution.getVariable("actionTaken");

        AlertSeverity severity = AlertSeverity.valueOf(severityName);

        LOG.info("=== RECORD AUDIT TRAIL ===");
        String auditId = auditService.recordAlert(reading, severity, actionTaken,
                execution.getProcessInstanceId());

        execution.setVariable("auditId", auditId);
        execution.setVariable("alertStatus", "CLOSED");

        LOG.info("Process complete. Alert {} — audit record {}.", severityName, auditId);
    }
}
