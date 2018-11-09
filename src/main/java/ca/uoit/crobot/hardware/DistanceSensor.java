package ca.uoit.crobot.hardware;

public interface DistanceSensor {

    /**
     * Takes a distance measurement
     *
     * @return The distance
     */
    int measure();

}
