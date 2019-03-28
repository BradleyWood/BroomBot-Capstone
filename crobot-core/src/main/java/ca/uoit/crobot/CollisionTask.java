package ca.uoit.crobot;

import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.task.NavigationTask;
import lombok.NonNull;

public final class CollisionTask extends NavigationTask {

    private enum DECISION {
        LEFT,
        RIGHT,
        NOTHING
    }

    private LidarScan lastScan = null;
    private DECISION decision = DECISION.NOTHING;
    private DECISION prevDecision = DECISION.NOTHING;
    private long lastDecision = 0;

    private CollisionTask() {
    }

    @Override
    public boolean activate(final @NonNull CRobot robot) {
        makeDecision(robot);

        return decision != DECISION.NOTHING;
    }

    @Override
    public void run(final @NonNull CRobot robot) {

        DECISION curDecision;

        if(prevDecision == DECISION.RIGHT) {
            curDecision = DECISION.LEFT;
        } else if(prevDecision == DECISION.LEFT) {
            curDecision = DECISION.RIGHT;
        } else {
            curDecision = decision;
        }

        prevDecision = curDecision;

        try {
            // Backup
            robot.getDriveController().driveToDistance(-20, 100);

            // Turn
            if (curDecision == DECISION.RIGHT) {
                robot.getDriveController().turnToAngle(20, 90);
            } else {
                robot.getDriveController().turnToAngle(-20, 90);
            }

            // Check if something is in front of the robot
            makeDecision(robot);

            // If there is nothing in front of the robot, drive forward
            if(decision == DECISION.NOTHING) {
                robot.getDriveController().driveToDistance(20, 200);
            }

            // Turn again
            if (curDecision == DECISION.RIGHT) {
                robot.getDriveController().turnToAngle(20, 90);
            } else {
                robot.getDriveController().turnToAngle(-20, 90);
            }

        } catch (InterruptedException e) {

        }

        robot.getDriveController().stop();
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }

    private int countPoints(final LidarScan scan, final double minRange, final double minAngle, final double maxAngle) {
        final float[] angles = scan.getAngles();
        final float[] ranges = scan.getRanges();

        int count = 0;

        for (int i = 0; i < ranges.length; i++) {
            if (angles[i] >= minAngle && angles[i] <= maxAngle && ranges[i] < minRange && ranges[i] > 0.01) {
                count++;
            }
        }

        return count;
    }

    private void makeDecision(final @NonNull CRobot robot) {
        final LidarScan scan = robot.getScan();

        if (scan == null) {
            decision = DECISION.NOTHING;
            return;
        }

        if (scan == lastScan)
            return;

        lastScan = scan;

        int right = countPoints(scan, 0.30, Math.PI / 1.7, Math.PI);
        int left = countPoints(scan, 0.30, -Math.PI, -Math.PI / 1.7);

        if (left + right > 8 || left > 5 || right > 5) {
            if (System.currentTimeMillis() - lastDecision > 2000) {
                if (left > right) {
                    decision = DECISION.RIGHT;
                } else {
                    decision = DECISION.LEFT;
                }
                lastDecision = System.currentTimeMillis();
            }
        } else {
            decision = DECISION.NOTHING;
        }
    }

    public static final CollisionTask INSTANCE = new CollisionTask();

}
