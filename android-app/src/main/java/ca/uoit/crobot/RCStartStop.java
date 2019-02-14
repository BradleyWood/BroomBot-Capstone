package ca.uoit.crobot;

import android.os.Bundle;
import android.support.v4.app.Fragment;

public class RCStartStop extends Fragment {

    private OnRCStartStopInteractionListener mListener;

    public RCStartStop() {
    }

    public static RCStartStop newInstance() {
        RCStartStop startStop = new RCStartStop();
        Bundle args = new Bundle();

        startStop.setArguments(args);
        return startStop;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public interface OnRCStartStopInteractionListener {

    }

}
