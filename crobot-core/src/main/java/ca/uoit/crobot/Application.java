package ca.uoit.crobot;


import ca.uoit.crobot.hardware.Motor;
import ca.uoit.crobot.hardware.shield.AdafruitDCMotor;
import ca.uoit.crobot.rc.RemoteControl;

import java.io.IOException;

public class Application {

    public static void main(String[] args) throws IOException {
        final Motor motorA = AdafruitDCMotor.MOTOR1;
        final Motor motorB = AdafruitDCMotor.MOTOR2;

        motorA.init();
        motorB.init();

        System.out.println("Initialized and Read to go!");

        new RemoteControl(80, motorA, motorB);
    }
}
