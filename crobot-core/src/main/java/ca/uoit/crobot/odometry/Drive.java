package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;

public class Drive implements Runnable {

    enum Direction {STRAIGHT, LEFT, RIGHT}

    // Current direction of the robot
    private Direction dir = Direction.STRAIGHT;

    // A thread that keeps track of where the robot is going
    private Thread tracker;

    // Constant for converting millimeters and encoder counts
    private static final double ENCODER_COUNTS_PER_MILLIMETER = 2.0362962651058991957556107625584;

    // Constant for converting degrees and encoder counts
    private static final double ENCODER_COUNTS_PER_DEGREE = 8.2;

    // Whether the robot is currently driving
    private boolean driving = false;

    // Tracking variables
    private double xDistance = 0;
    private double yDistance = 0;
    private double angle = 0;
    private int prevLeftCounts = 0;
    private int prevRightCounts = 0;


    // Last time the PoseChange was calculated
    private long lastPoseChangeTime = System.currentTimeMillis();

    // Motor declarations
    private final Motor leftMotor;
    private final Motor rightMotor;

    public void init() {
        leftMotor.init();
        rightMotor.init();
    }

    public Drive(Motor leftMotor, Motor rightMotor) {
        this.leftMotor = leftMotor;
        this.rightMotor = rightMotor;
    }

    /** Drive forwards at the given speed
     *
     * @param speed The speed to drive at
     */
    public void drive(int speed) {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(speed);

        dir = Direction.STRAIGHT;
        start();
    }

    /** Turn left at the given speed
     *
     * @param speed The speed to turn at
     */
    public void turnLeft(int speed) {
        leftMotor.setSpeed(-speed);
        rightMotor.setSpeed(speed);

        dir = Direction.LEFT;
        start();
    }

    /** Turn right at the given speed
     *
     * @param speed The speed to turn at
     */
    public void turnRight(int speed) {
        leftMotor.setSpeed(speed);
        rightMotor.setSpeed(-speed);

        dir = Direction.RIGHT;
        start();
    }

    /** Stop the motors and the tracker thread */
    public void stop() {
        leftMotor.stop();
        rightMotor.stop();
        driving = false;
    }

    private void start() {
        // Start the tracker thread if it has not been created, or if it is not currently running
        if(tracker == null || !tracker.isAlive()) {
            tracker = new Thread(this);
            tracker.start();

            driving = true;
        }
    }

    public void run() {
        prevLeftCounts = leftMotor.getCount();
        prevRightCounts = rightMotor.getCount();

        while(driving) {
            switch(dir) {
                case STRAIGHT:
                    // Update distances
                    xDistance += Math.cos(Math.PI * angle / 180) * ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_MILLIMETER;
                    yDistance += Math.sin(Math.PI * angle / 180) * ((leftMotor.getCount() - prevLeftCounts) + (rightMotor.getCount() - prevRightCounts)) / 2.0 / ENCODER_COUNTS_PER_MILLIMETER;
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

            // System.out.println("X: " + xDistance + " Y: " + yDistance + " Angle: " + angle);

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }
    }

    /** Calculates a PoseChange object based on the direction the robot is travelling. This method will zero
     * the tracking values.
     *
     * @return A PoseChange object representing the displacement of the robot
     */
    public PoseChange getPoseChange() {
        PoseChange poseChange;

        // If the robot is going straight
        if(dir == Direction.STRAIGHT) {
            // Calculate the displacement vector
            double r = Math.sqrt(Math.pow(yDistance, 2) + Math.pow(xDistance, 2));
            double degrees = 180 * Math.atan(yDistance / xDistance) / Math.PI;

            // Update the poseChange object
            poseChange = new PoseChange(r, degrees, 0);
        } else {
            // The robot is turning on the spot, so only update the angle. Leave the distance at 0
            poseChange = new PoseChange(0, angle, 0);
        }

        // Zero the tracking variables
        xDistance = 0;
        yDistance = 0;
        angle = 0;

        prevLeftCounts = 0;
        prevRightCounts = 0;

        return poseChange;
    }
}
