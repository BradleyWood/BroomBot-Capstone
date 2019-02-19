package ca.uoit.crobot.hardware;

import ca.uoit.crobot.SimulationEntity;
import ca.uoit.crobot.model.GameObject;
import edu.wlu.cs.levy.breezyslam.components.Laser;
import lombok.Data;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public @Data class SimulatedLidar implements Lidar, SimulationEntity {

    private final List<GameObject> objects;
    private final GameObject robot;
    private final float rayDistance;
    private final int numRays;

    @Override
    public LidarScan scan() {
        final float anglePerRay = 2f * (float) Math.PI / numRays;
        final float[] angles = new float[numRays];
        final float[] ranges = new float[numRays];

        final List<Polygon> polygons = new LinkedList<>();

        for (final GameObject object : objects) {
            if (object == robot)
                continue;

            if (Math.abs(object.getPosition().getX()) < 0.000001 & Math.abs(object.getPosition().getY()) < 0.000001
                    && Math.abs(object.getYaw()) < 0.000001) {
                polygons.add(object.getBody());
                continue;
            }

            final Polygon body = object.getBody();
            final double centerX = body.getBounds().getCenterX();
            final double centerY = body.getBounds().getCenterY();

            final int[] xpoints = Arrays.copyOf(body.xpoints, body.npoints);
            final int[] ypoints = Arrays.copyOf(body.ypoints, body.npoints);

            for (int j = 0; j < body.npoints; j++) {
                final int x = xpoints[j];
                final int y = ypoints[j];
                final double dist = Math.sqrt((centerY - y) * (centerY - y) + (centerX - x) * (centerX - x));
                final double newAngle = Math.atan2(centerY - y, centerX - x) + object.getYaw();
                xpoints[j] = (int) (dist * Math.cos(newAngle) + object.getPosition().getX() + 0.5);
                ypoints[j] = (int) (dist * Math.sin(newAngle) + object.getPosition().getY() + 0.5);
            }

            final Polygon translatedBody = new Polygon(xpoints, ypoints, xpoints.length);

            if (!object.equals(robot)) {
                polygons.add(translatedBody);
            }
        }

        final Point2D.Double robotPos = new Point2D.Double(robot.getPosition().getX(), robot.getPosition().getY());

        for (int i = 0; i < numRays; i++) {
            final float angle = i * anglePerRay;
            float dist = rayDistance;

            for (final Polygon polygon : polygons) {
                final double nx = robotPos.x + rayDistance * Math.cos(angle);
                final double ny = robotPos.y + rayDistance * Math.sin(angle);
                final Point2D.Double rayEnd = new Point2D.Double(nx, ny);
                final Point2D closestIntersection = getClosestIntersection(polygon, robotPos, rayEnd);

                if (closestIntersection != null && robotPos.distance(closestIntersection) < dist) {
                    dist = (float) robotPos.distance(closestIntersection);
                }
            }

            angles[i] = angle;
            ranges[i] = dist / rayDistance * 10f;
        }

        return new LidarScan(angles, ranges);
    }

    @Override
    public void update() {

    }

    private Point2D getClosestIntersection(final Polygon obj, final Point2D a, final Point2D b) {
        final List<Point2D> intersections = getIntersections(obj, a, b);

        double dist = Double.POSITIVE_INFINITY;
        Point2D result = null;

        for (final Point2D intersection : intersections) {
            if (intersection.distance(a) < dist && dist > 0.0000001) {
                result = intersection;
                dist = intersection.distance(a);
            }
        }

        return result;
    }

    private LinkedList<Point2D> getIntersections(final Polygon obj, final Point2D a, final Point2D b) {
        final LinkedList<Point2D> list = new LinkedList<>();

        for (int i = 0; i < obj.npoints - 1; i++) {
            if (obj.xpoints[i] == a.getX() && obj.ypoints[i] == a.getY() || obj.xpoints[i + 1] == a.getX() && obj.ypoints[i + 1] == a.getY()
                    || obj.xpoints[i] == b.getX() && obj.ypoints[i] == b.getY() || obj.xpoints[i + 1] == b.getX() && obj.ypoints[i + 1] == b.getY())
                continue;

            final Line2D edge = getLineFromIndices(obj, i, i + 1);
            final Point2D intersectionPt = getIntersection(new Line2D.Double(a, b), edge);

            if (intersectionPt != null)
                list.add(intersectionPt);
        }

        if (obj.xpoints[0] == a.getX() && obj.ypoints[0] == a.getY() || obj.xpoints[obj.npoints - 1] == a.getX() && obj.ypoints[obj.npoints - 1] == a.getY()
                || obj.xpoints[0] == b.getX() && obj.ypoints[0] == b.getY() || obj.xpoints[obj.npoints - 1] == b.getX() && obj.ypoints[obj.npoints - 1] == b.getY())
            return list;

        final Line2D.Double edge = new Line2D.Double(obj.xpoints[obj.npoints - 1], obj.ypoints[obj.npoints - 1], obj.xpoints[0], obj.ypoints[0]);
        final Point2D intersectionPt = getIntersection(new Line2D.Double(a, b), edge);

        if (intersectionPt != null)
            list.add(intersectionPt);

        return list;
    }

    private Line2D getLineFromIndices(final Polygon obj, final int a, final int b) {
        if (a == b || a > obj.npoints || b > obj.npoints || a < 0 || b < 0)
            throw new IllegalArgumentException("Invalid line segment");

        final Point2D.Double ptA = new Point2D.Double(obj.xpoints[a], obj.ypoints[a]);
        final Point2D.Double ptB = new Point2D.Double(obj.xpoints[b], obj.ypoints[b]);

        return new Line2D.Double(ptA, ptB);
    }

    private static Point2D getIntersection(final Line2D a, final Line2D b) {
        double x = ((a.getX2() - a.getX1()) * (b.getX1() * b.getY2() - b.getX2() * b.getY1()) -
                (b.getX2() - b.getX1()) * (a.getX1() * a.getY2() - a.getX2() * a.getY1()))
                / ((a.getX1() - a.getX2()) * (b.getY1() - b.getY2()) - (a.getY1() - a.getY2()) * (b.getX1() - b.getX2()));

        double y = ((b.getY1() - b.getY2()) * (a.getX1() * a.getY2() - a.getX2() * a.getY1()) -
                (a.getY1() - a.getY2()) * (b.getX1() * b.getY2() - b.getX2() * b.getY1()))
                / ((a.getX1() - a.getX2()) * (b.getY1() - b.getY2()) - (a.getY1() - a.getY2()) * (b.getX1() - b.getX2()));

        double minXa = Math.min(a.getX1(), a.getX2());
        double minXb = Math.min(b.getX1(), b.getX2());
        double maxXa = Math.max(a.getX1(), a.getX2());
        double maxXb = Math.max(b.getX1(), b.getX2());
        double minYa = Math.min(a.getY1(), a.getY2());
        double minYb = Math.min(b.getY1(), b.getY2());
        double maxYa = Math.max(a.getY1(), a.getY2());
        double maxYb = Math.max(b.getY1(), b.getY2());

        // check that the intersection is within the domain and range of each line segment
        if (x >= minXa && x >= minXb && x <= maxXa && x <= maxXb && y >= minYa && y >= minYb && y <= maxYa && y <= maxYb) {
            return new Point2D.Double(x, y);
        }

        return null;
    }

    @Override
    public void shutdown() {

    }

    @Override
    public void rotate() {

    }

    @Override
    public void stopRotation() {

    }

    @Override
    public Laser getLaserConfig() {
        return null;
    }

    @Override
    public void init() {

    }
}
