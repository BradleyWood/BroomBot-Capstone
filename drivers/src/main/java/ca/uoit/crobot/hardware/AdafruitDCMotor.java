package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import java.util.concurrent.atomic.AtomicInteger;

public enum AdafruitDCMotor implements Motor {

    MOTOR1(8, 9, 10, 27, 24),
    MOTOR2(13, 12, 11, 28, 29),
    MOTOR3(2, 3, 4, -1, -1),
    MOTOR4(7, 6, 5, -1, -1);

    final int pwnPin;
    final int inputA;
    final int inputB;
    final int encoderPhaseA;
    final int encoderPhaseB;

    private final AtomicInteger counter = new AtomicInteger();
    private int speed = 0;

    AdafruitDCMotor(final int pwnPin, final int inputA, final int inputB, final int encoderPhaseA, final int encoderPhaseB) {
        this.pwnPin = pwnPin;
        this.inputA = inputA;
        this.inputB = inputB;
        this.encoderPhaseA = encoderPhaseA;
        this.encoderPhaseB = encoderPhaseB;
    }

    @Override
    public void setSpeed(final int speed) {
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
        } catch (Exception e) {
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
    public void init() {
        stop();

        if (encoderPhaseA != -1) {
            GpioUtility.getDigitalInput(encoderPhaseA).addListener((GpioPinListenerDigital) event -> {
                counter.incrementAndGet();
            });
        }

        Runtime.getRuntime().addShutdownHook(new Thread(this::stop));
    }
}
