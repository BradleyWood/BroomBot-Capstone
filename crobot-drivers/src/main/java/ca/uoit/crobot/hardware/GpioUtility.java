package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.*;

public class GpioUtility {

    public static GpioPinDigitalInput getDigitalInput(final int address) {
        final Pin pin = RaspiPin.getPinByAddress(address);

        if (pin != null)
            return GpioFactory.getInstance().provisionDigitalInputPin(pin);

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }

    public static GpioPinDigitalOutput getDigitalOutput(final int address) {
        final Pin pin = RaspiPin.getPinByAddress(address);

        if (pin != null)
            return GpioFactory.getInstance().provisionDigitalOutputPin(pin);

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }

    public static GpioPinPwmOutput getPwmOutput(final int address) {
        for (final Pin pin : RaspiPin.allPins()) {
            if (pin.getAddress() == address) {
                if (pin.getSupportedPinModes().contains(PinMode.PWM_OUTPUT)) {
                    return GpioFactory.getInstance().provisionPwmOutputPin(pin);
                } else {
                    return GpioFactory.getInstance().provisionSoftPwmOutputPin(pin);
                }
            }
        }

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }
}
