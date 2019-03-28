package ca.uoit.crobot;

import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.task.PeriodicRobotTask;
import edu.wlu.cs.levy.breezyslam.algorithms.CoreSLAM;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

@EqualsAndHashCode(callSuper = true)
public class SLAMTask extends PeriodicRobotTask {

    public static final SLAMTask INSTANCE = new SLAMTask();

    private LidarScan lastScan;

    private SLAMTask() {
        super(1000 / 7 + 150, 1000 / 7);
    }

    @Override
    public void run(final @NonNull CRobot robot) {
        final CoreSLAM slamAlgorithm = robot.getSlamAlgorithm();
        final LidarScan scan = robot.getScan();

        if (scan != lastScan) {
            final PoseChange pc = robot.getDriveController().getPoseChange();
            final int size = robot.getLidar().getLaserConfig().getScanSize();
            long start = System.currentTimeMillis();

            if (pc.getDxyMm() > 0.001 || Math.abs(pc.getDthetaDegrees()) > 0.001) {
                slamAlgorithm.update(getRangesInMillimeters(size, scan), pc);
            } else {
                slamAlgorithm.update(getRangesInMillimeters(size, scan));
            }

            // System.out.println("Map Time: " + (System.currentTimeMillis() - start));

            lastScan = scan;
        }
    }

    private int[] getRangesInMillimeters(final int size, final LidarScan scan) {
        final int[] scanMM = new int[size];
        final float[] ranges = scan.getRanges();

        for (int i = 0, j = 0; i < scanMM.length && j < ranges.length; i++, j += 4) {
            if (ranges[j] < 0.25) {
                scanMM[i] = 0;
            } else {
                scanMM[i] = (int) (ranges[j] * 1000);
            }
        }

        return scanMM;
    }
}
