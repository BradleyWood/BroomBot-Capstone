package ca.uoit.crobot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import ca.uoit.crobot.R;
import ca.uoit.crobot.adaptors.DeviceAdaptor;

public class DeviceSelectionFragment extends Fragment {

    private OnDeviceSelectionInteractionListener mListener;
    private SwipeRefreshLayout swipeRefreshLayout;
    private DeviceAdaptor da;

    public DeviceSelectionFragment() {
    }

    public void setRefreshing(final boolean refreshing) {
        getActivity().runOnUiThread(() -> swipeRefreshLayout.setRefreshing(refreshing));
    }

    public void addDevice(final String name, final String address) {
        da.addDevice(name, address);
    }

    public void remove(final String address) {
        da.remove(address);
    }

    public void setConnected(final String address) {
        if (address == null) {
            da.setSelected(-1);
        } else {
            da.setSelected(address);
        }
    }

    public void setDisconnected() {
        da.setSelected(-1);
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mListener != null && da != null) {
            mListener.onRefresh();
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_device_selection, container, false);
        final ListView listView = view.findViewById(R.id.device_list);

        da = new DeviceAdaptor(view.getContext());
        listView.setAdapter(da);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            da.clear();

            setRefreshing(true);

            if (mListener != null) {
                mListener.onRefresh();
            }
        });

        listView.setOnItemClickListener((parent, itemView, position, id) -> {
            if (itemView instanceof TextView) {
                if (position == da.getSelected()) {
                    mListener.onPressDisconnect();
                    da.setSelected(-1);
                    return;
                }

                synchronized (DeviceSelectionFragment.this) {
                    da.setSelected(position);
                    da.setConnecting(true);

                    new Thread(() -> {
                        final boolean success = mListener.onPressConnect(da.getAddress(position));

                        getActivity().runOnUiThread(() -> {
                            da.setConnecting(false);
                            da.setSelected(success ? position : -1);
                        });

                    }).start();
                }
            }
        });

        return view;
    }

    @Override
    public void onAttach(final Context context) {
        super.onAttach(context);

        if (context instanceof OnDeviceSelectionInteractionListener) {
            mListener = (OnDeviceSelectionInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDeviceSelectionInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnDeviceSelectionInteractionListener {

        void onRefresh();

        boolean onPressConnect(String address);

        void onPressDisconnect();
    }
}
