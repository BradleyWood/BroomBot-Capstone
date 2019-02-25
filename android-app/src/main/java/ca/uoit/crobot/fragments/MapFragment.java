package ca.uoit.crobot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import ca.uoit.crobot.R;
import ca.uoit.crobot.components.MapView;

public class MapFragment extends Fragment {

    private MapFragment.OnMapDataInteractionListener mListener;
    private MapView mv;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void update(final int dim, final byte[] map) {
        if (mv != null) {
            mv.setImage(dim, map);
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map_data, container, false);
        mv = view.findViewById(R.id.mapView);

        return view;
    }

    public interface OnMapDataInteractionListener {

    }

}
