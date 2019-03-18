package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

public class AdafruitDCMotor extends Motor {

    public static AdafruitDCMotor MOTOR1 = new AdafruitDCMotor(13, 12, 11, 23, 27);
    public static AdafruitDCMotor MOTOR2 = new AdafruitDCMotor(8, 9, 10, 24, 28);
    public static AdafruitDCMotor MOTOR3 = new AdafruitDCMotor(2, 3, 4, -1, -1);
    public static AdafruitDCMotor MOTOR4 = new AdafruitDCMotor(7, 6, 5, -1, -1);

    private final int pwnPin;
    private final int inputA;
    private final int inputB;
    private final int encoderPhaseA;
    private final int encoderPhaseB;

    private final AtomicInteger counter = new AtomicInteger();
    private double rate = 0;
    private int speed = 0;

    private AdafruitDCMotor(final int pwnPin, final int inputA, final int inputB, final int encoderPhaseA, final int encoderPhaseB) {
        this.pwnPin = pwnPin;
        this.inputA = inputA;
        this.inputB = inputB;
        this.encoderPhaseA = encoderPhaseA;
        this.encoderPhaseB = encoderPhaseB;
    }

    @Override
    public synchronized void setSpeed(final int speed) {
        if (Math.abs(speed) > 100)
            throw new IllegalArgumentException("speed out of bounds [-100, 100]");

        try {
            if (speed == 0) {
                AdafruitPWMDriver.setPwm(inputA, 0, 4096);
                AdafruitPWMDriver.setPwm(inputB, 0, 4096);
            } else if (speed > 0) {
                AdafruitPWMDriver.setPwm(inputA, 0, 4096);
                AdafruitPWMDriver.setPwm(inputB, 4096, 0);
            } else {
                AdafruitPWMDriver.setPwm(inputA, 4096, 0);
                AdafruitPWMDriver.setPwm(inputB, 0, 4096);
            }

            int nv = (int)((Math.abs(speed) / 100f) * 255f + 0.5f);

            AdafruitPWMDriver.setPwm(pwnPin, 0, nv * 16);
            this.speed = speed;
        } catch (RuntimeException e) {
            e.printStackTrace();
        } catch (IOException | I2CFactory.UnsupportedBusNumberException e) {
            e.printStackTrace();
            System.exit(0);
        }
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public void stop() {
        setSpeed(0);
    }

    @Override
    public int getCount() {
        return counter.get();
    }

    @Override
    public double getRate() {
        return rate;
    }

    @Override
    public void zero() {
        counter.set(0);
    }

    @Override
    public void init() {
        stop();

        if (encoderPhaseA != -1) {
            GpioUtility.getDigitalInput(encoderPhaseA).addListener((GpioPinListenerDigital) event -> {
                final int count = counter.incrementAndGet();
                listeners.forEach(l -> l.onMove(count));
            });
        }

        new Thread(() -> {
            long prevTime = 0;
            int prevCount = 0;

            while(true) {
                long currentTime = System.currentTimeMillis();
                long deltaT = currentTime - prevTime;

                rate = (counter.get() - prevCount);// / (deltaT / 1000.0);

                prevCount = counter.get();
                prevTime = currentTime;

                try {
                    Thread.sleep(25);
                } catch (InterruptedException e) {
                }
            }
        }).start();

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }
}
