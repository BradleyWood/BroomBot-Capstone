package ca.uoit.crobot.model;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.awt.*;

@RequiredArgsConstructor
public class GameObject {

    private final @Getter Polygon body;
    private final @Getter Color color;

    private @Getter Vector2f position = new Vector2f();
    private @Getter Vector2f velocity = new Vector2f();
    private @Getter @Setter float angularVelocity;
    private @Getter @Setter float yaw;

    /**
     * Update object position and angle
     */
    public void update() {
        yaw += angularVelocity;
        position.setX(position.getX() + velocity.getX());
        position.setY(position.getX() + velocity.getY());
    }
}
