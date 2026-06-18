package com.vprolabs.sparrow.state;

public final class ToggleSneakState {
    public static volatile boolean enabled = false;

    private ToggleSneakState() {}

    public static void toggle() {
        enabled = !enabled;
    }
}
