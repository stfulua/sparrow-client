package com.vprolabs.sparrow.tweaks;

import net.minecraft.util.math.Vec3d;

public class SparrowCache {
    public static volatile Vec3d cameraPos = Vec3d.ZERO;
    public static volatile double cameraX = 0;
    public static volatile double cameraY = 0;
    public static volatile double cameraZ = 0;

    public static void updateCamera(Vec3d pos) {
        cameraPos = pos;
        cameraX = pos.x;
        cameraY = pos.y;
        cameraZ = pos.z;
    }
}
