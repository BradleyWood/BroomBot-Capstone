package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;
import ca.uoit.crobot.odometry.pid.FakeMotor;
import ca.uoit.crobot.odometry.pid.PID;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Vector;

public class Drive extends Thread {

    // Constant for converting inches and encoder counts
    private static final double ENCODER_COUNTS_PER_INCH = 93;

    // Constant for converting degrees and encoder counts
    private static final double ENCODER_COUNTS_PER_DEGREE = 8.2;

    // A queue of commands to execute
    private final Queue<Command> commandQueue = new LinkedList<>();

    private int distance = 0;
    private double angle = 0;

    // Motor declarations
    private final Motor leftMotor;
    private final Motor rightMotor;

    // PID declarations
    private final PID leftRatePID;
    private final PID rightRatePID;
    private final PID leftDistancePID;
    private final PID rightDistancePID;

    private final Speedometer leftSpeedometer;
    private final Speedometer rightSpeedometer;

    public void init() {
        leftMotor.init();
        rightMotor.init();
    }

    public Drive(Motor leftMotor, Motor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        leftSpeedometer = new Speedometer(this.leftMotor);
        rightSpeedometer = new Speedometer(this.rightMotor);

        leftRatePID = new PID(leftMotor, leftSpeedometer::getSpeed, 1);

        rightRatePID = new PID(rightMotor, rightSpeedometer::getSpeed, 1);

        leftDistancePID = new PID(new FakeMotor(), leftMotor::getCount, 0.05);

        rightDistancePID = new PID(new FakeMotor(), rightMotor::getCount, 0.05);
    }

    /**
     * Drive the robot straight with the given speed and distance in inches. To drive backwards, enter a negative
     * distance.
     *
     * @param speed  The speed to drive the robot.
     * @param inches The distance to drive in inches. Enter negative values to reverse direction.
     */
    public void driveDistance(int speed, double inches) {
        leftRatePID.setMaxSetPoint(speed);
        rightRatePID.setMaxSetPoint(speed);

        // Convert the distance to encoder counts
        int distance = (int) (inches * ENCODER_COUNTS_PER_INCH);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.DRIVE, speed, distance);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if (!this.isAlive()) {
            this.start();
        }
    }

    /**
     * Turn the robot to the left with the given speed.
     *
     * @param speed   The speed to drive the robot.
     * @param degrees The amount to turn in degrees.
     */
    public void turnLeft(int speed, double degrees) {
        leftRatePID.setMaxSetPoint(speed);
        rightRatePID.setMaxSetPoint(speed);

        // Convert the degrees to encoder counts
        int distance = (int) (degrees * ENCODER_COUNTS_PER_DEGREE);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.TURN_LEFT, speed, distance);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if (!this.isAlive()) {
            this.start();
        }
    }

    /**
     * Turn the robot to the right with the given speed.
     *
     * @param speed   The speed to drive the robot.
     * @param degrees The amount to turn in degrees.
     */
    public void turnRight(int speed, double degrees) {
        leftRatePID.setMaxSetPoint(speed);
        rightRatePID.setMaxSetPoint(speed);

        // Convert the degrees to encoder counts
        int distance = (int) (degrees * ENCODER_COUNTS_PER_DEGREE);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.TURN_LEFT, speed, distance);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if (!this.isAlive()) {
            this.start();
        }
    }

    @Override
    public void run() {
        // Run until the command queue is empty
        while (!commandQueue.isEmpty()) {

            leftMotor.zero();
            rightMotor.zero();

            // Get the current command
            Command currentCommand = commandQueue.poll();

            switch (currentCommand.commandType) {

                case DRIVE:
                    if (currentCommand.distance < 0) {
                        // Drive backwards at the given speed
                        leftDistancePID.setSetPoint(-currentCommand.distance);
                        rightDistancePID.setSetPoint(-currentCommand.distance);
                        //System.out.println("Driving backwards " + currentCommand.distance + " encoder counts");
                    } else {
                        // Drive forwards at the given speed
                        leftDistancePID.setSetPoint(currentCommand.distance);
                        rightDistancePID.setSetPoint(currentCommand.distance);
                        //System.out.println("Driving forwards " + currentCommand.distance + " encoder counts");
                    }

                    break;
                case TURN_LEFT:
                    // Turn left at the given speed
                    leftDistancePID.setSetPoint(currentCommand.distance);
                    rightDistancePID.setSetPoint(-currentCommand.distance);
                    //System.out.println("Turning Left " + currentCommand.distance + " encoder counts");

                    break;
                case TURN_RIGHT:
                    // Turn right at the given speed
                    leftDistancePID.setSetPoint(-currentCommand.distance);
                    rightDistancePID.setSetPoint(currentCommand.distance);
                    //System.out.println("Turning right " + currentCommand.distance + " encoder counts");

                    break;
            }

            int prevLeftCounts = leftMotor.getCount();
            int prevRightCounts = rightMotor.getCount();

            // Don't move to the next command until the encoder counts reach the given distance
            while (Math.abs(leftMotor.getCount()) < Math.abs(currentCommand.distance)
                    && Math.abs(rightMotor.getCount()) < Math.abs(currentCommand.distance)) {

                leftRatePID.setSetPoint(leftDistancePID.get());
                rightRatePID.setSetPoint(rightDistancePID.get());

                switch(currentCommand.commandType) {
                    case DRIVE:
                        distance += ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2;
                        break;
                    case TURN_LEFT:
                        angle -= ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2 / ENCODER_COUNTS_PER_DEGREE;
                        break;
                    case TURN_RIGHT:
                        angle += ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2 / ENCODER_COUNTS_PER_DEGREE;
                        break;
                }

                //System.out.println(distance + " " + angle);

                prevLeftCounts = leftMotor.getCount();
                prevRightCounts = rightMotor.getCount();

                //System.out.println("Waiting for robot to travel to destination. Left encoder: " + (leftMotor.getCount() - leftStartPos) + " Right encoder: " + (rightMotor.getCount() - rightStartPos));

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                }
            }
        }

        // Stop the motors
        leftMotor.stop();
        rightMotor.stop();

        leftRatePID.stopPID();
        rightRatePID.stopPID();

        leftDistancePID.stopPID();
        rightDistancePID.stopPID();
    }
}
