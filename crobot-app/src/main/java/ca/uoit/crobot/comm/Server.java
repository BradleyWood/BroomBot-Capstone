package ca.uoit.crobot.comm;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import ca.uoit.crobot.Connection;
import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.event.MessageListener;

public class Server implements Runnable {

    private ConnectionListener connectionListener;
    private MessageListener messageListener;

    private boolean running = false;

    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setConnectionListener(final ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        try {
            final ServerSocket serverSocket = new ServerSocket(5000);

            while (running) {
                try {
                    final Socket socket = serverSocket.accept();
                    final Connection connection = new Connection(socket, socket.getInputStream(), socket.getOutputStream());

                    if (connectionListener != null) {
                        connection.addConnectionListener(connectionListener);
                    }

                    if (messageListener != null) {
                        connection.setMessageListener(messageListener);
                    }

                    new Thread(connection).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
