package ca.uoit.crobot;

import ca.uoit.crobot.hardware.LidarScan;
import ca.uoit.crobot.odometry.Drive;
import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.PoseChange;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

public class CRobot {

    public static final int MAP_SIZE_PIXELS = 1000;
    public static final int MAP_SIZE_METERS = 20;

    private final DeviceController deviceController;

    private ScheduledExecutorService executorService;
    private RMHCSLAM rmhcslam;
    private LidarScan scan;

    private AtomicBoolean obsticleFlag = new AtomicBoolean(false);

    private byte[] map;

    public CRobot(final DeviceController deviceController) {
        this.deviceController = deviceController;
    }

    public boolean isRunning() {
        return executorService != null && !executorService.isShutdown();
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

        executorService.scheduleAtFixedRate(collisionTask, 100, (long) (1000 / scanRate + 50), TimeUnit.MILLISECONDS);

        executorService.scheduleAtFixedRate(() -> {
            if (!obsticleFlag.get()) {
                deviceController.getDriveController().drive(20);
            }
        }, 0, 1000, TimeUnit.MILLISECONDS);
    }

    private Runnable collisionTask = new Runnable() {

        Drive.Direction decision = null;
        long lastDecision = 0;

        @Override
        public void run() {
            try {
                final LidarScan scan = getScan();

                if (scan == null)
                    return;

                int right = countPoints(scan, 0.30, Math.PI / 1.6, Math.PI);
                int left = countPoints(scan, 0.30, -Math.PI, -Math.PI / 1.6);

                if (left + right > 8 || left > 5|| right > 5) {
                    if (obsticleFlag.getAndSet(true) && System.currentTimeMillis() - lastDecision > 1000) {
                        if (left > right) {
                            decision = Drive.Direction.RIGHT;
                        } else {
                            decision = Drive.Direction.LEFT;
                        }
                        lastDecision = System.currentTimeMillis();
                    }

                    if (decision == Drive.Direction.RIGHT) {
                        deviceController.getDriveController().turnRight(15);
                    } else {
                        deviceController.getDriveController().turnLeft(15);
                    }
                } else if (obsticleFlag.get()) {
                    deviceController.getDriveController().stop();
                    lastDecision = System.currentTimeMillis();
                    obsticleFlag.set(false);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };

    private int countPoints(final LidarScan scan, final double minRange, final double minAngle, final double maxAngle) {
        final float[] angles = scan.getAngles();
        final float[] ranges = scan.getRanges();

        int count = 0;

        for (int i = 0; i < ranges.length; i++) {
            if (angles[i] >= minAngle && angles[i] <= maxAngle && ranges[i] < minRange && ranges[i] > 0.01) {
                count++;
            }
        }

        return count;
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

    public int getX() {
        return (int) (rmhcslam.getpos().x_mm / 1000 / MAP_SIZE_METERS * MAP_SIZE_PIXELS);
    }

    public int getY() {
        return (int) (rmhcslam.getpos().y_mm / 1000 / MAP_SIZE_METERS * MAP_SIZE_PIXELS);
    }

    private Runnable mapUpdate = new Runnable() {

        private LidarScan lastScan;

        @Override
        public void run() {
            final LidarScan scan = getScan();

            if (scan != lastScan) {
                final PoseChange pc = deviceController.getDriveController().getPoseChange();

                if (pc.getDxyMm() > 0.001 || Math.abs(pc.getDthetaDegrees()) > 0.001) {
                    rmhcslam.update(getRangesInMillimeters(scan), pc);
                } else {
                    rmhcslam.update(getRangesInMillimeters(scan));
                }
                lastScan = scan;
            }
        }
    };

    public void mapRefresh() {
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

        for (int i = 0; i < scanMM.length && i < ranges.length; i++) {
            scanMM[i] = (int) (ranges[i] * 1000);
        }

        return scanMM;
    }
}
