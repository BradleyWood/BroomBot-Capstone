package ca.uoit.crobot.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

@RequiredArgsConstructor
public class GameObject {

    private final @Getter Polygon body;
    private final @Getter Color color;

    private final @Getter Vector2f position = new Vector2f();
    private @Getter @Setter float speed;
    private @Getter @Setter float angularVelocity;
    private @Getter @Setter float yaw;

    /**
     * Update object position and angle
     */
    public void update() {
        yaw += angularVelocity;

        yaw %= 2 * Math.PI;

        position.setX(position.getX() + speed * (float) Math.sin(yaw));
        position.setY(position.getY() + speed * (float) Math.cos(yaw));

    }
}
