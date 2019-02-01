package ca.uoit.crobot;


import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.event.MessageListener;
import ca.uoit.crobot.messages.*;
import ca.uoit.crobot.rc.BluetoothServer;

import java.io.IOException;
import java.util.HashMap;

public class Application {

    public static void main(String[] args) {
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

        final BluetoothServer bluetoothServer = new BluetoothServer();

        bluetoothServer.setConnectionListener(connectionListener);
        bluetoothServer.setMessageListener(messageHandler);

        bluetoothServer.start();

//        Drive drive = new Drive(AdafruitDCMotor.MOTOR1, AdafruitDCMotor.MOTOR2);
//
//        drive.init();
//
//        drive.driveDistance(25, 10);
//        drive.turnLeft(25, 90);
//
//        drive.driveDistance(25, 10);
//        drive.turnLeft(25, 90);
//
//        drive.driveDistance(25, 10);
//        drive.turnLeft(25, 90);
//
//        drive.driveDistance(25, 10);
//        drive.turnLeft(25, 90);
    }

    private static final MessageListener messageHandler = new MessageListener() {

        @Override
        public void messageReceived(Connection connection, Message message) {

        }

        @Override
        public void requestReceived(final Connection connection, final Request request, final int rId) {
            Reply reply = null;

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
            }

            try {
                if (reply != null) {
                    connection.send(reply, rId);
                } else {
                    connection.send(new Reply(false, "Unhandled request type: " + request.getClass().getName()), rId);
                }
            } catch (IOException ignored) {
            }
        }
    };

    private static final ConnectionListener connectionListener = new ConnectionListener() {

        @Override
        public void connected(final Connection connection) {

        }

        @Override
        public void disconnected(final Connection connection) {

        }
    };
}
