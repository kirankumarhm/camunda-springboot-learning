package com.example.event.model;

/**
 * Severity levels for data center environmental alerts.
 *
 * <p>Thresholds are based on ASHRAE TC 9.9 recommended operating
 * ranges for data center equipment:</p>
 * <ul>
 *   <li>NORMAL: 18–27°C (within recommended range)</li>
 *   <li>WARNING: 28–40°C (allowable but requires attention)</li>
 *   <li>DANGER: above 40°C (equipment damage risk, immediate action)</li>
 * </ul>
 */
public enum AlertSeverity {

    NORMAL("No action required"),
    WARNING("Attention required — notify operations team via email"),
    DANGER("Immediate action — activate emergency cooling and page on-call engineer");

    private final String responseAction;

    AlertSeverity(String responseAction) {
        this.responseAction = responseAction;
    }

    public String getResponseAction() {
        return responseAction;
    }

    /**
     * Classifies temperature into severity based on ASHRAE thresholds.
     *
     * @param temperatureCelsius the measured temperature
     * @return the corresponding severity level
     */
    public static AlertSeverity fromTemperature(int temperatureCelsius) {
        if (temperatureCelsius > 40) {
            return DANGER;
        }
        if (temperatureCelsius > 27) {
            return WARNING;
        }
        return NORMAL;
    }
}
