package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;
import ca.uoit.crobot.odometry.pid.FakeMotor;
import ca.uoit.crobot.odometry.pid.PID;
import ca.uoit.crobot.odometry.pid.PIDInput;

import java.util.LinkedList;
import java.util.Queue;

public class Drive extends Thread {

    // Constant for converting inches and encoder counts
    private static final double ENCODER_COUNTS_PER_INCH = 93;

    // Constant for converting degrees and encoder counts
    private static final double ENCODER_COUNTS_PER_DEGREE = 8.2;

    // A queue of commands to execute
    private final Queue<Command> commandQueue = new LinkedList<>();

    // Motor declarations
    private final Motor leftMotor;
    private final Motor rightMotor;

    // PID declarations
    private final PID leftRatePID;
    private final PID rightRatePID;
    private final PID leftDistancePID;
    private final PID rightDistancePID;

    public void init() {
        leftMotor.init();
        rightMotor.init();
    }

    public Drive(Motor leftMotor, Motor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        leftRatePID = new PID(leftMotor, new PIDInput() {
            @Override
            public double getInput() {
                return leftMotor.getSpeed();
            }
        }, 0.3);

        rightRatePID = new PID(rightMotor, new PIDInput() {
            @Override
            public double getInput() {
                return rightMotor.getSpeed();
            }
        }, 0.3);

        leftDistancePID = new PID(new FakeMotor(), new PIDInput() {
            @Override
            public double getInput() {
                return leftMotor.getCount();
            }
        }, 0.3);

        rightDistancePID = new PID(new FakeMotor(), new PIDInput() {
            @Override
            public double getInput() {
                return rightMotor.getCount();
            }
        }, 0.3);
    }

    /**
     * Drive the robot straight with the given speed and distance in inches. To drive backwards, enter a negative
     * distance.
     *
     * @param speed The speed to drive the robot.
     * @param inches The distance to drive in inches. Enter negative values to reverse direction.
     */
    public void driveDistance(int speed, double inches) {
        leftRatePID.setMaxSpeed(speed);
        rightRatePID.setMaxSpeed(speed);

        // Convert the distance to encoder counts
        int distance = (int) (inches * ENCODER_COUNTS_PER_INCH);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.DRIVE, speed, distance, 0);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if(!this.isAlive()) {
            this.start();
        }

        leftMotor.zero();
        rightMotor.zero();
    }

    /**
     * Drive the robot straight at max speed for the given distance in inches. To drive backwards, enter a negative
     * distance.
     *
     * @param inches The distance to drive in inches. Enter negative values to reverse direction.
     */
    public void driveDistance(double inches) {
        driveDistance(100, inches);
    }

    /**
     * Turn the robot to the left with the given speed.
     *
     * @param speed The speed to drive the robot.
     * @param degrees The amount to turn in degrees.
     */
    public void turnLeft(int speed, double degrees) {
        leftRatePID.setMaxSpeed(speed);
        rightRatePID.setMaxSpeed(speed);

        // Convert the degrees to encoder counts
        int distance = (int) (degrees * ENCODER_COUNTS_PER_DEGREE);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.TURN_LEFT, speed, distance, 0);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if(!this.isAlive()) {
            this.start();
        }

        leftMotor.zero();
        rightMotor.zero();
    }

    /**
     * Turn the robot to the left.
     *
     * @param degrees The amount to turn in degrees.
     */
    public void turnLeft(double degrees) {
        turnLeft(100, degrees);
    }

    /**
     * Turn the robot to the right with the given speed.
     *
     * @param speed The speed to drive the robot.
     * @param degrees The amount to turn in degrees.
     */
    public void turnRight(int speed, double degrees) {
        leftRatePID.setMaxSpeed(speed);
        rightRatePID.setMaxSpeed(speed);

        // Convert the degrees to encoder counts
        int distance = (int) (degrees * ENCODER_COUNTS_PER_DEGREE);

        // Create a new command and add it to the queue
        Command driveCommand = new Command(Command.CommandType.TURN_LEFT, speed, distance, 0);
        commandQueue.add(driveCommand);

        // If the thread is not running, start it so that the queue can be emptied.
        if(!this.isAlive()) {
            this.start();
        }

        leftMotor.zero();
        rightMotor.zero();
    }

    /**
     * Turn the robot to the right.
     *
     * @param degrees The amount to turn in degrees.
     */
    public void turnRight(double degrees) {
        turnRight(100, degrees);
    }

    public void run() {
        // Run until the command queue is empty
        while(!commandQueue.isEmpty()) {

            // Get the current command
            Command currentCommand = commandQueue.poll();

            // Save the starting position of the encoders to compare against later
            int leftStartPos = leftMotor.getCount();
            int rightStartPos = rightMotor.getCount();

            switch (currentCommand.commandType) {

                case DRIVE:
                    if (currentCommand.distance < 0) {
                        // Drive backwards at the given speed
                        leftMotor.setSpeed(-currentCommand.speed);
                        rightMotor.setSpeed(-currentCommand.speed);
                        System.out.println("Driving backwards " + currentCommand.distance + " encoder counts");
                    }
                    else {
                        // Drive forwards at the given speed
                        leftMotor.setSpeed(currentCommand.speed);
                        rightMotor.setSpeed(currentCommand.speed);
                        System.out.println("Driving forwards " + currentCommand.distance + " encoder counts");
                    }

                    break;
                case TURN_LEFT:
                    // Turn left at the given speed
                    leftMotor.setSpeed(currentCommand.speed);
                    rightMotor.setSpeed(-currentCommand.speed);
                    System.out.println("Turning Left " + currentCommand.distance + " encoder counts");

                    break;
                case TURN_RIGHT:
                    // Turn right at the given speed
                    leftMotor.setSpeed(-currentCommand.speed);
                    rightMotor.setSpeed(currentCommand.speed);
                    System.out.println("Turning right " + currentCommand.distance + " encoder counts");

                    break;
            }

            // Don't move to the next command until the encoder counts reach the given distance
            while (Math.abs(leftMotor.getCount() - leftStartPos) < Math.abs(currentCommand.distance)
                    && Math.abs(rightMotor.getCount() - rightStartPos) < Math.abs(currentCommand.distance)) {

                System.out.println("Waiting for robot to travel to destination. Left encoder: " + leftMotor.getCount() + " Right encoder: " + rightMotor.getCount());

                try {
                    Thread.sleep(10);
                } catch(InterruptedException e) {}
            }

            // Stop the motors
            leftMotor.stop();
            rightMotor.stop();
        }
    }
}
