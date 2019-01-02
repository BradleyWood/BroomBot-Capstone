package ca.uoit.crobot.hardware;

import java.util.*;

public abstract class Motor implements Device {

    protected final Set<EncoderListener> listeners = Collections.synchronizedSet(new LinkedHashSet<>());

    /**
     * Set the speed of the device, as a percent.
     * The sign denotes the direction of the motor.
     *
     * @param speed The speed [-100, 100]
     */
    public abstract void setSpeed(int speed);

    /**
     * The current speed of the motor [-100, 100]
     * The sign denotes the direction of the motor.
     */
    public abstract int getSpeed();

    /**
     * Stops the motor from turning
     */
    public abstract void stop();

    /**
     * Returns the number of pulses from the motor's hall effect
     * sensor
     *
     * @return number of pulses
     */
    public abstract int getCount();

    /**
     * Add an encoder listener to set.
     *
     * @param listener The listener to add
     */
    public void addEncoderListener(final EncoderListener listener) {
        listeners.add(listener);
    }

    /**
     * Removes an encoder listener from set. The listener will
     * no longer be notified of any future events.
     *
     * @param listener The listener to remove
     */
    public void removeEncoderListener(final EncoderListener listener) {
        listeners.remove(listener);
    }
}
