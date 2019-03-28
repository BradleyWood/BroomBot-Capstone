package ca.uoit.crobot.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import ca.uoit.crobot.R;

public class MainFragment extends Fragment {

    public OnMainFragmentInteractionListener mListener;
    public OnMainFragmentLoadedListener fragmentLoadedListener;

    private Button button;
    private TextView cleaningText;

    private boolean running = false;
    private boolean buttonEnabled = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("MainFragment", "Called onCreateView");
        View parentView = inflater.inflate(R.layout.fragment_main, container, false);

        button = parentView.findViewById(R.id.custom_button);
        cleaningText = parentView.findViewById(R.id.cleaningText);

        button.setEnabled(buttonEnabled);

        button.setOnClickListener(v -> {
            if (mListener != null) {
                running = !running;
                mListener.onToggleDevice(running);
                if (running) {
                    setCleaningText("Cleaning");
                } else {
                    setCleaningText("Press to begin cleaning");
                }
            }
        });

        if(fragmentLoadedListener != null) {
            fragmentLoadedListener.onFragmentLoaded();
        }

        return parentView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnMainFragmentInteractionListener) {
            mListener = (OnMainFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void setButtonEnabled(boolean enabled) {
        buttonEnabled = enabled;
    }

    public void setCleaningText(final String text) {
        cleaningText.setText(text);
    }

    public interface OnMainFragmentLoadedListener {
        void onFragmentLoaded();
    }

    public interface OnMainFragmentInteractionListener {
        void onToggleDevice(boolean enable);
    }
}
