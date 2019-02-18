package ca.uoit.crobot.odometry.pid;

import ca.uoit.crobot.hardware.Motor;

public class PID extends Thread {

    private final Motor motor;
    private final PIDInput pidInput;
    private double P;

    private volatile boolean running = false;

    private double setPoint = 0;
    private int speed = 0;
    private int maxSetPoint = Integer.MAX_VALUE;

    public PID(Motor motor, PIDInput pidInput, double P) {
        this.motor = motor;
        this.pidInput = pidInput;
        this.P = P;
    }

    public void setSetPoint(double setPoint) {
        this.setPoint = setPoint;
        if(setPoint > maxSetPoint) {
            this.setPoint = maxSetPoint;
        }

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

            if (Math.abs(speed) > maxSetPoint) {
                if (speed < 0) {
                    motor.setSpeed(-maxSetPoint);
                } else {
                    motor.setSpeed(maxSetPoint);
                }
            } else {
                motor.setSpeed(speed);
            }

            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
            }
        }

        motor.stop();
    }

    public void setMaxSetPoint(int maxSetPoint) {
        this.maxSetPoint = maxSetPoint;
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
