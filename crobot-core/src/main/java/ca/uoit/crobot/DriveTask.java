package ca.uoit.crobot;

import ca.uoit.crobot.task.NavigationTask;
import lombok.NonNull;

import java.util.concurrent.TimeUnit;

public class DriveTask extends NavigationTask {

    public static final DriveTask INSTANCE = new DriveTask();
    private static final int MAP_SIZE = 1000;

    private boolean[][] visited;

    private DriveTask() {
        super(1000 / 7 + 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void init(final @NonNull CRobot robot) {
        visited = new boolean[MAP_SIZE][MAP_SIZE];
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

    }
}
