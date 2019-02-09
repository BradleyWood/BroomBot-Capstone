package ca.uoit.crobot;

import android.os.Bundle;
import android.app.Activity;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MapData extends Fragment {

    private MapData.OnMapDataInteractionListener mListener;

    public MapData() {
    }

    public static MapData newInstance() {
        MapData fragment = new MapData();
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
        final View view = inflater.inflate(R.layout.fragment_map_data, container, false);
        return view;
    }

    public interface OnMapDataInteractionListener {

    }

}
