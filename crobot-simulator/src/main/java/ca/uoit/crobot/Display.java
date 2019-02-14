package ca.uoit.crobot;

import ca.uoit.crobot.hardware.Lidar;
import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.model.GameObject;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.util.List;

@RequiredArgsConstructor
public class Display extends JPanel {

    @NonNull
    private final List<GameObject> objects;
    @NonNull
    private final Color backgroundColor;

    @NonNull
    private final Lidar lidar;

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

        final int size = Math.min(getWidth(), getHeight());
        final float transformationScaleX = size / width;
        final float transformationScaleY = size / height;

        for (final GameObject object : objects) {
            g2d.setColor(object.getColor());

            final Polygon body = object.getBody();
            final Rectangle bounds = body.getBounds();

            final AffineTransform af = new AffineTransform();

            af.translate(object.getPosition().getX() / width * size / transformationScaleX, object.getPosition().getY() / height * size / transformationScaleY);
            af.rotate(-object.getYaw(), bounds.getCenterX(), bounds.getCenterY());
            af.scale(transformationScaleX, transformationScaleY);

            g2d.transform(af);
            g2d.drawPolygon(object.getBody());
        }

        final AffineTransform af = new AffineTransform();
        af.setToIdentity();
        g2d.transform(af);

        final LidarScan scan = lidar.scan();

        final float[] angles = scan.getAngles();
        final float[] ranges = scan.getRanges();

        try {
            final PrintStream ps = new PrintStream(new FileOutputStream("sim.scan"));

            for (int i = 0; i < angles.length; i++) {
                final float angle = angles[i];

                if (ranges[i] < 0.26 || ranges[i] >= 10) {
                    continue;
                }

                final float dist = ranges[i] / 10 * width;

                ps.println(angle + " " + dist);

                final double x = dist * Math.cos(angle);
                final double y = dist * Math.sin(angle);

//                System.out.println("x " + x + " y " + y + " dist " + dist);

                g2d.setColor(Color.RED);
                g2d.drawOval((int) (x / width * size / transformationScaleX + 0.5), (int) (y / height * size / transformationScaleY + 0.5), 4, 4);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
