package ca.uoit.crobot;

import ca.uoit.crobot.messages.MapReply;
import ca.uoit.crobot.messages.MapRequest;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class MapTest {

    public static void main(String[] args) throws IOException, InterruptedException {
        final Socket socket = new Socket("raspberry-pi", 5000);
        final Connection connection = new Connection(socket, socket.getInputStream(), socket.getOutputStream());
        new Thread(connection).start();

        final JFrame frame = new JFrame();

        final MapView mv = new MapView();
        mv.setSize(600, 600);

        frame.add(mv);
        frame.pack();

        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 600);

        while (true) {
            final MapReply reply = connection.send(new MapRequest());

            mv.size = reply.getMAP_SIZE_PIXELS();
            mv.mapBytes = reply.getMap();

            if (reply.getY() >= 0) {
                mv.points.add(new Point(reply.getX(), reply.getY()));
            }

            mv.repaint();
            Thread.sleep(50);
        }
    }

    private static class MapView extends JPanel {

        List<Point> points = new ArrayList<>();
        byte[] mapBytes = null;
        int size = 0;

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (mapBytes != null) {
                BufferedImage result = new BufferedImage(size, size, BufferedImage.TYPE_BYTE_GRAY);
                System.arraycopy(mapBytes, 0, ((DataBufferByte) result.getRaster().getDataBuffer()).getData(), 0, mapBytes.length);
                g.drawImage(result, 0, 0, result.getWidth(), result.getHeight(), null);

                if (!points.isEmpty()) {
                    Point last = points.get(0);

                    for (int i = 1; i < points.size(); i++) {
                        g.setColor(Color.RED);
                        Point next = points.get(i);
                        g.drawLine(last.x, last.y, next.x, next.y);
                        last = next;
                    }
                }

                try {
                    ImageIO.write(result, "png", new File("map.png"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                g.setColor(Color.BLACK);
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        }
    }
}
