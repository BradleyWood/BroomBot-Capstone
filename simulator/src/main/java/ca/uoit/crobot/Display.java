package ca.uoit.crobot;

import ca.uoit.crobot.model.GameObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.util.List;

@RequiredArgsConstructor
public class Display extends JPanel {

    @NonNull
    private final List<GameObject> objects;
    @NonNull
    private final Color backgroundColor;

    private final float width;

    private final float height;

    @Override
    protected void paintComponent(final Graphics g) {
        final Graphics2D g2d = (Graphics2D) g;

        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        final float transformationScaleX = getWidth() / width;
        final float transformationScaleY = getWidth() / height;

        for (final GameObject object : objects) {
            g2d.setColor(object.getColor());

            final AffineTransform af = new AffineTransform();
            af.scale(transformationScaleX, transformationScaleY);

            g2d.transform(af);
            g2d.drawPolygon(object.getBody());
        }

        System.out.println(getWidth() + " " + getHeight());
    }
}
