package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.event.model.AlertSeverity;
import com.example.event.model.SensorReading;
import com.example.event.service.CoolingSystemService;

/**
 * WARNING path: Sends an email alert to the data center operations team.
 *
 * <p>For WARNING-level events (28–40°C), the response is non-emergency:
 * notify the ops team so they can investigate. No emergency cooling
 * activation, no paging.</p>
 */
@Component("sendWarningEmailDelegate")
public class SendWarningEmailDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(SendWarningEmailDelegate.class);

    private final CoolingSystemService coolingService;

    public SendWarningEmailDelegate(CoolingSystemService coolingService) {
        this.coolingService = coolingService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        SensorReading reading = (SensorReading) execution.getVariable("sensorReading");

        LOG.info("=== WARNING PATH: Send Email Alert ===");
        coolingService.sendWarningNotification(reading, AlertSeverity.WARNING);

        execution.setVariable("actionTaken", "Email alert sent to dc-ops@company.com");
    }
}
