package ca.uoit.crobot;

import ca.uoit.crobot.task.NavigationTask;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public class ForwardTask extends NavigationTask {

    public static final ForwardTask INSTANCE = new ForwardTask();

    private ForwardTask() {
        super(500, TimeUnit.MILLISECONDS);
    }

    @Override
    public boolean activate(final @NonNull CRobot cRobot) {
        return true;
    }

    @Override
    public boolean canInterrupt() {
        return true;
    }

    @Override
    public void run(final @NonNull CRobot robot) {
        robot.getDriveController().drive(30);
    }
}
