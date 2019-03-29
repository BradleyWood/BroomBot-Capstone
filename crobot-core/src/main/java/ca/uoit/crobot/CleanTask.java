package ca.uoit.crobot;

import ca.uoit.crobot.task.PeriodicRobotTask;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public class CleanTask extends PeriodicRobotTask {

    public static final CleanTask INSTANCE = new CleanTask();

    private CleanTask() {
        super(0, 10000, TimeUnit.MILLISECONDS);
    }

    @Override
    public void run(final @NonNull CRobot robot) {
        robot.getBrushMotor().setSpeed(75);
    }
}
