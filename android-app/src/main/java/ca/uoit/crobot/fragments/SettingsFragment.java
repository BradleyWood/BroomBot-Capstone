package ca.uoit.crobot.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import ca.uoit.crobot.MainActivity;
import ca.uoit.crobot.R;

public class SettingsFragment extends Fragment {

    private SettingsFragment.OnSettingsInteractionListener mListener;
    private static final String BASE_URL = "http://35.243.160.240:9090";

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

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String appVersion;
                appVersion = getApplicationVersion();

                String version;
                version = getLatestVersion();



                if (appVersion != version) {
                    update(version);
                    Toast.makeText(getContext(), "Updating Robot to Latest Version", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Already Updated to Latest Version", Toast.LENGTH_SHORT).show();
                }


            }
        });

        Switch upload = view.findViewById(R.id.uploadSwitch);

        //CODE FOR UPLOAD

        return view;
    }

    public interface OnSettingsInteractionListener {

    }

    public static File getApplicationFile() {
        return new File(SettingsFragment.class.getProtectionDomain()
                .getCodeSource()
                .getLocation()
                .getPath());
    }

    public static void updateToLatest() {
        final String latestVersion = getLatestVersion();

        if (latestVersion != null) {
            update(latestVersion);
        }
    }

    public static void update(final String version) {
        final File file = getApplicationFile();
        final boolean success = download(BASE_URL + "/download?version=" + version, file);

        if (success) {
            try {
                Runtime.getRuntime().exec("java -jar " + file.getAbsolutePath());
                System.exit(0);
            } catch (IOException e) {
            }
        }
    }

    public static String getLatestVersion() {
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();

        if (!download(BASE_URL + "/version", baos)) {
            return null;
        }

        return baos.toString();
    }

    public static boolean download(final String link, final OutputStream os) {
        try (final BufferedInputStream in = new BufferedInputStream(new URL(link).openStream())) {
            final byte[] buf = new byte[1024];

            int len;
            while ((len = in.read(buf, 0, 1024)) != -1) {
                os.write(buf, 0, len);
            }

            os.close();
        } catch (IOException e) {
            return false;
        }

        return true;
    }

    public static boolean download(final String link, final File destinationPath) {
        try {
            return download(link, new FileOutputStream(destinationPath));
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    public static boolean download(final String link, final String destinationPath) {
        return download(link, new File(destinationPath));
    }

    public static String getApplicationVersion() {
        try {
            final Enumeration<URL> resources = SettingsFragment.class.getClassLoader()
                    .getResources("META-INF/MANIFEST.MF");
            while (resources.hasMoreElements()) {
                final Manifest manifest = new Manifest(resources.nextElement().openStream());
                final Attributes attributes = manifest.getMainAttributes();

                return attributes.getValue("crobot-version");
            }
        } catch (IOException e) {
        }

        return null;
    }

}
