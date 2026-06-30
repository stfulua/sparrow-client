package xyz.vprolabs.sparrow.state;

import java.util.HashMap;
import java.util.Map;

public class HudMoveState {
    public static boolean active = false;
    public static String selectedElement = null;
    public static int dragStartX, dragStartY;
    public static int origOffsetX, origOffsetY;
    public static boolean isDragging = false;
    public static boolean wasMouseDown = false;
    public static final Map<String, int[]> elementBounds = new HashMap<>();

    public static final Map<String, int[]> savedOffsets = new HashMap<>();
    public static final String[] ELEMENT_KEYS = {"coords", "ping", "desync", "fire-timer", "ghost-block", "knockback", "shield"};

    public static void activate() {
        savedOffsets.clear();
        for (String key : ELEMENT_KEYS) {
            int[] off = HudPositions.getOffset(key);
            savedOffsets.put(key, new int[]{off[0], off[1]});
        }
        active = true;
    }

    public static void reset() {
        active = false;
        selectedElement = null;
        isDragging = false;
        wasMouseDown = false;
        elementBounds.clear();
    }
}
