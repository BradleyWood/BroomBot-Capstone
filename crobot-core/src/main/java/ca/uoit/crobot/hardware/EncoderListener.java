package ca.uoit.crobot.hardware;

@FunctionalInterface
public interface EncoderListener {

    /**
     * Invoked to notify listeners that the encoder
     * has detected movement
     *
     * @param count The tick count (since device initialization)
     */
    void onMove(int count);
}
