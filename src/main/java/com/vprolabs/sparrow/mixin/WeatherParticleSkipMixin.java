package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.render.FrameGraphBuilder;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Cancels weather (rain/snow) particle rendering entirely. Weather is pure
 * visual — has no effect on multiplayer gameplay, and is one of the heaviest
 * particle generators per-rainy-tick. Aligned with the project's "100% UNLOAD"
 * philosophy: bloat systems must be 100% disabled.
 *
 * Zero detection risk — the server doesn't care if the client renders rain.
 *
 * To re-enable weather, remove this mixin from sparrow.mixins.json.
 */
@Mixin(WorldRenderer.class)
public class WeatherParticleSkipMixin {

    @Unique private static boolean sparrow_weatherLogged = false;

    @Inject(method = "renderWeather", at = @At("HEAD"), cancellable = true)
    private void sparrow_skipWeather(FrameGraphBuilder frameGraphBuilder, GpuBufferSlice bufferSlice, CallbackInfo ci) {
        if (!sparrow_weatherLogged) {
            sparrow_weatherLogged = true;
            SparrowLogger.debug("WeatherParticleSkipMixin: 100% weather unload active");
        }
        ci.cancel();
    }
}
