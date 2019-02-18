package ca.uoit.crobot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uoit.crobot.R;
import ca.uoit.crobot.components.LidarView;

public class LidarFragment extends Fragment {

    private LidarFragment.OnLidarDataInteractionListener mListener;
    private LidarView lidarView;

    public LidarFragment() {
    }

    public static LidarFragment newInstance() {
        LidarFragment fragment = new LidarFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    public void update(final float[] angles, final float[] ranges) {
        if (lidarView != null) {
            lidarView.setScan(angles, ranges);
        } else {
            System.out.println();
        }
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.frament_lidar_view, container, false);

        lidarView = view.findViewById(R.id.lidarCanvas);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        if (context instanceof OnLidarDataInteractionListener) {
            mListener = (OnLidarDataInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnLidarDataInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnLidarDataInteractionListener {

    }

}
