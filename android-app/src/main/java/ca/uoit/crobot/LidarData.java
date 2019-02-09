package ca.uoit.crobot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class LidarData extends Fragment {

    private LidarData.OnLidarDataInteractionListener mListener;

    public LidarData() {
    }

    public static LidarData newInstance() {
        LidarData fragment = new LidarData();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.activity_lidar_data, container, false);
        return view;
    }

    public interface OnLidarDataInteractionListener {

    }

}
