package ca.uoit.crobot.task;

import ca.uoit.crobot.CRobot;
import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
public abstract @Data class NavigationTask implements RobotTask {

    private final int pollingRate;
    private final TimeUnit timeUnit;

    public NavigationTask() {
        this(100, TimeUnit.MILLISECONDS);
    }

    public abstract boolean activate(final @NonNull CRobot cRobot);

    public abstract boolean canInterrupt();

}
