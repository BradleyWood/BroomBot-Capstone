package ca.uoit.crobot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import ca.uoit.crobot.R;
import ca.uoit.crobot.components.MapView;

public class MapFragment extends Fragment {

    private MapFragment.OnMapDataInteractionListener mListener;

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

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_map_view, container, false);

        ImageView map = view.findViewById(R.id.map);

        //SET UP MAP IMAGE HERE

        TextView area = view.findViewById(R.id.area);

        area.setText(""); //SET AREA HERE

        return view;
    }

    public interface OnMapDataInteractionListener {

    }
}
