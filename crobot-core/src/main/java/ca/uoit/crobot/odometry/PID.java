package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;

public abstract class PID extends Thread {

    private final Motor motor;
    private int P;

    private boolean running = false;

    private int setpoint = 0;
    private int error = 0;

    public PID(Motor motor, int P) {
        this.motor = motor;
        this.P = P;
    }

    public void setSetpoint(int setpoint) {
        this.setpoint = setpoint;

        if(!this.isAlive()) {
            this.start();
            running = true;
        }
    }

    protected abstract int getCurrentValue();

    protected abstract boolean isFinished();

    public void run() {
        while(running) {
            error = setpoint - getCurrentValue();

            motor.setSpeed(P * error);

            try { Thread.sleep(10); }
            catch (InterruptedException e) {}

            if(isFinished()) {
                stopMotor();
            }
        }
    }

    public void stopMotor() {
        this.setpoint = 0;
        motor.stop();
        running = false;
    }

}
