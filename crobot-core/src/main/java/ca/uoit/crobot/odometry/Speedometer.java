package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;

public class Speedometer {

    private long prevTime = 0;
    private int previousCount = 0;
    private double speed = 0;

    private int index = 0;
    private double[] previousSpeeds = new double[4];

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

        double avgSpeed = speed;

        for(double d: previousSpeeds) {
            avgSpeed += d;
        }

        avgSpeed /= 5;

        previousSpeeds[index] = speed;
        index = (index + 1) % 4;

        prevTime = System.currentTimeMillis();
        previousCount = motor.getCount();

        //System.out.println(avgSpeed);

        return avgSpeed;
    }
}
