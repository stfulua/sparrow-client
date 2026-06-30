package xyz.vprolabs.sparrow.state;

public final class KnockbackState {
    public static double kbStrength = 0;
    public static long lastKbTime = 0;
    public static final long KB_DISPLAY_MS = 1500;

    private KnockbackState() {}

    public static void onKnockback(double x, double z) {
        kbStrength = Math.sqrt(x * x + z * z);
        lastKbTime = System.currentTimeMillis();
    }

    public static boolean isShowing() {
        return System.currentTimeMillis() - lastKbTime < KB_DISPLAY_MS;
    }
}
