package ca.uoit.crobot.hardware;

public interface Motor {

    /**
     * Set the speed of the device, as a percent
     *
     * @param speed The speed out of 100
     */
    void setSpeed(int speed);

    /**
     * The current speed of the motor, out of 100
     */
    void getSpeed();

    /**
     * Stops the motor from turning
     */
    void stop();
}
