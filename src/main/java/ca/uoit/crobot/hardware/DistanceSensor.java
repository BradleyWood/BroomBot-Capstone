package ca.uoit.crobot.hardware;

public interface DistanceSensor {

    /**
     * Takes a distance measurement
     *
     * @return The distance in CM
     */
    int measure();

    /**
     * Initialize the device
     */
    void init();

}
