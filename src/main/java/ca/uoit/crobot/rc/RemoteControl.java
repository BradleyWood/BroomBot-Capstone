package ca.uoit.crobot.rc;

import ca.uoit.crobot.hardware.Motor;
import fi.iki.elonen.NanoHTTPD;

import java.io.IOException;
import java.util.Map;

public class RemoteControl extends NanoHTTPD implements Runnable {

    private long lastRequest = 0;

    private final Motor left;
    private final Motor right;

    public RemoteControl(final int port, final Motor left, final Motor right) throws IOException {
        super(port);
        start(NanoHTTPD.SOCKET_READ_TIMEOUT, false);
        this.left = left;
        this.right = right;

        new Thread(this).start();
    }

    @Override
    public Response serve(final IHTTPSession session) {
        try {
            if (session.getMethod() == Method.GET) {
                final Map<String, String> parameters = session.getParms();

                int leftSpeed = Integer.parseInt(parameters.get("left"));
                int rightSpeed = Integer.parseInt(parameters.get("right"));

                System.out.println("Request, left=" + leftSpeed + ", right=" + rightSpeed);

                if (Math.abs(leftSpeed) <= 100 && Math.abs(rightSpeed) <= 100) {
                    lastRequest = System.currentTimeMillis();
                    left.setSpeed(leftSpeed);
                    right.setSpeed(rightSpeed);

                    return newFixedLengthResponse(Response.Status.OK, "text", "");
                }

                return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text",
                        "speed must be in range [-100, 100]");
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return newFixedLengthResponse(Response.Status.BAD_REQUEST, "text", "NaN");
        }

        return newFixedLengthResponse(Response.Status.NOT_FOUND, "text", "");
    }

    @Override
    public void run() {
        while (true) {
            try {
                if (lastRequest + 500 < System.currentTimeMillis()) {
                    // no request in the last 500 ms - turn off motors
                    left.stop();
                    right.stop();
                }
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }
        }
    }
}
