package ca.uoit.crobot;

import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.task.PeriodicRobotTask;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public final class ScanTask extends PeriodicRobotTask {

    public static final ScanTask INSTANCE = new ScanTask();

    private ScanTask() {
        super(100, 1000 / 7, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run(final @NonNull CRobot robot) {
        final LidarScan scan = robot.getLidar().scan();

        robot.setScan(scan);
    }
}
