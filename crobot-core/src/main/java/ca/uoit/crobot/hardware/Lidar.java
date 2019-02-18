package ca.uoit.crobot.hardware;

import edu.wlu.cs.levy.breezyslam.components.Laser;

public interface Lidar extends Device {

    LidarScan scan();

    void shutdown();

    void rotate();

    void stopRotation();

    Laser getLaserConfig();

}
