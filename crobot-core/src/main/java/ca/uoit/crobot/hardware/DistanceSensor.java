package ca.uoit.crobot.hardware;

public interface DistanceSensor extends Device {

    /**
     * Takes a distance measurement
     *
     * @return The distance in CM
     */
    int measure();

}
