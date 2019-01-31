package ca.uoit.crobot;


import ca.uoit.crobot.hardware.AdafruitDCMotor;
import ca.uoit.crobot.odometry.Drive;

public class Application {

    public static void main(String[] args) {
        Drive drive = new Drive(AdafruitDCMotor.MOTOR1, AdafruitDCMotor.MOTOR2);

        drive.init();

        drive.driveDistance(25, 10);
        drive.turnLeft(25, 90);

        drive.driveDistance(25, 10);
        drive.turnLeft(25, 90);

        drive.driveDistance(25, 10);
        drive.turnLeft(25, 90);

        drive.driveDistance(25, 10);
        drive.turnLeft(25, 90);

    }
}
