package ca.uoit.crobot.task;

import lombok.Data;

import java.util.concurrent.TimeUnit;

public abstract @Data class PeriodicRobotTask implements RobotTask {

    private final int offset;
    private final int period;
    private final TimeUnit timeUnit;

    public PeriodicRobotTask(final int offset, final int period, final TimeUnit timeUnit) {
        this.offset = offset;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    public PeriodicRobotTask(final int offset, final int period) {
        this(offset, period, TimeUnit.MILLISECONDS);
    }
}
