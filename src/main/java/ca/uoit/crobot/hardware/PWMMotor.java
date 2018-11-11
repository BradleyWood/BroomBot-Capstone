package ca.uoit.crobot.hardware;

import com.pi4j.io.gpio.*;

import static ca.uoit.crobot.hardware.GpioUility.getDigitalOutput;
import static ca.uoit.crobot.hardware.GpioUility.getPwmOutput;

public class PWMMotor implements Motor {

    private final int enablePinAddress;
    private final int inputAAddress;
    private final int inputBAddress;

    private GpioPinPwmOutput enablePin;
    private GpioPinPwmOutput pinAPwm; // used if the controller does not provide an enable pin
    private GpioPinDigitalOutput pinADigital;
    private GpioPinDigitalOutput pinBDigital;

    public PWMMotor(final int inputAAddress, final int inputBAddress) {
        this(-1, inputAAddress, inputBAddress);
    }

    public PWMMotor(final int enablePinAddress, final int inputAAddress, final int inputBAddress) {
        this.enablePinAddress = enablePinAddress;
        this.inputAAddress = inputAAddress;
        this.inputBAddress = inputBAddress;
    }

    @Override
    public void setSpeed(final int speed) {
        if (speed > 100 || speed < -100)
            throw new IllegalArgumentException("Speed must be the in range [-100, 100]");

        if (enablePin != null) {
            enablePin.setPwm(Math.abs(speed));

            if (speed > 0) {
                pinADigital.setState(PinState.HIGH);
            } else {
                pinADigital.setState(PinState.LOW);
            }
        } else if (pinAPwm != null) {
            pinAPwm.setPwm(Math.abs(speed));
        } else {
            throw new IllegalStateException("not initialized");
        }

        if (speed > 0) {
            pinBDigital.setState(PinState.LOW);
        } else {
            pinBDigital.setState(PinState.HIGH);
        }
    }

    @Override
    public void stop() {
        setSpeed(0);
    }

    @Override
    public int getSpeed() {
        if (enablePin != null)
            return enablePin.getPwm();

        if (pinAPwm == null)
            throw new IllegalStateException("not initialized");

        return pinAPwm.getPwm();
    }

    @Override
    public void init() {
        if (enablePinAddress > -1) {
            enablePin = getPwmOutput(enablePinAddress);
            enablePin.setShutdownOptions(true, PinState.LOW);
            enablePin.setPwmRange(100);

            pinADigital = getDigitalOutput(inputAAddress);
            pinADigital.setShutdownOptions(true, PinState.LOW);
        } else {
            pinAPwm = getPwmOutput(inputAAddress);
            pinAPwm.setPwmRange(100);
            pinAPwm.setShutdownOptions(true, PinState.LOW);
        }

        pinBDigital = getDigitalOutput(inputBAddress);
        pinBDigital.setShutdownOptions(true, PinState.LOW);
    }
}
