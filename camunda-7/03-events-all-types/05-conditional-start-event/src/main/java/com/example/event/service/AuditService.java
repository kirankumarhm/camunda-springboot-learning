package com.example.event.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.example.event.model.AlertRecord;
import com.example.event.model.AlertSeverity;
import com.example.event.model.SensorReading;
import com.example.event.repository.AlertRecordRepository;

/**
 * Persists audit records for compliance.
 *
 * <p>Each completed alert — whether WARNING or DANGER — is written to
 * the {@code alert_records} table via JPA. In production, this data
 * would also be forwarded to a SIEM system (e.g., Splunk, Elastic SIEM)
 * for ISO 27001 / SOC 2 environmental monitoring evidence.</p>
 */
@Service
public class AuditService {

    private static final Logger LOG = LoggerFactory.getLogger(AuditService.class);

    private final AlertRecordRepository alertRecordRepository;

    public AuditService(AlertRecordRepository alertRecordRepository) {
        this.alertRecordRepository = alertRecordRepository;
    }

    /**
     * Persists a completed alert as an audit record.
     *
     * @param reading           the original sensor reading
     * @param severity          the classified severity
     * @param actionTaken       description of the response action
     * @param processInstanceId the Camunda process instance ID
     * @return the generated audit record ID
     */
    public String recordAlert(SensorReading reading, AlertSeverity severity,
                              String actionTaken, String processInstanceId) {
        String auditId = "AUD-" + System.currentTimeMillis();

        AlertRecord record = new AlertRecord(
                auditId,
                reading.getSensorId(),
                reading.getLocation(),
                reading.getRackId(),
                reading.getTemperatureCelsius(),
                severity,
                actionTaken,
                processInstanceId
        );

        alertRecordRepository.save(record);

        LOG.info("AUDIT: Persisted alert record {}", record);
        LOG.info("    Compliance: ISO 27001 A.11.1.4 (Environmental threats)");

        return auditId;
    }
}
