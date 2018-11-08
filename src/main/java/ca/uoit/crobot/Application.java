package ca.uoit.crobot;

import com.pi4j.io.gpio.*;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        final GpioController controller = GpioFactory.getInstance();

        final Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("Enter pin address: ");
            final int address = scanner.nextInt();
            System.out.println("Enter true to set state to HIGH");
            final boolean high = scanner.nextLine().trim().toLowerCase().equals("true");

            final Pin pin = RaspiPin.getPinByAddress(address);
            final GpioPinDigitalOutput device = controller.provisionDigitalOutputPin(pin);

            if (high) {
                device.setState(PinState.HIGH);
            } else {
                device.setState(PinState.LOW);
            }

            System.out.println("Pin: " + address + " name: " + pin.getName() + " high=" + high);

            device.setShutdownOptions(true, PinState.LOW);
        }
    }
}
