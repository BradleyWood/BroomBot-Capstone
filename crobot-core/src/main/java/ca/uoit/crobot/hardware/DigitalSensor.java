package ca.uoit.crobot.hardware;

public interface DigitalSensor extends Device {

    /**
     * Get the status of the sensor
     *
     * @return The status of the sensor. true for HIGH, false for LOW
     */
    boolean get();
}
