package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.sun.jna.*;
import edu.wlu.cs.levy.breezyslam.components.Laser;


public class X4Lidar implements Lidar {

    private static final int MOTOR_CTRL_PIN = 7;
    private static final int BAUD_RATE = 230400;

    private GpioPinDigitalOutput motorCtrl;
    private boolean isInitialized = false;

    static {
        Native.register("/home/pi/lidar/build/bindings/libydlidar_bindings.so");
    }

    @Override
    public synchronized LidarScan scan() {
        final Pointer ptr = scanLidar();

        int off = 0;

        final int scanSize = ptr.getInt(off);
        final boolean success = ptr.getByte(off += 17) != 0;

        if (!success)
            return null;

        float min_angle = ptr.getFloat(off += 4);
        float ang_increment = ptr.getFloat(off += 8);
        float[] ranges = ptr.getFloatArray(off + 20, scanSize);

        final float[] angles = new float[scanSize];

        for (int i = 0; i < scanSize; i++) {
            angles[i] = min_angle + i * ang_increment;
        }

        return new LidarScan(angles, ranges);
    }

    @Override
    public void shutdown() {
        turnOff();
        stopRotation();

        isInitialized = false;
    }

    @Override
    public void rotate() {
        if (!isInitialized)
            throw new IllegalStateException("not initialized");

        motorCtrl.setState(PinState.HIGH);
    }

    @Override
    public void stopRotation() {
        if (!isInitialized)
            throw new IllegalStateException("not initialized");

        motorCtrl.setState(PinState.LOW);
    }

    @Override
    public Laser getLaserConfig() {
        return new Laser(640, 7, 360, 10000, 0, 0);
    }

    @Override
    public void init() {
        isInitialized = init(BAUD_RATE);
        motorCtrl = GpioUtility.getDigitalOutput(MOTOR_CTRL_PIN);

        Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
    }

    public static native boolean init(int baud_rate);

    public static native void turnOff();

    public static native Pointer getSdkVersion();

    public static native Pointer scanLidar();

}
