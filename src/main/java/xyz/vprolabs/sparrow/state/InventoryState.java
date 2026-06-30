package xyz.vprolabs.sparrow.state;

public final class InventoryState {
    public static long lastCheckTime = 0;
    public static final long CHECK_INTERVAL = 2000;
    public static int expectedTotalCount = -1;

    private InventoryState() {
    }
}
