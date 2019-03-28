package ca.uoit.crobot;

import ca.uoit.crobot.task.NavigationTask;
import lombok.NonNull;

public class DropTask extends NavigationTask {

    public static final DropTask INSTANCE = new DropTask();

    private DropTask() {
    }

    @Override
    public boolean activate(@NonNull CRobot robot) {
        return robot.getDropSensor().get();
    }

    @Override
    public boolean canInterrupt() {
        return false;
    }

    @Override
    public void run(@NonNull CRobot robot) {
        System.out.println("DropTask");
        try {
            robot.getDriveController().driveToDistance(-15, 100);
            robot.getDriveController().turnToAngle(15, 90);
        } catch (InterruptedException e) {
        }
    }
}
