package com.example.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.event.model.AlertSeverity;
import com.example.event.model.SensorReading;

/**
 * Simulates integration with the Building Management System (BMS)
 * cooling controls.
 *
 * <p>In production, this would call the BMS API (e.g., Schneider Electric
 * EcoStruxure, Honeywell Niagara) to adjust CRAC/CRAH unit setpoints
 * or activate emergency cooling in the affected zone.</p>
 */
@Service
public class CoolingSystemService {

    private static final Logger LOG = LoggerFactory.getLogger(CoolingSystemService.class);

    /**
     * Activates emergency cooling for the zone containing the affected rack.
     * Only called for DANGER-level alerts.
     *
     * @param reading the sensor reading that triggered the alert
     * @return a confirmation reference from the BMS
     */
    public String activateEmergencyCooling(SensorReading reading) {
        String bmsRef = "BMS-EMG-" + System.currentTimeMillis();

        LOG.info(">>> BMS API CALL: Activating emergency cooling");
        LOG.info("    Zone    : {}", reading.getLocation());
        LOG.info("    Rack    : {}", reading.getRackId());
        LOG.info("    Temp    : {}°C", reading.getTemperatureCelsius());
        LOG.info("    BMS Ref : {}", bmsRef);
        LOG.info("    Action  : Set CRAC units to MAX, open hot-aisle containment vents");

        // In production: restTemplate.postForEntity(bmsApiUrl, request, BmsResponse.class)
        return bmsRef;
    }

    /**
     * Sends a warning-level notification to the operations team.
     * Called for WARNING-level alerts — no emergency action, just awareness.
     *
     * @param reading  the sensor reading
     * @param severity the classified severity
     */
    public void sendWarningNotification(SensorReading reading, AlertSeverity severity) {
        LOG.info(">>> EMAIL: Sending warning to dc-ops@company.com");
        LOG.info("    Subject : [{}] Temperature alert in {}",
                severity, reading.getLocation());
        LOG.info("    Body    : Sensor {} reported {}°C at rack {}. "
                        + "Current threshold: 27°C. Please investigate.",
                reading.getSensorId(),
                reading.getTemperatureCelsius(),
                reading.getRackId());

        // In production: emailService.send(opsTeamEmail, subject, body)
    }
}
