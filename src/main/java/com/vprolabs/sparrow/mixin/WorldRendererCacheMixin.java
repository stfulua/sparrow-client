package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.tweaks.SparrowCache;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.util.ObjectAllocator;
import org.joml.Matrix4f;
import org.joml.Vector4f;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererCacheMixin {

    @Inject(
        method = "render",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/Camera;getCameraPos()Lnet/minecraft/util/math/Vec3d;"
        )
    )
    private void sparrow_updateCameraCache(
        ObjectAllocator allocator,
        RenderTickCounter tickCounter,
        boolean bl,
        Camera camera,
        Matrix4f matrix4f,
        Matrix4f matrix4f2,
        Matrix4f matrix4f3,
        GpuBufferSlice bufferSlice,
        Vector4f vector4f,
        boolean bl2,
        CallbackInfo ci
    ) {
        if (camera == null) return;
        Vec3d pos = camera.getCameraPos();
        if (pos == null) return;
        SparrowCache.updateCamera(pos);
    }
}
