package com.example.event.delegate;

import org.camunda.bpm.engine.delegate.BpmnError;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.example.event.model.AlertSeverity;
import com.example.event.model.SensorReading;
import com.example.event.service.SensorRegistryService;

/**
 * Front task: Validates the incoming sensor data and classifies severity.
 *
 * <p>This is the first task after the Conditional Start Event fires.
 * It catches bad data (sensor malfunctions, null values) before the
 * process does any real work, and enriches the raw variables into a
 * proper {@link SensorReading} domain object.</p>
 */
@Component("validateSensorReadingDelegate")
public class ValidateSensorReadingDelegate implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(ValidateSensorReadingDelegate.class);

    private final SensorRegistryService sensorRegistry;

    public ValidateSensorReadingDelegate(SensorRegistryService sensorRegistry) {
        this.sensorRegistry = sensorRegistry;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Integer temperature = (Integer) execution.getVariable("temperature");
        String sensorId = (String) execution.getVariable("sensorId");
        String location = (String) execution.getVariable("location");

        LOG.info("=== VALIDATE SENSOR READING ===");

        if (temperature == null) {
            throw new BpmnError("INVALID_READING", "Temperature value is null");
        }

        if (!sensorRegistry.isPlausibleReading(temperature)) {
            throw new BpmnError("SENSOR_MALFUNCTION",
                    "Implausible reading: " + temperature
                            + "°C — possible sensor hardware failure");
        }

        SensorReading reading = sensorRegistry.buildReading(temperature, sensorId, location);
        AlertSeverity severity = AlertSeverity.fromTemperature(temperature);

        LOG.info("Severity classified: {} — {}", severity, severity.getResponseAction());

        // Store enriched data for downstream tasks
        execution.setVariable("sensorReading", reading);
        execution.setVariable("severity", severity.name());
        execution.setVariable("alertTimestamp", reading.getTimestamp().toString());
    }
}
