package ca.uoit.crobot;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.ParcelUuid;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import ca.uoit.crobot.event.ConnectionListener;
import ca.uoit.crobot.fragments.DeviceSelectionFragment;
import ca.uoit.crobot.fragments.LidarFragment;
import ca.uoit.crobot.fragments.MapFragment;
import ca.uoit.crobot.fragments.RCFragment;
import ca.uoit.crobot.fragments.SettingsFragment;
import ca.uoit.crobot.adaptors.TabPageAdaptor;
import ca.uoit.crobot.messages.DriveCommand;
import ca.uoit.crobot.messages.LidarReply;
import ca.uoit.crobot.messages.LidarRequest;

public class MainActivity extends AppCompatActivity implements RCFragment.OnRCFragmentInteractionListener,
        DeviceSelectionFragment.OnDeviceSelectionInteractionListener, LidarFragment.OnLidarDataInteractionListener, ConnectionListener {

    private static final String ROBOT_UUID = "396badb4-1837-11e9-ab14-d663bd873d93";

    private final List<BluetoothDevice> deviceList = Collections.synchronizedList(new LinkedList<>());
    private DeviceSelectionFragment deviceSelectionFragment;
    private String deviceAddress;
    private Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final AppViewPager viewPager = findViewById(R.id.view_container);
        final TabPageAdaptor tpa = new TabPageAdaptor(getSupportFragmentManager());

        deviceSelectionFragment = DeviceSelectionFragment.newInstance();

        final RCFragment rcFragment = RCFragment.newInstance();
        final MapFragment mapData = MapFragment.newInstance();
        final SettingsFragment settings = SettingsFragment.newInstance();
        final LidarFragment lidarFragment = LidarFragment.newInstance();

        tpa.addTab(getString(ca.uoit.crobot.R.string.rc), rcFragment);
        tpa.addTab("Connect", deviceSelectionFragment);
        tpa.addTab("Maps", mapData);
        tpa.addTab("Lidar", lidarFragment);
//        tpa.addTab("SettingsFragment", settings);

        viewPager.setAdapter(tpa);
        viewPager.setEnableSwipe(false);

        final TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);

        new Thread(() -> {
            while (true) {
                if (lidarFragment.isVisible() && connection != null) {
                    try {
                        Log.d("lidar", "request...");
                        final LidarReply reply = connection.send(new LidarRequest());
                        Log.d("lidar", "reply...");
                        runOnUiThread(() -> lidarFragment.update(reply.getAngles(), reply.getRanges()));
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
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

                if (device.getUuids() != null) {
                    for (final ParcelUuid parcelUuid : device.getUuids()) {
                        if (ROBOT_UUID.equals(parcelUuid.getUuid().toString())) {
                            deviceList.add(device);
                            deviceSelectionFragment.addDevice(name, address);

                            if (connection != null && connection.isRunning() && address.equals(deviceAddress)) {
                                deviceSelectionFragment.setConnected(address);
                            }
                            break;
                        }
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
    public void onMoveJoystick(int angle, int strength) {
        int speed = (int) (strength * 0.4f + 0.5);
        final DriveCommand.COMMAND command;

        if (angle > 45 && angle < 135) {
            // forward
            command = DriveCommand.COMMAND.FORWARD;
        } else if (angle >= 135 && angle < 135 + 90) {
            // left
            command = DriveCommand.COMMAND.LEFT_TURN;
        } else if (angle >= 225 && angle < 315) {
            // back
            command = DriveCommand.COMMAND.BACKWARD;
            speed = -speed;
        } else {
            // right
            command = DriveCommand.COMMAND.RIGHT_TURN;
        }

        if (connection != null) {
            int finalSpeed = speed;
            new Thread(() -> {
                try {
                    connection.send(new DriveCommand(finalSpeed, command));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }

    @Override
    public void connected(final Connection connection) {
        Log.d("BT", "Connected");
    }

    @Override
    public void disconnected(final Connection connection) {
        Log.d("BT", "Disconnected");
        runOnUiThread(() -> {
            deviceSelectionFragment.setDisconnected();
            Toast.makeText(this, ca.uoit.crobot.R.string.disconnected, Toast.LENGTH_SHORT).show();
            deviceAddress = null;
        });
    }
}
