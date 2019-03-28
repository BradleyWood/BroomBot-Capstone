package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.PinState;

import static ca.uoit.crobot.hardware.GpioUtility.getDigitalInput;

public class InfarredSensor implements DigitalSensor {

    public static final InfarredSensor DROP_SENSOR = new InfarredSensor(26);

    private GpioPinDigitalInput signalPin;
    private final int signalPinAddress;

    public InfarredSensor(final int signalPinAddress) {
        this.signalPinAddress = signalPinAddress;
    }

    @Override
    public void init() {
        signalPin = getDigitalInput(signalPinAddress);
        signalPin.setShutdownOptions(true, PinState.LOW);
    }

    @Override
    public boolean get() {
        return signalPin.isHigh();
    }
}
