package com.example.event.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * JPA entity representing a persisted temperature alert record.
 *
 * <p>Stored in the {@code alert_records} table (H2 in dev, PostgreSQL
 * in production). Each row is an immutable audit record created by
 * the back task ({@code RecordAuditTrailDelegate}) after the alert
 * has been fully processed.</p>
 *
 * <p>This satisfies ISO 27001 A.11.1.4 (protection against environmental
 * threats) by providing a queryable, tamper-evident log of every
 * temperature exceedance and the automated response taken.</p>
 */
@Entity
@Table(name = "alert_records")
public class AlertRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String auditId;

    @Column(nullable = false)
    private String sensorId;

    private String location;

    private String rackId;

    @Column(nullable = false)
    private int temperatureCelsius;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertSeverity severity;

    @Column(nullable = false)
    private String actionTaken;

    @Column(nullable = false)
    private Instant recordedAt;

    /** Camunda process instance ID for traceability. */
    private String processInstanceId;

    protected AlertRecord() {
        // JPA requires a no-arg constructor
    }

    public AlertRecord(String auditId, String sensorId, String location,
                       String rackId, int temperatureCelsius,
                       AlertSeverity severity, String actionTaken,
                       String processInstanceId) {
        this.auditId = auditId;
        this.sensorId = sensorId;
        this.location = location;
        this.rackId = rackId;
        this.temperatureCelsius = temperatureCelsius;
        this.severity = severity;
        this.actionTaken = actionTaken;
        this.processInstanceId = processInstanceId;
        this.recordedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getAuditId() {
        return auditId;
    }

    public String getSensorId() {
        return sensorId;
    }

    public String getLocation() {
        return location;
    }

    public String getRackId() {
        return rackId;
    }

    public int getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public Instant getRecordedAt() {
        return recordedAt;
    }

    public String getProcessInstanceId() {
        return processInstanceId;
    }

    @Override
    public String toString() {
        return "AlertRecord{auditId='" + auditId + "', sensor='" + sensorId
                + "', temp=" + temperatureCelsius + "°C, severity=" + severity
                + ", action='" + actionTaken + "'}";
    }
}
