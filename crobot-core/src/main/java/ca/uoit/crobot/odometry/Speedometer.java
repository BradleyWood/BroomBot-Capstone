package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;

public class Speedometer {

    private long prevTime = 0;
    private int previousCount = 0;
    private double speed = 0;

    private static final double MAX_ENCODER_COUNTS_PER_SECOND = 3300;

    private final Motor motor;

    public Speedometer(Motor motor) {
        this.motor = motor;
    }

    public double getSpeed() {
        if (System.currentTimeMillis() - prevTime > 100) {
            speed = 0;
        } else {
            speed = (1000.0 / (System.currentTimeMillis() - prevTime) * (motor.getCount() - previousCount)) / MAX_ENCODER_COUNTS_PER_SECOND * 100;
        }

        prevTime = System.currentTimeMillis();
        previousCount = motor.getCount();

        return speed;
    }
}
