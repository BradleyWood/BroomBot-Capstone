package ca.uoit.crobot;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;

public class Connection implements Runnable, AutoCloseable {

    private final BluetoothDevice device;
    private final BluetoothSocket socket;

    public Connection(final BluetoothDevice device, final BluetoothSocket socket) {
        this.device = device;
        this.socket = socket;
    }

    public String getDeviceName() {
        return device.getName();
    }

    public String getDeviceAddress() {
        return device.getAddress();
    }

    @Override
    public void run() {
        try {
            final InputStream in = socket.getInputStream();

            // todo
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket.isConnected();
    }

    @Override
    public void close() throws Exception {
        socket.close();
    }
}
