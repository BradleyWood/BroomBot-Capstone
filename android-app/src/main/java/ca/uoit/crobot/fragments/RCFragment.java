package ca.uoit.crobot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ToggleButton;

import ca.uoit.crobot.R;
import io.github.controlwear.virtual.joystick.android.JoystickView;


public class RCFragment extends Fragment {

    private OnRCFragmentInteractionListener mListener;

    public RCFragment() {
    }

    public static RCFragment newInstance() {
        RCFragment fragment = new RCFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_rc, container, false);

        final JoystickView joystickView = view.findViewById(R.id.joystick);

        final ToggleButton button = view.findViewById(R.id.toggleButton);

        joystickView.setOnMoveListener((angle, strength) -> {
            if (mListener != null) {
                mListener.onMoveJoystick(angle, strength);
            }
        }, 100);

        button.setOnClickListener(v -> mListener.onToggleDevice(button.isChecked()));

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnRCFragmentInteractionListener) {
            mListener = (OnRCFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnRCFragmentInteractionListener {

        void onToggleDevice(boolean enabled);

        void onMoveJoystick(int angle, int strength);
    }
}
