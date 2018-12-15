package ca.uoit.crobot.hardware;

public class LidarScan {

    private final float[] angles;
    private final float[] ranges;

    public LidarScan(final float[] angles, final float[] ranges) {
        this.angles = angles;
        this.ranges = ranges;
    }

    public float[] getRanges() {
        return ranges;
    }

    public float[] getAngles() {
        return angles;
    }
}
