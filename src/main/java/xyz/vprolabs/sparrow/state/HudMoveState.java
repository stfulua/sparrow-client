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

    public static void reset() {
        active = false;
        selectedElement = null;
        isDragging = false;
        wasMouseDown = false;
        elementBounds.clear();
    }
}
