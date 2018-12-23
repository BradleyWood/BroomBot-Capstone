package ca.uoit.crobot.hardware;

public interface Motor extends Device {

    /**
     * Set the speed of the device, as a percent.
     * The sign denotes the direction of the motor.
     *
     * @param speed The speed [-100, 100]
     */
    void setSpeed(int speed);

    /**
     * The current speed of the motor [-100, 100]
     * The sign denotes the direction of the motor.
     */
    int getSpeed();

    /**
     * Stops the motor from turning
     */
    void stop();

    /**
     * Returns the number of pulses from the motor's hall effect
     * sensor
     *
     * @return number of pulses
     */
    int getCount();

}
