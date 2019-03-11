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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_rc, container, false);

        view.findViewById(R.id.leftButton).setOnClickListener(l -> mListener.onLeft());
        view.findViewById(R.id.rightButton).setOnClickListener(l -> mListener.onRight());
        view.findViewById(R.id.upButton).setOnClickListener(l -> mListener.onForward());
        view.findViewById(R.id.downButton).setOnClickListener(l -> mListener.onBackward());

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

        void onLeft();

        void onRight();

        void onForward();

        void onBackward();
    }
}
