package ca.uoit.crobot.adaptors;

import android.content.Context;
import android.graphics.Color;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DeviceAdaptor extends BaseAdapter {

    private final List<Pair<String, String>> devices = new ArrayList<>();
    private final LayoutInflater inflater;
    private int selected = -1;
    private boolean connecting = false;

    public DeviceAdaptor(final Context context) {
        inflater = LayoutInflater.from(context);
    }

    public void setSelected(final int position) {
        selected = position;
        notifyDataSetChanged();
    }

    public int getSelected() {
        return selected;
    }

    public void setConnecting(final boolean connecting) {
        this.connecting = connecting;
        notifyDataSetChanged();
    }

    public String getAddress(final int position) {
        return devices.get(position).second;
    }

    public void setSelected(final String address) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).second.equals(address)) {
                setSelected(i);
                break;
            }
        }
    }

    public void addDevice(final String name, final String address) {
        final Pair<String, String> pair = new Pair<>(name, address);

        if (!devices.contains(pair)) {
            devices.add(new Pair<>(name, address));
            notifyDataSetChanged();
        }
    }

    public void remove(final String address) {
        for (int i = 0; i < devices.size(); i++) {
            if (devices.get(i).second.equals(address)) {
                remove(i);
                break;
            }
        }
    }

    public void remove(final int position) {
        if (position == selected) {
            selected = -1;
        }

        devices.remove(position);
        notifyDataSetChanged();
    }

    public void clear() {
        devices.clear();
        selected = -1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return devices.size();
    }

    @Override
    public Object getItem(int position) {
        return devices.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);

            if (convertView instanceof TextView) {
                final TextView view = (TextView) convertView;
                view.setText(devices.get(position).first);
            }
        }

        if (position == selected) {
            if (connecting) {
                convertView.setBackgroundColor(Color.YELLOW);
            } else {
                convertView.setBackgroundColor(Color.BLUE);
            }
        } else {
            convertView.setBackgroundColor(Color.WHITE);
        }

        return convertView;
    }
}
