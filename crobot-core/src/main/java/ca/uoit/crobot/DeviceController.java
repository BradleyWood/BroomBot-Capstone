package ca.uoit.crobot;

import ca.uoit.crobot.hardware.Device;
import ca.uoit.crobot.hardware.Lidar;
import ca.uoit.crobot.hardware.Motor;
import ca.uoit.crobot.odometry.Drive;
import lombok.Data;

import java.util.concurrent.TimeoutException;

public @Data class DeviceController {

    private final Motor leftMotor;
    private final Motor rightMotor;
    private final Lidar lidar;
    private Drive driveController;

    private boolean initialized = false;

    public void init() throws TimeoutException {
        parallelInit(leftMotor, rightMotor);
        lidar.stopRotation();
        driveController = new Drive(leftMotor, rightMotor);
        initialized = true;
    }

    /**
     * Stop all devices
     */
    public void stop() {
        leftMotor.stop();
        rightMotor.stop();
        lidar.stopRotation();
    }

    /**
     * Initialize a set devices in parallel to save time.
     *
     * @param devices The set of devices to initialize
     * @throws TimeoutException if a device fails to initialize within the timelimit
     */
    private void parallelInit(final Device... devices) throws TimeoutException {
        final Thread[] threads = new Thread[devices.length];

        for (int i = 0; i < devices.length; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> devices[idx].init());
            threads[i].start();
        }

        final long timeout = 10000;
        final long start = System.currentTimeMillis();

        for (int j = 0; j < threads.length; j++) {
            try {
                threads[j].join(timeout);
            } catch (InterruptedException e) {
                j--;
            }
        }

        if (System.currentTimeMillis() - start > timeout) {
            throw new TimeoutException();
        }
    }
}
