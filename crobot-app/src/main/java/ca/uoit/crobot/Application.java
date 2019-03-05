package ca.uoit.crobot;


import ca.uoit.crobot.comm.BluetoothServer;
import ca.uoit.crobot.comm.Server;
import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.event.MessageListener;
import ca.uoit.crobot.hardware.AdafruitDCMotor;
import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.hardware.X4Lidar;
import ca.uoit.crobot.messages.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.TimeoutException;

public class Application {

    private static final CRobot robot = new CRobot(AdafruitDCMotor.MOTOR1, AdafruitDCMotor.MOTOR2, new X4Lidar());
    private static long lastDriveCommand = 0;

    public static void main(String[] args) throws InterruptedException, TimeoutException {
        if (args.length > 0 && "--update".equals(args[0])) {
            if (args.length > 1) {
                Utility.update(args[1]);
            } else {
                Utility.updateToLatest();
            }

            // process should be dead if successful

            System.err.println("Failed to complete update");
            return;
        }

        robot.init();

        final Server server = new Server();
        final BluetoothServer bts = new BluetoothServer();

        server.setConnectionListener(connectionListener);
        server.setMessageListener(messageHandler);
        server.start();

        bts.setConnectionListener(connectionListener);
        bts.setMessageListener(messageHandler);
        bts.start();

        while (true) {

            Thread.sleep(500);
        }
    }

    private static final MessageListener messageHandler = new MessageListener() {

        @Override
        public void messageReceived(final Connection connection, final Message message) {
            if (message instanceof DriveCommand) {
                final DriveCommand command = (DriveCommand) message;
                lastDriveCommand = System.currentTimeMillis();

                if (command.getCommand() == DriveCommand.COMMAND.FORWARD) {
                    robot.getDriveController().drive(command.getSpeed());
                } else if (command.getCommand() == DriveCommand.COMMAND.BACKWARD) {
                    robot.getDriveController().drive(command.getSpeed());
                } else if (command.getCommand() == DriveCommand.COMMAND.LEFT_TURN) {
                    robot.getDriveController().turnLeft(command.getSpeed());
                } else if (command.getCommand() == DriveCommand.COMMAND.RIGHT_TURN) {
                    robot.getDriveController().turnRight(command.getSpeed());
                } else if (command.getCommand() == DriveCommand.COMMAND.PROGRAM_START && !robot.isRunning()) {
                    robot.start();
                } else if (command.getCommand() == DriveCommand.COMMAND.PROGRAM_STOP && robot.isRunning()) {
                    robot.stop();
                }
            }
        }

        @Override
        public void requestReceived(final Connection connection, final Request request, final int rId) {
            Reply reply = null;

            System.out.println(request);
            if (request instanceof StatusRequest) {
                // no extra attributes as of yet
                reply = new StatusReply(new HashMap<>(), Utility.getApplicationVersion());
            } else if (request instanceof UpdateRequest) {
                final String version = ((UpdateRequest) request).getVersion();

                if (version == null) {
                    Utility.updateToLatest();
                } else {
                    Utility.update(version);
                }

                reply = new Reply(false, "Failed to update");
            } else if (request instanceof LidarRequest) {
                final LidarScan scan = robot.getScan();

                if (scan != null) {
                    reply = new LidarReply(scan.getAngles(), scan.getRanges());
                } else {
                    reply = new LidarReply(new float[0], new float[0]);
                }
            } else if (request instanceof MapRequest) {
                robot.mapRefresh();
                final byte[] map = robot.getMap();
                reply = new MapReply(CRobot.MAP_SIZE_PIXELS, CRobot.MAP_SIZE_METERS, map, robot.getX(), robot.getY());
            }

            try {
                if (reply != null) {
                    connection.send(reply, rId);
                } else {
                    connection.send(new Reply(false, "Unhandled request type: " + request.getClass().getName()), rId);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    private static final ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void connected(final Connection connection) {
            System.out.println("Connected");
        }

        @Override
        public void disconnected(final Connection connection) {
            System.out.println("Disconnected");
        }
    };
}

