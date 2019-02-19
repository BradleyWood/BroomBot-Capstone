package ca.uoit.crobot;

import ca.uoit.crobot.hardware.LidarScan;
import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;

import java.util.concurrent.*;

public class CRobot {

    public static final int MAP_SIZE_PIXELS = 1000;
    public static final int MAP_SIZE_METERS = 14;

    private final DeviceController deviceController;

    private ScheduledExecutorService executorService;
    private RMHCSLAM rmhcslam;
    private LidarScan scan;
    private byte[] map;

    public CRobot(final DeviceController deviceController) {
        this.deviceController = deviceController;
    }

    public void start() {
        if (executorService != null && !executorService.isShutdown())
            throw new IllegalStateException();

        rmhcslam = new RMHCSLAM(deviceController.getLidar().getLaserConfig(), MAP_SIZE_PIXELS, MAP_SIZE_METERS,
                (int) System.currentTimeMillis());

        executorService = Executors.newScheduledThreadPool(4);
        executorService.submit(this::run);
    }

    public void stop() {
        executorService.shutdownNow();
        deviceController.stop();
    }

    private void run() {
        deviceController.getLidar().rotate();

        final double scanRate = deviceController.getLidar().getLaserConfig().getScanRate();

        executorService.scheduleAtFixedRate(scanUpdate, 0, (long) (1000 / scanRate), TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(mapUpdate, (long) (1000 / scanRate) + 50,
                (long) (1000 / scanRate), TimeUnit.MILLISECONDS);

        // todo;
    }

    private Runnable scanUpdate = new Runnable() {
        @Override
        public void run() {
            final LidarScan scan = deviceController.getLidar().scan();

            synchronized (this) {
                CRobot.this.scan = scan;
            }
        }
    };

    private Runnable mapUpdate = new Runnable() {

        private LidarScan lastScan;

        @Override
        public void run() {
            final LidarScan scan = getScan();

            if (scan != lastScan) {
                rmhcslam.update(getRangesInMillimeters(scan), deviceController.getDriveController().getPoseChange());
                lastScan = scan;
            }
        }
    };

    private void mapRefresh() {
        final byte[] map = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
        rmhcslam.getmap(map);

        synchronized (this) {
            this.map = map;
        }
    }

    public LidarScan getScan() {
        synchronized (this) {
            return scan;
        }
    }

    public byte[] getMap() {
        synchronized (this) {
            return map;
        }
    }

    private int[] getRangesInMillimeters(final LidarScan scan) {
        final int[] scanMM = new int[deviceController.getLidar().getLaserConfig().getScanSize()];
        final float[] ranges = scan.getRanges();

        for (int i = 0; i < scanMM.length; i++) {
            scanMM[i] = (int) (ranges[i] * 1000);
        }

        return scanMM;
    }
}
