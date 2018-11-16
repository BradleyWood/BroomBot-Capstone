package ca.uoit.crobot.hardware.shield;

import com.pi4j.io.i2c.I2CBus;
import com.pi4j.io.i2c.I2CDevice;
import com.pi4j.io.i2c.I2CFactory;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class AdafruitPWMDriver {

    /**
     * Registers
     */
    private static final int MODE1 = 0x00;
    private static final int MODE2 = 0x01;
    private static final int SUBADR1 = 0x02;
    private static final int SUBADR2 = 0x03;
    private static final int SUBADR3 = 0x04;
    private static final int PRESCALE = 0x05;
    private static final int LED0_ON_L = 0x06;
    private static final int LED0_ON_H = 0x07;
    private static final int LED0_OFF_L = 0x08;
    private static final int LED0_OFF_H = 0x09;
    private static final int ALL_LED_ON_L = 0x0FA;
    private static final int ALL_LED_ON_H = 0x0FB;
    private static final int ALL_LED_OFF_L = 0x0FC;
    private static final int ALL_LED_OFF_H = 0x0FD;

    private static final int RESTART = 0x80;
    private static final int SLEEP = 0x10;
    private static final int ALLCALL = 0X01;
    private static final int INVRT = 0X10;
    private static final int OUTDRV = 0X04;

    private static final int DEFAULT_HAT_ADDRESS = 0x60;

    private static final Map<Integer, I2CDevice> devices = new HashMap<>();
    private static I2CBus bus = null;

    private static synchronized I2CBus getI2CBus() throws IOException, I2CFactory.UnsupportedBusNumberException {
        if (bus != null)
            return bus;

        bus = I2CFactory.getInstance(I2CBus.BUS_1);

        return bus;
    }

    private static synchronized I2CDevice getI2CDevice(final int address) throws IOException, I2CFactory.UnsupportedBusNumberException {
        I2CDevice device = devices.get(address);

        if (device != null)
            return device;

        device = getI2CBus().getDevice(address);
        devices.put(address, device);

        device.write(ALL_LED_ON_L, (byte) 0);
        device.write(ALL_LED_ON_H, (byte) 0);
        device.write(ALL_LED_OFF_L, (byte) 0);
        device.write(ALL_LED_OFF_H, (byte) 0);

        device.write(MODE2, (byte) OUTDRV);
        device.write(MODE1, (byte) ALLCALL);

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }

        System.out.println(29);
        byte mode1 = (byte) (device.read(MODE1) & ~SLEEP);
        device.write(mode1);
        System.out.println(30);

        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }

        setPwmFreq(1600);

        return device;
    }

    private static I2CDevice getDefaultDevice() throws IOException, I2CFactory.UnsupportedBusNumberException {
        return getI2CDevice(DEFAULT_HAT_ADDRESS);
    }

    public static void softwareReset() throws IOException, I2CFactory.UnsupportedBusNumberException {
        getI2CDevice(0x00).write((byte) 0x06);
    }

    public static void setPwmFreq(final int freq) throws IOException, I2CFactory.UnsupportedBusNumberException {
        final I2CDevice device = getDefaultDevice();

        System.out.println(99);

        float prescaleValue = 25000000.0f; // 25 MHz
        prescaleValue /= 4096f;
        prescaleValue /= freq;
        prescaleValue -= 1f;

        byte prescale = (byte) (prescaleValue + 0.5f);
        byte oldMode = (byte) device.read(MODE1);
        byte newMode = (byte) ((oldMode & 0x7F) | 0x10);

        device.write(MODE1, newMode);
        device.write(PRESCALE, prescale);
        device.write(MODE1, oldMode);

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        device.write(MODE1, (byte) (oldMode | 0x80));

        System.out.println(101);
    }

    public static void setPwm(final int channel, final int on, final int off) throws IOException, I2CFactory.UnsupportedBusNumberException {
        final I2CDevice device = getDefaultDevice();

        device.write(LED0_ON_L + 4 * channel, (byte) (on & 0xFF));
        device.write(LED0_ON_H + 4 * channel, (byte) (on >> 8));
        device.write(LED0_OFF_L + 4 * channel, (byte) (off & 0xFF));
        device.write(LED0_OFF_H + 4 * channel, (byte) (off >> 8));
    }
}
