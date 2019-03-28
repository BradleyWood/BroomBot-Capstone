package ca.uoit.crobot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;

import ca.uoit.crobot.R;

public class SettingsFragment extends Fragment {

    private SettingsFragment.OnSettingsInteractionListener mListener;

    public SettingsFragment() {
    }

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
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
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        Button update = view.findViewById(R.id.updateButton);

        update.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onUpdate();
            }
        });

        Switch upload = view.findViewById(R.id.uploadSwitch);

        //CODE FOR UPLOAD

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnSettingsInteractionListener) {
            mListener = (OnSettingsInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnSettingsInteractionListener");
        }
    }

    public interface OnSettingsInteractionListener {
        void onUpdate();
    }
}
