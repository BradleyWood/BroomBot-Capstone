package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.*;

public class GpioUtility {

    private static final GpioController GPIO_FACTORY = GpioFactory.getInstance();

    public static GpioPinDigitalInput getDigitalInput(final int address) {
        final Pin pin = RaspiPin.getPinByAddress(address);

        if (pin != null)
            return GPIO_FACTORY.provisionDigitalInputPin(pin);

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }

    public static GpioPinDigitalOutput getDigitalOutput(final int address) {
        final Pin pin = RaspiPin.getPinByAddress(address);

        if (pin != null)
            return GPIO_FACTORY.provisionDigitalOutputPin(pin);

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }

    public static GpioPinPwmOutput getPwmOutput(final int address) {
        for (final Pin pin : RaspiPin.allPins()) {
            if (pin.getAddress() == address) {
                if (pin.getSupportedPinModes().contains(PinMode.PWM_OUTPUT)) {
                    return GPIO_FACTORY.provisionPwmOutputPin(pin);
                } else {
                    return GPIO_FACTORY.provisionSoftPwmOutputPin(pin);
                }
            }
        }

        throw new IllegalArgumentException("Pin at address=" + address + " not found");
    }
}
