package ca.uoit.crobot.odometry.pid;

import ca.uoit.crobot.hardware.Motor;

/**
 * A fake motor used as an output of a PID
 */
public class FakeMotor extends Motor {

    @Override
    public void setSpeed(int speed) {}

    @Override
    public int getSpeed() { return 0; }

    @Override
    public void stop() {}

    @Override
    public int getCount() { return 0; }

    @Override
    public void zero() {}

    @Override
    public void init() {}
}
