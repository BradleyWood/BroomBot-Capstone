package ca.uoit.crobot.task;

import ca.uoit.crobot.CRobot;
import lombok.NonNull;

public interface RobotTask {

    void run(final @NonNull CRobot robot);
}
