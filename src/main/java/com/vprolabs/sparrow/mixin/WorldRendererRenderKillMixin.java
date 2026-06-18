package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.state.CameraRenderState;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class WorldRendererRenderKillMixin {

    @Unique
    private static boolean sparrow_worldRenderLogged = false;

    @Inject(method = "renderLateDebug(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/state/CameraRenderState;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;Lorg/joml/Matrix4f;)V", at = @At("HEAD"), cancellable = true)
    private void sparrow_stopRenderLateDebug(FrameGraphBuilder builder, CameraRenderState cameraRenderState, GpuBufferSlice gpuBufferSlice, Matrix4f matrix4f, CallbackInfo ci) {
        if (!sparrow_worldRenderLogged) {
            sparrow_worldRenderLogged = true;
            SparrowLogger.debug("WorldRendererRenderKillMixin: blocking renderLateDebug");
        }
        ci.cancel();
    }
}
