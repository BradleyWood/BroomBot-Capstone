package ca.uoit.crobot.hardware;

public interface Lidar extends Device {

    boolean isHealthy();

    LidarScan scan();

    void shutdown();

}
