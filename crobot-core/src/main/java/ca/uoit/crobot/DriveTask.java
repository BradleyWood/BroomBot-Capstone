package ca.uoit.crobot;

import ca.uoit.crobot.task.NavigationTask;
import edu.wlu.cs.levy.breezyslam.components.Position;
import lombok.NonNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static ca.uoit.crobot.CRobot.MAP_SIZE_METERS;
import static ca.uoit.crobot.CRobot.MAP_SIZE_PIXELS;

public class DriveTask extends NavigationTask {

    public static final DriveTask INSTANCE = new DriveTask();

    private static final int TARGETING_RADIUS_MM = 2500;
    private static final int ROBOT_RADIUS_MM = 120;
    private static final int TARGET_SPACING_MM = 100;

    private static final int MM_PER_PIXEL = 5; // TODO: Figure out what this number should be

    private static final LinkedList<Position> targetPath = new LinkedList<>();
    private boolean[][] visited;
    private long initTime;

    private DriveTask() {
        super(1000 / 7 + 50, TimeUnit.MILLISECONDS);
    }

    @Override
    public void init(final @NonNull CRobot robot) {
        visited = new boolean[MAP_SIZE_PIXELS][MAP_SIZE_PIXELS];
        initTime = System.currentTimeMillis();
    }

    @Override
    public boolean activate(final @NonNull CRobot cRobot) {
        // require 5 seconds of buffer time to build a map;
        return System.currentTimeMillis() - 5000 > initTime;
    }

    @Override
    public boolean canInterrupt() {
        return true;
    }

    @Override
    public void run(final @NonNull CRobot robot) {
        try {

            final List<Point> targets = new ArrayList<>();// = getTargets(robot);
            // TODO: Replace this test point with real values
            targets.add(new Point(520, 520));

            while(!targets.isEmpty()) {
                Point target = targets.get(0);
                // Change target units to be in MM
                target.x *= MM_PER_PIXEL;
                target.y *= MM_PER_PIXEL;

                Position pos = robot.getPosition();

                // Calculate the amount the robot needs to turn in degrees
                double dTheta =  pos.theta_degrees + 180 * Math.atan2(target.x - pos.x_mm, pos.y_mm - target.y) / Math.PI;

                // Normalize dTheta
                if(Math.abs(dTheta) > 180) {
                    dTheta = dTheta - (Math.signum(dTheta) * 360);
                }

                // Calculate the distance the robot has to travel
                double distance = Math.sqrt(Math.pow(target.y - pos.y_mm, 2) + Math.pow(target.x - pos.x_mm, 2));

                // Synchronous Drive methods
                robot.getDriveController().turnToAngle(20, dTheta);
                robot.getDriveController().driveToDistance(20, distance);

                // Remove the target from the list
                targets.remove(0);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * Marks the robots position as visited on the grid-map
     *
     * @param robot
     */
    private void markVisited(final @NonNull CRobot robot) {
        final int pixelRadius = toPixel(ROBOT_RADIUS_MM);
        final int rx = robot.getX();
        final int ry = robot.getY();

        if (!mapContains(rx, ry))
            return;

        for (int x = rx - pixelRadius; x < rx + pixelRadius; x++) {
            for (int y = ry - pixelRadius; y < ry + pixelRadius; y++) {
                if (x < 0 || y < 0 || x >= MAP_SIZE_PIXELS || y >= MAP_SIZE_PIXELS)
                    continue;

                visited[x][y] = true;
            }
        }

    }

    private boolean mapContains(final int x, final int y) {
        return x >= 0 && y >= 0 && x < MAP_SIZE_PIXELS && y < MAP_SIZE_PIXELS;
    }

    private boolean mapContains(final @NonNull Position position) {
        return mapContains(toPixel(position.x_mm), toPixel(position.y_mm));
    }

    private int toPixel(final double mm) {
        return (int) (mm / 1000 / MAP_SIZE_METERS * MAP_SIZE_PIXELS);
    }

    private List<Point> getTargets(final @NonNull CRobot robot) {
        // todo;
        final LinkedList<Point> targets = new LinkedList<>();

        final byte[] map = robot.getMap();
        final double mmPerPixel = MAP_SIZE_METERS * 1000 / MAP_SIZE_PIXELS;
        final int spacingPixels = (int) (TARGET_SPACING_MM / mmPerPixel + 0.5);

        // position in pixels not mm
        final int xPos = robot.getX();
        final int yPos = robot.getY();

        markVisited(robot);

        final int startX = xPos + (xPos % spacingPixels < spacingPixels / 2 ?
                -(xPos % spacingPixels) :
                (-xPos) % spacingPixels);
        final int startY = yPos + (yPos % spacingPixels < spacingPixels / 2 ?
                -(yPos % spacingPixels) :
                (-yPos) % spacingPixels);

        for (int x = startX; x < startX + 2 * TARGETING_RADIUS_MM; x += spacingPixels) {
            for (int y = startY; y < startY + 2 * TARGETING_RADIUS_MM; y += spacingPixels) {
                if (x < 0 || y < 0 || x >= MAP_SIZE_PIXELS || y >= MAP_SIZE_PIXELS ||
                        visited[x][y] || (map[y * MAP_SIZE_PIXELS + x] & 0xFF) < 240) {
                    continue;
                }

                targets.add(new Point(x, y));
            }
        }

        return targets;
    }
}
