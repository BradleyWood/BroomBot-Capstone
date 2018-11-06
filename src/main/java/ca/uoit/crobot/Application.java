package ca.uoit.crobot;

import com.pi4j.io.gpio.*;

public class Application {

    public static void main(String[] args) throws InterruptedException {
        final GpioController controller = GpioFactory.getInstance();

        final GpioPinPwmOutput leftPmw = controller.provisionPwmOutputPin(RaspiPin.GPIO_01, "LEFT_PMW");
        final GpioPinDigitalOutput leftMotorDirection = controller.provisionDigitalOutputPin(RaspiPin.GPIO_04, "LEFT_DIR");

        final GpioPinPwmOutput rightPmw = controller.provisionPwmOutputPin(RaspiPin.GPIO_26, "RIGHT_PMW");
        final GpioPinDigitalOutput rightMotorDirection = controller.provisionDigitalOutputPin(RaspiPin.GPIO_27, "RIGHT_DIR");

        leftPmw.setShutdownOptions(true, PinState.LOW);
        rightPmw.setShutdownOptions(true, PinState.LOW);

        leftMotorDirection.setShutdownOptions(true, PinState.LOW);
        rightMotorDirection.setShutdownOptions(true, PinState.LOW);


        System.out.println("Test run for 10 seconds.");

        leftPmw.setPwm(1024);
        rightPmw.setPwm(1024);

        Thread.sleep(10000);

        controller.shutdown();
    }
}
