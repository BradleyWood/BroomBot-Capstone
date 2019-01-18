package ca.uoit.crobot.rc;

import javax.bluetooth.DiscoveryAgent;
import javax.bluetooth.LocalDevice;
import javax.bluetooth.UUID;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

import ca.uoit.crobot.Connection;
import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.event.MessageListener;

public class BluetoothServer implements Runnable {

    private static final String UUID = "396badb4183711e9ab14d663bd873d93";

    private ConnectionListener connectionListener;
    private MessageListener messageListener;

    public void setMessageListener(final MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    public void setConnectionListener(final ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }

    @Override
    public void run() {
        StreamConnectionNotifier notifier;

        try {
            final LocalDevice local = LocalDevice.getLocalDevice();
            local.setDiscoverable(DiscoveryAgent.GIAC);

            UUID uuid = new UUID(UUID, false);
            System.out.println(uuid.toString());

            String url = "btspp://localhost:" + uuid.toString() + ";name=CleaningRobot";

            notifier = (StreamConnectionNotifier) Connector.open(url);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        while (true) {
            try {
                final StreamConnection sc = notifier.acceptAndOpen();

                final Connection connection = new Connection(sc::close, sc.openInputStream(), sc.openOutputStream());

                if (connectionListener != null) {
                    connection.addConnectionListener(connectionListener);
                }

                if (messageListener != null) {
                    connection.setMessageListener(messageListener);
                }

                new Thread(connection).start();

            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
