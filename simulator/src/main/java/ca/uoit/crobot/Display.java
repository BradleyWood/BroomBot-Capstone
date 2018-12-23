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

        final RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        g2d.setRenderingHints(rh);

        g2d.setColor(backgroundColor);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        g2d.setStroke(new BasicStroke(5));

        final float transformationScaleX = Math.min(getWidth(), getHeight()) / width;
        final float transformationScaleY = Math.min(getWidth(), getHeight()) / height;

        for (final GameObject object : objects) {
            g2d.setColor(object.getColor());

            final Polygon body = object.getBody();
            final Rectangle bounds = body.getBounds();

            final AffineTransform af = new AffineTransform();
            af.translate(object.getPosition().getX() / width * getWidth(), object.getPosition().getY() / height * getHeight());
            af.scale(transformationScaleX, transformationScaleY);

            af.rotate(-object.getYaw(), bounds.getCenterX(), bounds.getCenterY());

            g2d.transform(af);
            g2d.drawPolygon(object.getBody());
        }
    }
}
