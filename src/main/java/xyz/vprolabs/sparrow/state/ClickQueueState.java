package xyz.vprolabs.sparrow.state;

public class ClickQueueState {
    private static long queueTime = 0;
    private static final long EXPIRY_MS = 250;

    public static void queue() {
        queueTime = System.currentTimeMillis();
    }

    public static boolean shouldReplay() {
        if (queueTime == 0) return false;
        long elapsed = System.currentTimeMillis() - queueTime;
        if (elapsed > EXPIRY_MS) {
            queueTime = 0;
            return false;
        }
        return true;
    }

    public static void clear() {
        queueTime = 0;
    }
}
