package com.example.event.service;

import java.time.Instant;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.event.model.SensorReading;

/**
 * Simulates a sensor registry / CMDB lookup.
 *
 * <p>In production, this would query a Configuration Management Database
 * (e.g., ServiceNow CMDB, NetBox) to resolve sensor IDs to physical
 * locations, rack assignments, and responsible teams.</p>
 */
@Service
public class SensorRegistryService {

    private static final Logger LOG = LoggerFactory.getLogger(SensorRegistryService.class);

    /** Simulated sensor registry — maps sensorId to known location. */
    private static final Map<String, String> SENSOR_LOCATIONS = Map.of(
            "SENSOR-DC1-A3-01", "Data Center 1 — Hall A, Row 3",
            "SENSOR-DC1-B2-07", "Data Center 1 — Hall B, Row 2",
            "SENSOR-DC2-C1-03", "Data Center 2 — Hall C, Row 1"
    );

    private static final Map<String, String> SENSOR_RACKS = Map.of(
            "SENSOR-DC1-A3-01", "RACK-A3-01",
            "SENSOR-DC1-B2-07", "RACK-B2-07",
            "SENSOR-DC2-C1-03", "RACK-C1-03"
    );

    /**
     * Builds a {@link SensorReading} from raw process variables,
     * enriching with registry data when available.
     */
    public SensorReading buildReading(Integer temperature, String sensorId, String location) {
        String resolvedLocation = resolveLocation(sensorId, location);
        String resolvedRack = resolveRack(sensorId);

        SensorReading reading = new SensorReading(
                sensorId != null ? sensorId : "UNREGISTERED",
                resolvedLocation,
                temperature,
                resolvedRack
        );
        reading.setTimestamp(Instant.now());

        LOG.info("Sensor reading built: {}", reading);
        return reading;
    }

    /**
     * Validates that the temperature is within a physically plausible range.
     * Catches sensor malfunctions (e.g., broken thermistor reporting -999 or 999).
     */
    public boolean isPlausibleReading(int temperatureCelsius) {
        return temperatureCelsius >= -40 && temperatureCelsius <= 150;
    }

    private String resolveLocation(String sensorId, String fallbackLocation) {
        if (sensorId != null && SENSOR_LOCATIONS.containsKey(sensorId)) {
            return SENSOR_LOCATIONS.get(sensorId);
        }
        return fallbackLocation != null ? fallbackLocation : "Unknown Location";
    }

    private String resolveRack(String sensorId) {
        if (sensorId != null && SENSOR_RACKS.containsKey(sensorId)) {
            return SENSOR_RACKS.get(sensorId);
        }
        return "UNASSIGNED";
    }
}
