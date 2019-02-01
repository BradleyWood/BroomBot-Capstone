package ca.uoit.crobot;

import java.io.*;
import java.net.URL;
import java.util.Enumeration;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class Utility {

    private static final String BASE_URL = "http://35.243.160.240:9090";

    public static File getApplicationFile() {
        return new File(Utility.class.getProtectionDomain()
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
            final Enumeration<URL> resources = Utility.class.getClassLoader()
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
