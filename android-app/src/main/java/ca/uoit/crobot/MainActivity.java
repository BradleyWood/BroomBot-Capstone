package ca.uoit.crobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.fragments.DeviceSelectionFragment;
import ca.uoit.crobot.fragments.MainFragment;
import ca.uoit.crobot.fragments.MapFragment;
import ca.uoit.crobot.fragments.RCFragment;
import ca.uoit.crobot.fragments.SettingsFragment;
import ca.uoit.crobot.messages.*;

public class MainActivity extends AppCompatActivity implements RCFragment.OnRCFragmentInteractionListener,
        ConnectionListener, DeviceSelectionFragment.OnDeviceSelectionInteractionListener,
        MainFragment.OnMainFragmentInteractionListener {

    private static final String ROBOT_UUID = "396badb4-1837-11e9-ab14-d663bd873d93";

    private final List<BluetoothDevice> deviceList = Collections.synchronizedList(new LinkedList<>());
    private DeviceSelectionFragment deviceSelectionFragment;
    private String deviceAddress;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        deviceSelectionFragment = new DeviceSelectionFragment();

        final MainFragment mainFragment = new MainFragment();
        final MapFragment mapsFragment = new MapFragment();
        final RCFragment rcFragment = new RCFragment();
        final SettingsFragment settingsFragment = new SettingsFragment();

        displayFragment(mainFragment);

        final BottomNavigationView navView = findViewById(R.id.navigation);
        navView.setOnNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.navigation_maps) {
                displayFragment(mapsFragment);
            } else if (menuItem.getItemId() == R.id.navigation_rc) {
                displayFragment(rcFragment);
            } else if (menuItem.getItemId() == R.id.navigation_connect) {
                displayFragment(deviceSelectionFragment);
            } else if (menuItem.getItemId() == R.id.navigation_settings) {
                displayFragment(settingsFragment);
            } else {
                return false;
            }

            return true;
        });

        ImageView home = findViewById(R.id.homeButton);

        home.setOnClickListener(v -> displayFragment((mainFragment)));
    }

    @Override
    protected void onResume() {
        super.onResume();

        final IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        registerReceiver(receiver, filter);

        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter != null && !adapter.isDiscovering()) {
            if (!adapter.startDiscovery()) {
                Toast.makeText(MainActivity.this, R.string.discovery_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        unregisterReceiver(receiver);
    }

    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            if (BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {

                final BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                final String name = device.getName();
                final String address = device.getAddress();

                if (name != null && name.contains("rasp")) {
                    deviceList.add(device);
                    deviceSelectionFragment.addDevice(name, address);

                    if (connection != null && connection.isRunning() && address.equals(deviceAddress)) {
                        deviceSelectionFragment.setConnected(address);
                    }
                }
            } else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(intent.getAction())) {
                deviceSelectionFragment.setRefreshing(false);
            }
        }
    };

    @Override
    public void onRefresh() {
        final BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (adapter != null && adapter.isDiscovering()) {
            adapter.cancelDiscovery();
        }

        if (adapter != null && !adapter.startDiscovery()) {
            Toast.makeText(MainActivity.this, R.string.discovery_failed, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public synchronized boolean onPressConnect(final String address) {
        for (final BluetoothDevice device : deviceList) {
            if (address.equals(device.getAddress())) {
                try {
                    final BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString(ROBOT_UUID));

                    if (connection != null) {
                        connection.close();
                    }

                    socket.connect();
                    connection = new Connection(socket, socket.getInputStream(), socket.getOutputStream());
                    connection.addConnectionListener(this);
                    deviceAddress = address;

                    new Thread(connection).start();

                    return true;
                } catch (Exception e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }

        return false;
    }

    @Override
    public synchronized void onPressDisconnect() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
            } catch (Exception ignored) {
            }
        }
    }

    @Override
    public void onToggleDevice(final boolean enabled) {
        try {
            if (enabled) {
                if (connection != null) {
                    connection.send(new DriveCommand(0, DriveCommand.COMMAND.PROGRAM_START));
                }
            } else {
                if (connection != null) {
                    connection.send(new DriveCommand(0, DriveCommand.COMMAND.PROGRAM_STOP));
                }
            }
        } catch (Exception ignored) {
        }
    }

    private <T> T sendRequest(final Request request) {
        if (connection != null) {
            try {
                return (T) connection.send(request);
            } catch (IOException e) {
            }
        }

        return null;
    }

    private void sendMessage(final Message message) {
        if (connection != null) {
            try {
                connection.send(message);
            } catch (IOException ignored) {
            }
        }
    }

    @Override
    public void connected(final Connection connection) {
        Log.d("BT", "Connected");
    }

    @Override
    public void disconnected(final Connection connection) {
        runOnUiThread(() -> {
            deviceSelectionFragment.setDisconnected();
            Toast.makeText(this, ca.uoit.crobot.R.string.disconnected, Toast.LENGTH_SHORT).show();
            deviceAddress = null;
        });
    }

    private void displayFragment(final Fragment fragment) {
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentContainer, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    @Override
    public void onLeft() {
        sendMessage(new DriveCommand(22, DriveCommand.COMMAND.LEFT_TURN));
    }

    @Override
    public void onRight() {
        sendMessage(new DriveCommand(22, DriveCommand.COMMAND.RIGHT_TURN));
    }

    @Override
    public void onForward() {
        sendMessage(new DriveCommand(30, DriveCommand.COMMAND.FORWARD));
    }

    @Override
    public void onBackward() {
        sendMessage(new DriveCommand(30, DriveCommand.COMMAND.BACKWARD));
    }

    public void setBattery(final int percentage) {
        final ProgressBar progressBar = findViewById (R.id.battery);
        progressBar.setProgress(percentage);

        final TextView textview = findViewById (R.id.batteryText);
        textview.setText(String.format("%d", percentage));
    }
}
