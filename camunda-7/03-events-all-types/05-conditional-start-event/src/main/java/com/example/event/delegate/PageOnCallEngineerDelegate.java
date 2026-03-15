package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.event.model.SensorReading;
import com.example.event.service.IncidentManagementService;

/**
 * DANGER path: Pages the on-call data center engineer via PagerDuty.
 *
 * <p>After emergency cooling is activated, the on-call engineer is paged
 * with full context: what happened, where, and what automated action
 * was already taken. The engineer can then physically inspect the
 * affected rack and determine root cause.</p>
 */
@Component("pageOnCallEngineerDelegate")
public class PageOnCallEngineerDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(PageOnCallEngineerDelegate.class);

    private final IncidentManagementService incidentService;

    public PageOnCallEngineerDelegate(IncidentManagementService incidentService) {
        this.incidentService = incidentService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        SensorReading reading = (SensorReading) execution.getVariable("sensorReading");
        String bmsRef = (String) execution.getVariable("bmsReference");

        LOG.info("=== DANGER PATH: Page On-Call Engineer ===");
        String incidentKey = incidentService.pageOnCallEngineer(reading, bmsRef);

        execution.setVariable("pagerDutyIncidentKey", incidentKey);
        execution.setVariable("actionTaken",
                "Emergency cooling activated (BMS: " + bmsRef
                        + "), on-call paged (PD: " + incidentKey + ")");
    }
}
