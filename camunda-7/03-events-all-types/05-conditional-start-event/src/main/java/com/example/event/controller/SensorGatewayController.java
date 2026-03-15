package com.example.event.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.event.model.AlertRecord;
import com.example.event.model.AlertSeverity;
import com.example.event.repository.AlertRecordRepository;

/**
 * REST controller acting as the IoT sensor gateway endpoint.
 *
 * <p>In a real data center, IoT sensors publish readings via MQTT to a
 * broker. A gateway microservice consumes those messages and forwards
 * them here. This controller accepts the reading, converts it into
 * Camunda process variables, and triggers the conditional start event
 * via {@code runtimeService.createConditionEvaluation()}.</p>
 *
 * <p>This replaces the need to call Camunda's internal
 * {@code POST /engine-rest/condition} directly — the caller doesn't
 * need to know about Camunda at all.</p>
 */
@RestController
@RequestMapping("/api/sensor")
public class SensorGatewayController {

    private static final Logger LOG = LoggerFactory.getLogger(SensorGatewayController.class);

    private final RuntimeService runtimeService;
    private final AlertRecordRepository alertRecordRepository;

    public SensorGatewayController(RuntimeService runtimeService,
                                   AlertRecordRepository alertRecordRepository) {
        this.runtimeService = runtimeService;
        this.alertRecordRepository = alertRecordRepository;
    }

    /**
     * Accepts a sensor reading from the IoT gateway.
     *
     * <p>If the temperature exceeds the ASHRAE threshold (27°C), Camunda's
     * condition evaluation triggers the {@code datacenter-temperature-alert}
     * process. If not, no process starts — the reading is simply acknowledged.</p>
     *
     * <h3>Example request:</h3>
     * <pre>
     * POST /api/sensor/readings
     * {
     *   "sensorId": "SENSOR-DC1-A3-01",
     *   "temperature": 48,
     *   "location": "Data Center 1 — Hall A, Row 3"
     * }
     * </pre>
     *
     * @param request the sensor reading payload
     * @return acknowledgement with trigger status
     */
    @PostMapping("/readings")
    public ResponseEntity<Map<String, Object>> submitReading(
            @RequestBody SensorReadingRequest request) {

        LOG.info("Received sensor reading: sensorId={}, temperature={}°C, location={}",
                request.sensorId(), request.temperature(), request.location());

        Map<String, Object> variables = new HashMap<>();
        variables.put("temperature", request.temperature());
        if (request.sensorId() != null) {
            variables.put("sensorId", request.sensorId());
        }
        if (request.location() != null) {
            variables.put("location", request.location());
        }

        // Trigger Camunda's condition evaluation — this is what fires
        // the Conditional Start Event if temperature > 27
        List<org.camunda.bpm.engine.runtime.ProcessInstance> instances =
                runtimeService.createConditionEvaluation()
                        .setVariables(variables)
                        .evaluateStartConditions();

        Map<String, Object> response = new HashMap<>();
        response.put("received", true);
        response.put("temperature", request.temperature());
        response.put("sensorId", request.sensorId());

        if (instances.isEmpty()) {
            response.put("alertTriggered", false);
            response.put("reason", "Temperature " + request.temperature()
                    + "°C is within normal range (≤27°C)");
        } else {
            response.put("alertTriggered", true);
            response.put("processInstanceId", instances.get(0).getId());
            response.put("severity",
                    AlertSeverity.fromTemperature(request.temperature()).name());
        }

        return ResponseEntity.ok(response);
    }

    /**
     * Returns all persisted alert records, most recent first.
     *
     * <p>Optionally filter by severity (WARNING or DANGER).</p>
     *
     * @param severity optional severity filter
     * @return list of alert records
     */
    @GetMapping("/alerts")
    public ResponseEntity<List<AlertRecord>> getAlerts(
            @RequestParam(required = false) AlertSeverity severity) {

        List<AlertRecord> records;
        if (severity != null) {
            records = alertRecordRepository.findBySeverity(severity);
        } else {
            records = alertRecordRepository.findAllByOrderByRecordedAtDesc();
        }
        return ResponseEntity.ok(records);
    }

    /**
     * Simple JSON request body for sensor readings.
     * Using a record keeps it minimal — no getters/setters needed.
     */
    public record SensorReadingRequest(
            String sensorId,
            Integer temperature,
            String location
    ) {}
}
