package com.example.event.model;

import java.io.Serializable;
import java.time.Instant;

/**
 * Represents a temperature reading from a data center IoT sensor.
 *
 * <p>In production, this would be populated from an MQTT broker or
 * a REST webhook from the sensor gateway (e.g., Schneider Electric
 * EcoStruxure, Vertiv Liebert).</p>
 */
public class SensorReading implements Serializable {

    private static final long serialVersionUID = 1L;

    private String sensorId;
    private String location;
    private int temperatureCelsius;
    private Instant timestamp;
    private String rackId;

    public SensorReading() {
    }

    public SensorReading(String sensorId, String location,
                         int temperatureCelsius, String rackId) {
        this.sensorId = sensorId;
        this.location = location;
        this.temperatureCelsius = temperatureCelsius;
        this.rackId = rackId;
        this.timestamp = Instant.now();
    }

    public String getSensorId() {
        return sensorId;
    }

    public void setSensorId(String sensorId) {
        this.sensorId = sensorId;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getTemperatureCelsius() {
        return temperatureCelsius;
    }

    public void setTemperatureCelsius(int temperatureCelsius) {
        this.temperatureCelsius = temperatureCelsius;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public String getRackId() {
        return rackId;
    }

    public void setRackId(String rackId) {
        this.rackId = rackId;
    }

    @Override
    public String toString() {
        return "SensorReading{sensorId='" + sensorId + "', location='" + location
                + "', temp=" + temperatureCelsius + "°C, rack='" + rackId
                + "', time=" + timestamp + "}";
    }
}
