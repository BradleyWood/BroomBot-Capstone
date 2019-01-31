package ca.uoit.crobot.odometry.pid;

import ca.uoit.crobot.hardware.Motor;

public class PID extends Thread {

    private final Motor motor;
    private final PIDInput pidInput;
    private double P;

    private boolean running = false;

    private double setpoint = 0;
    private double error = 0;
    private int speed = 0;
    private int maxSpeed = 0;

    public PID(Motor motor, PIDInput pidInput, double P) {
        this.motor = motor;
        this.pidInput = pidInput;
        this.P = P;
    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;

        if(!this.isAlive()) {
            this.start();
            running = true;
        }
    }

    public void run() {
        while(running) {
            error = setpoint - pidInput.getInput();

            speed = (int) (P * error + 0.5);

            if(Math.abs(speed) > maxSpeed) {
                motor.setSpeed(maxSpeed);
            } else {
                motor.setSpeed(speed);
            }

            try { Thread.sleep(10); }
            catch (InterruptedException e) {}
        }
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public int get() {
        return speed;
    }

    public void stopPID() {
        this.setpoint = 0;
        motor.stop();
        running = false;
    }

}
