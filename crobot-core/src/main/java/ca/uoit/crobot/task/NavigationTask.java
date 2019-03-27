package ca.uoit.crobot.task;

import ca.uoit.crobot.CRobot;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract @Data
class NavigationTask implements RobotTask {

    private final int pollingRate;
    private final TimeUnit timeUnit;

    public NavigationTask() {
        this(100, TimeUnit.MILLISECONDS);
    }

    public void init(final @NonNull CRobot robot) {
    }

    public abstract boolean activate(final @NonNull CRobot robot);

    public abstract boolean canInterrupt();

    public void onInterrupt(final @NonNull CRobot cRobot) {
    }
}
