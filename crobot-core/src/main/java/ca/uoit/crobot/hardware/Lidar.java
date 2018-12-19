package ca.uoit.crobot.hardware;

public interface Lidar extends Device {

    LidarScan scan();

    void shutdown();

    void rotate();

    void stopRotation();

}
