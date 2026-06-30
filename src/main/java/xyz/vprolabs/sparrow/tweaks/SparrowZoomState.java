package xyz.vprolabs.sparrow.tweaks;

/**
 * Live zoom state shared between ZoomMixin and MouseScrollMixin.
 * targetZoom starts at the config value but is adjusted at runtime via scroll.
 */
public class SparrowZoomState {
    public static double currentZoom = 1.0;
    public static double targetZoom = 4.0;

    private SparrowZoomState() {}
}
