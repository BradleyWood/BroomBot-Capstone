package ca.uoit.crobot.odometry.pid;

import ca.uoit.crobot.hardware.Motor;

public class PID extends Thread {

    private final Motor motor;
    private final PIDInput pidInput;
    private double P;

    private volatile boolean running = false;

    private double setPoint = 0;
    private int speed = 0;
    private int maxSpeed = 0;

    public PID(Motor motor, PIDInput pidInput, double P) {
        this.motor = motor;
        this.pidInput = pidInput;
        this.P = P;
    }

    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;

        if (!running) {
            this.start();
            running = true;
        }
    }

    @Override
    public void run() {
        while (running) {
            double error = setPoint - pidInput.getInput();

            speed = (int) (P * error + 0.5);

            if (Math.abs(speed) > maxSpeed) {
                if (speed < 0) {
                    motor.setSpeed(-maxSpeed);
                } else {
                    motor.setSpeed(maxSpeed);
                }
            } else {
                motor.setSpeed(speed);
            }

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
            }
        }

        motor.stop();
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int get() {
        return speed;
    }

    public void stopPID() {
        this.setPoint = 0;
        motor.stop();
        running = false;
    }

}
