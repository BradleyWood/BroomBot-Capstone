package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;

import static ca.uoit.crobot.hardware.GpioUtility.getDigitalInput;
import static ca.uoit.crobot.hardware.GpioUtility.getDigitalOutput;

public class UltrasonicSensor implements DistanceSensor {

    public static final int DEFAULT_TRIGGER_TIME_NS = 10000;

    private final int triggerPinAddress;
    private final int echoPinAddress;
    private final int triggerTimeNs;

    private GpioPinDigitalOutput triggerPin;
    private GpioPinDigitalInput echoPin;

    public UltrasonicSensor(final int triggerPinAddress, final int echoPinAddress) {
        this(triggerPinAddress, echoPinAddress, DEFAULT_TRIGGER_TIME_NS);
    }

    public UltrasonicSensor(final int triggerPinAddress, final int echoPinAddress, final int triggerTimeNs) {
        this.triggerPinAddress = triggerPinAddress;
        this.echoPinAddress = echoPinAddress;
        this.triggerTimeNs = triggerTimeNs;
    }

    @Override
    public synchronized int measure() {
        if (triggerPin == null || echoPin == null)
            throw new IllegalStateException("not initialized");

        triggerPin.setState(PinState.HIGH);

        long triggerStart = System.nanoTime();

        while (triggerStart + triggerTimeNs > System.nanoTime());

        triggerPin.setState(PinState.LOW);

        long start = System.nanoTime();

        while (echoPin.isLow())
            start = System.nanoTime();

        while (echoPin.isHigh());

        long duration = System.currentTimeMillis() - start;

        return (int) (duration / 58309.0);
    }

    @Override
    public void init() {
        triggerPin = getDigitalOutput(triggerPinAddress);
        triggerPin.setShutdownOptions(true, PinState.LOW);
        triggerPin.setState(PinState.LOW);

        echoPin = getDigitalInput(echoPinAddress);
        echoPin.setShutdownOptions(true, PinState.LOW);
    }
}
