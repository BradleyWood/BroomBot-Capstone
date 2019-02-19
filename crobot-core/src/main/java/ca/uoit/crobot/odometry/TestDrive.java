package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;

public class TestDrive {

    public static void main(String[] args) {
        Drive drive = new Drive(new Motor() {
            int count = 0;

            @Override
            public void setSpeed(int speed) {
                //System.out.println("Left speed: " + speed);
            }

            @Override
            public int getSpeed() {
                return 0;
            }

            @Override
            public void stop() {
            }

            @Override
            public int getCount() {
                count += 5;
                return count;
            }

            @Override
            public void zero() {
                count = 0;
            }

            @Override
            public void init() {
            }
        }, new Motor() {
            int count = 0;

            @Override
            public void setSpeed(int speed) {
                //System.out.println("Right speed: " + speed);
            }

            @Override
            public int getSpeed() {return 0;}

            @Override
            public void stop() {}

            @Override
            public int getCount() {
                count += 5;
                return count;
            }
            @Override
            public void zero() {
                count = 0;
            }

            @Override
            public void init() {}});

        drive.init();

        drive.drive(100);
    }
}
