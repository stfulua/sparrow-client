package xyz.vprolabs.sparrow.state;

import net.minecraft.screen.slot.Slot;

public final class StoragePreviewState {
    public static volatile Slot lastHoveredSlot = null;
    public static volatile int selectedIndex = 0;

    private StoragePreviewState() {}

    public static int getSelectedIndex(Slot slot, int totalItems) {
        if (slot != lastHoveredSlot) {
            lastHoveredSlot = slot;
            selectedIndex = 0;
        }
        if (totalItems <= 0) return 0;
        if (selectedIndex < 0) selectedIndex = totalItems - 1;
        if (selectedIndex >= totalItems) selectedIndex = 0;
        return selectedIndex;
    }

    public static void onScroll(double deltaY, int totalItems) {
        if (totalItems <= 0) return;
        int step = (deltaY > 0) ? 1 : -1;
        selectedIndex += step;
        if (selectedIndex < 0) selectedIndex = totalItems - 1;
        if (selectedIndex >= totalItems) selectedIndex = 0;
    }

    public static void reset() {
        lastHoveredSlot = null;
        selectedIndex = 0;
    }
}
