package com.example.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.event.model.SensorReading;

/**
 * Simulates integration with an incident management / on-call paging system.
 *
 * <p>In production, this would call the PagerDuty Events API v2 or
 * Opsgenie Alert API to page the on-call data center engineer.</p>
 */
@Service
public class IncidentManagementService {

    private static final Logger LOG = LoggerFactory.getLogger(IncidentManagementService.class);

    private static final String ON_CALL_SCHEDULE = "DC-FACILITIES-PRIMARY";

    /**
     * Pages the on-call engineer for a DANGER-level temperature event.
     *
     * @param reading the sensor reading
     * @param bmsRef  the BMS emergency cooling reference
     * @return the PagerDuty incident key
     */
    public String pageOnCallEngineer(SensorReading reading, String bmsRef) {
        String incidentKey = "PD-" + System.currentTimeMillis();

        LOG.info(">>> PAGERDUTY API: Creating P1 incident");
        LOG.info("    Incident Key : {}", incidentKey);
        LOG.info("    Schedule     : {}", ON_CALL_SCHEDULE);
        LOG.info("    Summary      : DANGER: {}°C at {} (rack {})",
                reading.getTemperatureCelsius(),
                reading.getLocation(),
                reading.getRackId());
        LOG.info("    BMS Ref      : {} (emergency cooling already activated)", bmsRef);
        LOG.info("    Urgency      : HIGH");

        // In production: pagerDutyClient.triggerIncident(event)
        return incidentKey;
    }
}
