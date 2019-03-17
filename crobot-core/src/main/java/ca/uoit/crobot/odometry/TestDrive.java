package ca.uoit.crobot.odometry;

import ca.uoit.crobot.hardware.Motor;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;

public class TestDrive {

    public static void main(String[] args) {

        Motor m = new Motor() {
            int count = 0;
            double rate = 0;


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
                count += 1;
                return count;
            }

            @Override
            public double getRate() {
                return rate;
            }

            @Override
            public void zero() {
                count = 0;
            }

            @Override
            public void init() {
                new Thread(() -> {
                    long prevTime = 0;
                    int prevCount = 0;

                    while(true) {
                        long currentTime = System.currentTimeMillis();
                        long deltaT = prevTime - currentTime;

                        rate = count - prevCount;

                        prevCount = count;
                        prevTime = currentTime;

                        try {
                            Thread.sleep(25);
                        } catch (InterruptedException e) {
                        }
                    }
                }).start();
            }};

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
                count += 6;
                return count;
            }

            @Override
            public double getRate() {
                return 0;
            }

            @Override
            public void zero() {
                count = 0;
            }

            @Override
            public void init() {
            }
        }, m);

        drive.init();

        drive.drive(100);

        long start = System.currentTimeMillis();

        while(System.currentTimeMillis() - start < 2000) {
            System.out.println(m.getRate());
            try {
                Thread.sleep(25);
            } catch(InterruptedException e ){

            }
        }
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        PoseChange change = drive.getPoseChange();

        System.out.println("Distance: " + change.getDxyMm() + " Theta: " + change.getDthetaDegrees());
    }
}
