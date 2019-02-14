package ca.uoit.crobot;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Settings extends Fragment {

    private Settings.OnSettingsInteractionListener mListener;

    public Settings() {
    }

    public static Settings newInstance() {
        Settings fragment = new Settings();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /** Button history = (Button)findViewById(R.id.historybutton);

        history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Settings.this, LidarData.class));
            }
        }); */

    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_settings, container, false);

        return view;
    }

    public interface OnSettingsInteractionListener {

    }
}
