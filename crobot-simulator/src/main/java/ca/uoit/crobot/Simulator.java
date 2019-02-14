package ca.uoit.crobot;

import ca.uoit.crobot.hardware.*;
import ca.uoit.crobot.model.GameObject;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

public class Simulator {

    public static void main(String[] args) throws InterruptedException, IOException {
        final JFrame frame = new JFrame("Crobot Simulator");
        final List<GameObject> objects = new LinkedList<>();

        final int[] xPointsBox = new int[] { 10, 100, 990, 500, 400, 990, 10};
        final int[] yPointsBox = new int[] { 10, 100, 10, 600, 750, 990, 990};

        final int[] xPoints = new int[] { 0, 25, 50, 75, 75, 50, 38, 25, 0};
        final int[] yPoints = new int[] { 25, 0, 0, 25, 50, 75, 100, 75, 50};

        final GameObject wall = new GameObject(new Polygon(xPointsBox, yPointsBox, 7), Color.BLACK);
        objects.add(wall);

        final GameObject robot = new GameObject(new Polygon(xPoints, yPoints, 9), Color.BLACK);
        final SimulatedMotor left = new SimulatedMotor(robot);
        final SimulatedMotor right = new SimulatedMotor(robot);
        left.setOtherMotor(right);
        right.setOtherMotor(left);

        robot.getPosition().setX(200);
        robot.getPosition().setY(250);

        objects.add(robot);

        final SimulatedLidar lidar = new SimulatedLidar(objects, robot, 1000, 600);

        final Display display = new Display(objects, Color.GRAY, lidar, 1000, 1000);
        frame.add(display);

        frame.pack();
        frame.setSize(600, 600);

        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);

        Thread.sleep(1000);
        System.out.println("Initialized...");



//        robot(left, right, lidar);
//        left.setSpeed(30);
//        right.setSpeed(30);

//        LidarScan scan = lidar.scan();
//        final Map map = CRSlam.createMap(1000, 0.5);
//
//        CRSlam.ts_map_update(scan, map, new Position(robot.getPosition().getX(), robot.getPosition().getY(), robot.getYaw()), 50);
//
//        robot.getPosition().setX(200);
//        robot.getPosition().setX(400);
//        scan = lidar.scan();
//
//        ImageIO.write(map.toImage(), "png", new File("img1.png"));
//
//        CRSlam.ts_map_update(scan, map, new Position(robot.getPosition().getX(), robot.getPosition().getY(), robot.getYaw()), 50);
//
//        ImageIO.write(map.toImage(), "png", new File("img2.png"));

//        while (true) {
//            Thread.sleep(50);
//            objects.forEach(GameObject::update);
//            left.update();
//            right.update();
//            display.repaint();
//        }
    }

    private enum ACTION {
        STOP,
        FORWARD,
        TURN_LEFT,
        TURN_RIGHT
    }
}
