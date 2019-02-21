package ca.uoit.crobot.util;

import java.io.*;

public class LibraryUtils {

    public static void loadLibrary(final String libName) {
        final String arch = System.getProperty("os.arch").toLowerCase();
        final String ext = getExtension();
        final String fileName = libName + "_" + arch + ext;

        final File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        final File outputFile = new File(tmpDir, fileName);

        if (!outputFile.exists()) {
            final InputStream in = LibraryUtils.class.getClassLoader().getResourceAsStream(fileName);

            if (in == null) {
                final File file = new File(new File("crobot-core/src/main/resources/"), fileName);
                System.load(file.getAbsolutePath());

                return;
            }

            try (FileOutputStream fos = new FileOutputStream(outputFile)) {
                final byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.load(outputFile.getAbsolutePath());
        outputFile.deleteOnExit();
    }

    private static String getExtension() {
        final String os = System.getProperty("os.name");

        if (os.toLowerCase().contains("windows")) {
            return ".dll";
        } else if (os.toLowerCase().contains("linux")) {
            return ".so";
        }

        throw new RuntimeException("Operating system: " + os + " is not supported.");
    }

}
