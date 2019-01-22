package ca.uoit.crobot;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkTask extends AsyncTask<String, Void, Void> {

    private static boolean flag = false;

    @Override
    protected Void doInBackground(final String... strings) {
        if (!flag) {
            flag = true;

            try {
                sendRequest(strings[0]);
            } catch (IOException e) {
            }

            flag = false;
        }

        return null;
    }

    private void sendRequest(final String requestUrl) throws IOException {
        final URL url = new URL(requestUrl);
        final HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        final InputStream inputStream = connection.getInputStream();

        final byte[] buffer = new byte[128];

        // read the response
        while (inputStream.read(buffer) >= 0);

        inputStream.close();
    }

    public static void send(final String url) {
        new NetworkTask().execute(url);
    }

}
