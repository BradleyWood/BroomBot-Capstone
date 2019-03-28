package ca.uoit.crobot;

import ca.uoit.crobot.hardware.*;
import ca.uoit.crobot.odometry.Drive;
import ca.uoit.crobot.task.NavigationTask;
import ca.uoit.crobot.task.PeriodicRobotTask;
import edu.wlu.cs.levy.breezyslam.algorithms.CoreSLAM;
import edu.wlu.cs.levy.breezyslam.algorithms.RMHCSLAM;
import edu.wlu.cs.levy.breezyslam.components.Position;
import lombok.Data;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public @Data class CRobot {

    public static final int MAP_SIZE_PIXELS = 700;
    public static final int MAP_SIZE_METERS = 32;

    private ScheduledExecutorService executorService;
    private RMHCSLAM rmhcslam;
    private LidarScan scan;

    private byte[] map;

    private final Motor leftMotor;
    private final Motor rightMotor;
    private final Motor brushMotor;
    private final Lidar lidar;
    private Drive driveController;
    private final DigitalSensor dropSensor;

    private boolean initialized = false;

    private final List<PeriodicRobotTask> periodicTasks = Arrays.asList(ScanTask.INSTANCE, SLAMTask.INSTANCE, CleanTask.INSTANCE);
    private final List<NavigationTask> navTasks = Arrays.asList(DriveTask.INSTANCE, CollisionTask.INSTANCE);//, DropTask.INSTANCE);//ForwardTask.INSTANCE, CollisionTask.INSTANCE, DriveTask.INSTANCE);
    private final Lock lock = new ReentrantLock();
    private NavigationTask currentTask = null;
    private Future navTaskFuture;

    public void init() throws TimeoutException {
        parallelInit(leftMotor, rightMotor, lidar, dropSensor);
        lidar.stopRotation();
        driveController = new Drive(leftMotor, rightMotor);
        initialized = true;
    }

    public boolean isRunning() {
        return executorService != null && !executorService.isShutdown();
    }

    public void start() {
        if (isRunning())
            throw new IllegalStateException();

        rmhcslam = new RMHCSLAM(getLidar().getLaserConfig(), MAP_SIZE_PIXELS, MAP_SIZE_METERS,
                (int) System.currentTimeMillis());

        executorService = Executors.newScheduledThreadPool(4);
        executorService.submit(this::run);
    }

    private void run() {
        lidar.rotate();

        periodicTasks.forEach(t ->
                executorService.scheduleAtFixedRate(() -> t.run(this), t.getOffset(), t.getPeriod(), t.getTimeUnit())
        );

        for (final NavigationTask navTask : navTasks) {
            navTask.init(this);
            executorService.scheduleAtFixedRate(() -> {

                if (((currentTask == null || currentTask.canInterrupt() || navTaskFuture.isDone()))
                        && navTask.activate(this) && (navTaskFuture == null || navTaskFuture.isDone() || navTask != currentTask)) {
                    try {
                        if (lock.tryLock(50, TimeUnit.MILLISECONDS)) {

                            if (navTaskFuture != null) {
                                navTaskFuture.cancel(true);
                                while (!navTaskFuture.isDone()) ;
                            }

                            if (currentTask != null) {
                                currentTask.onInterrupt(this);
                            }

                            currentTask = navTask;
                            navTaskFuture = executorService.submit(() -> navTask.run(this));
                        }

                    } catch (InterruptedException ignored) {
                    } finally {
                        lock.unlock();
                    }
                }
            }, 0, navTask.getPollingRate(), navTask.getTimeUnit());
        }
    }

    public Position getPosition() {
        return rmhcslam.getpos();
    }

    public int getX() {
        return (int) (rmhcslam.getpos().x_mm / 1000 / MAP_SIZE_METERS * MAP_SIZE_PIXELS);
    }

    public int getY() {
        return (int) (rmhcslam.getpos().y_mm / 1000 / MAP_SIZE_METERS * MAP_SIZE_PIXELS);
    }

    public CoreSLAM getSlamAlgorithm() {
        return rmhcslam;
    }

    public void mapRefresh() {
        final byte[] map = new byte[MAP_SIZE_PIXELS * MAP_SIZE_PIXELS];
        rmhcslam.getmap(map);

        synchronized (this) {
            this.map = map;
        }
    }

    public void setScan(final LidarScan scan) {
        synchronized (this) {
            this.scan = scan;
        }
    }

    public LidarScan getScan() {
        synchronized (this) {
            return scan;
        }
    }

    public void setMap(final byte[] map) {
        synchronized (this) {
            this.map = map;
        }
    }

    public byte[] getMap() {
        synchronized (this) {
            return map;
        }
    }

    /**
     * Stop all devices
     */
    public void stop() {
        executorService.shutdownNow();

        leftMotor.stop();
        rightMotor.stop();
        brushMotor.stop();
        lidar.stopRotation();
    }

    /**
     * Initialize a set devices in parallel to save time.
     *
     * @param devices The set of devices to initialize
     * @throws TimeoutException if a device fails to initialize within the timelimit
     */
    private void parallelInit(final Device... devices) throws TimeoutException {
        final Thread[] threads = new Thread[devices.length];

        for (int i = 0; i < devices.length; i++) {
            final int idx = i;
            threads[i] = new Thread(() -> devices[idx].init());
            threads[i].start();
        }

        final long timeout = 25000;
        final long start = System.currentTimeMillis();

        for (int j = 0; j < threads.length; j++) {
            try {
                threads[j].join(timeout);
            } catch (InterruptedException e) {
                j--;
            }
        }

        if (System.currentTimeMillis() - start > timeout) {
            throw new TimeoutException();
        }
    }
}
