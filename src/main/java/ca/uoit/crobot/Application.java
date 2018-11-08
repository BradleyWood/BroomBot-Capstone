package ca.uoit.crobot;

import com.pi4j.io.gpio.*;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {
        final GpioController controller = GpioFactory.getInstance();

        final Scanner scanner = new Scanner(System.in);

        final Pin[] pins = RaspiPin.allPins();
        final GpioPinDigitalOutput[] devices = new GpioPinDigitalOutput[pins.length];

        System.out.println("Found " + pins.length + " pins.");

        while (true) {
            System.out.println("Enter pin address: ");
            final int address = Integer.parseInt(scanner.nextLine());
            System.out.println("Enter true to set state to HIGH");
            final boolean high = scanner.nextLine().trim().toLowerCase().equals("true");

            GpioPinDigitalOutput device = null;

            for (int i = 0; i < pins.length; i++) {
                if (pins[i].getAddress() == address) {
                    if (devices[i] == null) {
                        devices[i] = controller.provisionDigitalOutputPin(pins[i]);
                    }
                    device = devices[i];
                    break;
                }
            }

            if (device == null) {
                System.out.println("Device not found");
                break;
            }

            if (high) {
                device.setState(PinState.HIGH);
            } else {
                device.setState(PinState.LOW);
            }

            System.out.println("Pin: " + address + " name: " + device.getPin().getName() + " high=" + high);

            device.setShutdownOptions(true, PinState.LOW);
        }
    }
}
