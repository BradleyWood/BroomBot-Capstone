package ca.uoit.crobot;

import org.nustaq.serialization.FSTConfiguration;

import lombok.Data;
import lombok.NonNull;
import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.event.MessageListener;
import ca.uoit.crobot.messages.Message;
import ca.uoit.crobot.messages.Request;
import ca.uoit.crobot.messages.Reply;

import java.io.*;
import java.util.*;

/**
 * Handles all networking IO between client, server
 */
public @Data class Connection implements Runnable, AutoCloseable {

    private static final FSTConfiguration conf = FSTConfiguration.createDefaultConfiguration();

    private final List<ConnectionListener> connectionListeners = new LinkedList<>();
    private final Map<Integer, Reply> responses = Collections.synchronizedMap(new HashMap<>());
    private MessageListener messageListener;

    private static int counter = 0;

    private final DataOutputStream dos;
    private final DataInputStream dis;
    private final AutoCloseable socket;

    private boolean isRunning;

    /**
     * Construct a connection from a socket
     *
     * @param socket The socket
     */
    public Connection(final AutoCloseable socket, final InputStream in, final OutputStream out) {
        this.socket = socket;
        this.dis = new DataInputStream(in);
        this.dos = new DataOutputStream(out);
    }

    /**
     * Send a synchronous request to the server.
     * This method is blocking and will sleep until
     * a response is received from the server.
     *
     * @param request
     * @param <T>     The expected response type
     * @return The response
     * @throws IOException
     */
    public <T extends Reply> T send(final Request request) throws IOException {
        final int req = counter++;

        sendMessage(request, req);

        while (!responses.containsKey(req)) {
            synchronized (this) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }
        }

        return (T) responses.remove(req);
    }

    /**
     * Send a message that does not require a response.
     *
     * @param message
     * @throws IOException
     */
    public void send(final Message message) throws IOException {
        sendMessage(message, -1);
    }

    /**
     * Sends a to a specific request
     *
     * @param response The response
     * @param rId      The id of the response message
     * @throws IOException
     */
    public void send(final Reply response, final int rId) throws IOException {
        sendMessage(response, rId);
    }

    /**
     * Send a message
     *
     * @param obj The message to send
     * @param rId The request id
     * @throws IOException
     */
    private void sendMessage(final @NonNull Message obj, final int rId) throws IOException {
        final byte[] message = conf.asByteArray(obj);

        synchronized (dos) {
            dos.writeInt(message.length);
            dos.writeInt(rId);
            dos.write(message);
        }
    }

    /**
     * Adds a packet listener the Client.
     *
     * @param messageListener The packet listener
     */
    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    /**
     * Adds a connection listener
     *
     * @param listener The connection listener to add
     */
    public void addConnectionListener(final @NonNull ConnectionListener listener) {
        connectionListeners.add(listener);
    }

    /**
     * Removes a connection listener
     *
     * @param listener The listener to remove
     */
    public void removeConnectionListener(final @NonNull ConnectionListener listener) {
        connectionListeners.remove(listener);
    }

    /**
     * Closes the socket and stops listening for packets
     *
     * @throws IOException if the socket is already closed
     */
    public void destroy() throws Exception {
        isRunning = false;
        socket.close();
    }

    @Override
    public void run() {
        isRunning = true;

        for (final ConnectionListener listener : connectionListeners) {
            listener.connected(this);
        }

        while (isRunning) {
            try {
                final int messageLength = dis.readInt();
                final int rId = dis.readInt();
                final byte[] bytes = new byte[messageLength];

                dis.readFully(bytes);

                final Message obj = (Message) conf.asObject(bytes);

                if (obj instanceof Reply) {
                    responses.put(rId, (Reply) obj);

                    synchronized (this) {
                        notifyAll();
                    }

                    continue;
                }

                final boolean isRequest = obj instanceof Request && rId >= 0;

                try {
                    if (isRequest && messageListener != null) {
                        messageListener.requestReceived(this, (Request) obj, rId);
                    } else if (messageListener != null) {
                        messageListener.messageReceived(this, obj);
                    }
                } catch (final Throwable e) {
                    if (isRequest) {
                        send(new Reply(false, e.getMessage()) {
                        }, rId);
                    }
                }
            } catch (IOException e) {
                isRunning = false;
            } catch (Throwable e) {
                System.err.println("Internal Error");
            }
        }

        try {
            socket.close();
        } catch (Exception ignored) {
        }

        synchronized (this) {
            notifyAll();
        }

        for (final ConnectionListener listener : connectionListeners) {
            listener.disconnected(this);
        }
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}