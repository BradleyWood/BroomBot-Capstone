package ca.uoit.crobot.hardware;

import ca.uoit.crobot.SimulationEntity;
import ca.uoit.crobot.model.GameObject;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public @Data class SimulatedMotor extends Motor implements SimulationEntity {

    private final GameObject robot;
    private SimulatedMotor otherMotor;

    private int counter;
    private int speed;

    @Override
    public void setSpeed(final int speed) {
        this.speed = speed;

        if (otherMotor != null) {
            final int otherSpeed = otherMotor.getSpeed();
            final int diff = speed - otherSpeed;

            robot.setSpeed((speed + otherSpeed) / 16f);
            robot.setAngularVelocity(diff / 800f);
        } else {
            throw new NullPointerException("Cannot calculate simulated velocity without reference to" +
                    " both robot motors.");
        }
    }

    @Override
    public void update() {
        counter += Math.abs(speed);
        listeners.forEach(l -> l.onMove(counter));
    }

    @Override
    public int getSpeed() {
        return speed;
    }

    @Override
    public void stop() {
        setSpeed(0);
    }

    @Override
    public int getCount() {
        return counter;
    }

    @Override
    public void init() {

    }
}
