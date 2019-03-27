package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;

public class Drive implements Runnable {

    public enum Direction {STRAIGHT, LEFT, RIGHT}

    // Current direction of the robot
    private Direction dir = Direction.STRAIGHT;

    // A thread that keeps track of where the robot is going
    private Thread tracker;

    // Constant for converting millimeters and encoder counts
    private static final double ENCODER_COUNTS_PER_MILLIMETER = 3.6592; // Old value: 2.02

    // Constant for converting degrees and encoder counts
    private static final double ENCODER_COUNTS_PER_DEGREE = 7.5925; // Old Value: 8.2

    private static final double MAX_ENCODER_COUNTS_PER_TICK = 70;

    // Whether the robot is currently driving
    private boolean driving = false;

    // Tracking variables
    private double xDistance = 0;
    private double yDistance = 0;
    private double angle = 0;

    // Last time the PoseChange was calculated
    private long lastPoseChangeTime = System.currentTimeMillis();

    // Motor declarations
    private final Motor leftMotor;
    private final Motor rightMotor;

    private final PID leftPID;
    private final PID rightPID;

    public Drive(final Motor leftMotor, final Motor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;

        leftPID = new PID(leftMotor);
        rightPID = new PID(rightMotor);
    }

    /**
     * Drive forwards at the given speed
     *
     * @param speed The speed to drive the robot at (-100 to 100)
     */
    public void drive(final int speed) {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);

        if (speed == 0) {
            driving = false;
            return;
        }

        dir = Direction.STRAIGHT;
        start();
    }

    /**
     * Drive forwards at the given speed using PIDs
     *
     * @param speed The speed to drive the robot at (-100 to 100)
     */
    public void drivePID(final int speed) {
        if (speed == 0) {
            driving = false;
            leftPID.stop();
            rightPID.stop();
            return;
        }

        double rate = (speed / 100.0) * MAX_ENCODER_COUNTS_PER_TICK;

        leftPID.setSetpoint(rate);
        rightPID.setSetpoint(rate);

        dir = Direction.STRAIGHT;
    }

    /**
     * Drive forwards at the given rate until it drives a certain distance using PIDs
     *
     * @param speed       The speed to drive the robot at (-100 to 100)
     * @param millimeters The distance to drive in millimeters
     */
    public void driveToDistance(final int speed, final double millimeters) {

        double distance_enc = millimeters * ENCODER_COUNTS_PER_MILLIMETER;

        int leftStartCount = leftMotor.getCount();
        int rightStartCount = rightMotor.getCount();

        double rate = (speed / 100.0) * MAX_ENCODER_COUNTS_PER_TICK;

        leftPID.setSetpoint(rate);
        rightPID.setSetpoint(rate);

        dir = Direction.STRAIGHT;
        start();

        while (distance_enc > (leftMotor.getCount() - leftStartCount)
                && distance_enc > (rightMotor.getCount() - rightStartCount)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }

        stop();
    }

    /**
     * Turn at the given speed
     *
     * @param speed The speed to turn at (-100 to 100)
     */
    public void turn(final int speed) {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(-speed);

        if (speed == 0) {
            driving = false;
            return;
        }

        if (speed < 0) {
            dir = Direction.LEFT;
        } else {
            dir = Direction.RIGHT;
        }

        start();
    }

    /**
     * Turn at the given speed
     *
     * @param speed The speed to turn at (-100 to 100)
     */
    public void turnPID(final int speed) {
        if (speed == 0) {
            driving = false;
            leftPID.stop();
            rightPID.stop();
            return;
        }

        final double rate = (speed / 100.0) * MAX_ENCODER_COUNTS_PER_TICK;

        leftPID.setSetpoint(rate);
        rightPID.setSetpoint(-rate);

        if (speed < 0) {
            dir = Direction.LEFT;
        } else {
            dir = Direction.RIGHT;
        }

        start();
    }

    /**
     * Turn at the given rate until a certain angle is reached
     *
     * @param speed The speed to turn at (-100 to 100)
     * @param angle The angle to turn to
     */
    public void turnToAngle(final int speed, final double angle) {
        final double distance_enc = angle * ENCODER_COUNTS_PER_DEGREE;

        final int leftStartCount = leftMotor.getCount();
        final int rightStartCount = rightMotor.getCount();

        double rate = (speed / 100.0) * MAX_ENCODER_COUNTS_PER_TICK;

        leftPID.setSetpoint(-rate);
        rightPID.setSetpoint(rate);

        if (rate < 0) {
            dir = Direction.LEFT;
        } else {
            dir = Direction.RIGHT;
        }

        start();

        while (Math.abs(distance_enc) > (leftMotor.getCount() - leftStartCount)
                && Math.abs(distance_enc) > (rightMotor.getCount() - rightStartCount)) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }

        stop();
    }

    /**
     * Stop the motors and the tracker thread
     */
    public void stop() {
        leftPID.stop();
        rightPID.stop();

        leftMotor.stop();
        rightMotor.stop();
        driving = false;
    }

    private void start() {
        // Start the tracker thread if it has not been created, or if it is not currently running
        if (tracker == null || !tracker.isAlive()) {
            tracker = new Thread(this);
            tracker.start();

            driving = true;
        }
    }

    @Override
    public void run() {
        int prevLeftCounts = leftMotor.getCount();
        int prevRightCounts = rightMotor.getCount();

        while (driving) {
            synchronized (this) {
                switch (dir) {
                    case STRAIGHT:
                        // Update distances
                        xDistance += Math.sin(Math.PI * angle / 180) * ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_MILLIMETER;
                        yDistance += Math.cos(Math.PI * angle / 180) * ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_MILLIMETER;
                        // angle += ((leftMotor.getCount() - prevLeftCounts) - (rightMotor.getCount() - prevRightCounts)) / ENCODER_COUNTS_PER_DEGREE;
                        break;
                    case LEFT:
                        // Update angle
                        angle -= ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_DEGREE;
                        break;
                    case RIGHT:
                        // Update angle
                        angle += ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_DEGREE;
                        break;
                }

                // Save current encoder counts for the next loop iteration
                prevLeftCounts = leftMotor.getCount();
                prevRightCounts = rightMotor.getCount();
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ignored) {
            }
        }
    }

    /**
     * Calculates a PoseChange object based on the direction the robot is travelling. This method will zero
     * the tracking values.
     *
     * @return A PoseChange object representing the displacement of the robot
     */
    public PoseChange getPoseChange() {

        PoseChange poseChange;

        double dtSeconds = (System.currentTimeMillis() - lastPoseChangeTime) / 1000.0;
        lastPoseChangeTime = System.currentTimeMillis();

        // If the robot is going straight
        if (dir == Direction.STRAIGHT) {
            // Calculate the displacement vector
            double r = Math.sqrt(Math.pow(yDistance, 2) + Math.pow(xDistance, 2));
            double degrees = arctan(yDistance, xDistance);

            // Update the poseChange object
            poseChange = new PoseChange(r, degrees, dtSeconds);
        } else {
            // The robot is turning on the spot, so only update the angle. Leave the distance at 0
            poseChange = new PoseChange(0, angle, dtSeconds);
        }

        // Zero the tracking variables
        xDistance = 0;
        yDistance = 0;
        angle = 0;

        return poseChange;
    }

    private double arctan(double y, double x) {
        double degrees;

        if (x == 0 && y > 0) {
            degrees = 0;
        } else if (x == 0 && y < 0) {
            degrees = 180;
        } else if (x > 0 && y == 0) {
            degrees = 90;
        } else if (x < 0 && y == 0) {
            degrees = -90;
        } else {
            degrees = 180 * Math.atan(y / x) / Math.PI;
        }

        return degrees;
    }
}
