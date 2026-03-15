package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.event.model.SensorReading;
import com.example.event.service.CoolingSystemService;

/**
 * DANGER path: Activates emergency cooling via the Building Management System.
 *
 * <p>For DANGER-level events (above 40°C), the BMS is instructed to set
 * all CRAC units in the affected zone to maximum output and open the
 * hot-aisle containment vents. This is an automated response — no human
 * approval needed at this stage.</p>
 */
@Component("activateEmergencyCoolingDelegate")
public class ActivateEmergencyCoolingDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ActivateEmergencyCoolingDelegate.class);

    private final CoolingSystemService coolingService;

    public ActivateEmergencyCoolingDelegate(CoolingSystemService coolingService) {
        this.coolingService = coolingService;
    }

    @Override
    public void execute(DelegateExecution execution) {
        SensorReading reading = (SensorReading) execution.getVariable("sensorReading");

        LOG.info("=== DANGER PATH: Activate Emergency Cooling ===");
        String bmsRef = coolingService.activateEmergencyCooling(reading);

        execution.setVariable("bmsReference", bmsRef);
        execution.setVariable("emergencyCoolingActivated", true);
    }
}
