package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;

public class PID {

    private final Motor motor;
    private double setpoint = 0;
    private boolean running = false;

    private final Thread pidThread;

    public PID(Motor motor) {
        this.motor = motor;

        pidThread = new Thread(() -> {

            double kp = 0.75;
            double ki = 0.1;
            double kd = 10;

            double prevError = 0;
            double dError = 0;
            double error;
            double totalError = 0;

            long now;
            long lastTime = System.currentTimeMillis();

            while(running) {
                now = System.currentTimeMillis();
                double timeChange = (double) (now - lastTime);

                if(setpoint > 0) {
                    error = setpoint - motor.getRate();
                } else {
                    error = setpoint + motor.getRate();
                }

                totalError += error;
                dError = (error - prevError) / timeChange;

                int speed = (int) (kp * error + ki * totalError + kd * dError);

                if(speed > 100) {
                    speed = 100;
                } else if(speed < -100) {
                    speed = -100;
                }

                motor.setSpeed(speed);

                lastTime = now;
                prevError = error;

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                }
            }

            stop();
        });

    }

    public void setSetpoint(double setpoint) {
        this.setpoint = setpoint;

        if(!running) {
            running = true;
            pidThread.start();
        }
    }

    public void stop() {
        motor.stop();
        setpoint = 0;
        running = false;
    }
}
