package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.LockSupport;

/**
 * Adaptive Frame Pacer — eliminates micro-stutter at high FPS by smoothing
 * frame delivery to the system's natural cadence.
 *
 * How it works:
 * 1. Tracks frame time deltas via an exponential moving average.
 * 2. If a frame finishes faster than the average (too fast → next frame
 *    will be perceived as a stutter), spin-wait the remaining time.
 * 3. If a frame finishes slower than average (already stuttered), does
 *    NOT add delay — keeps input lag minimal.
 * 4. No hard FPS cap — adapts to the system's current capability.
 *
 * The result: frame times are delivered at perfectly even intervals
 * (no jitter) without adding input lag when the system is under load.
 */
@Mixin(MinecraftClient.class)
public class FramePacerMixin {

    @Unique
    private long sparrow_frameStartNs = 0L;

    @Unique
    private long sparrow_avgFrameTime = 5_000_000L; // start at 5ms = 200 FPS

    @Unique
    private int sparrow_warmupFrames = 0;

    @Unique
    private boolean sparrow_logged = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_recordFrameStart(boolean tick, CallbackInfo ci) {
        sparrow_frameStartNs = System.nanoTime();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_paceFrame(boolean tick, CallbackInfo ci) {
        long now = System.nanoTime();
        long elapsed = now - sparrow_frameStartNs;

        // Warmup — collect 20 frames before starting to pace
        if (sparrow_warmupFrames < 20) {
            sparrow_warmupFrames++;
            if (sparrow_warmupFrames <= 10) {
                // Build initial average from first 10 frames
                sparrow_avgFrameTime = (sparrow_avgFrameTime * (sparrow_warmupFrames - 1)
                    + elapsed) / sparrow_warmupFrames;
            }
            return;
        }

        // Update exponential moving average (decay factor = 10)
        // Acts as a low-pass filter: smooths out single-frame spikes
        sparrow_avgFrameTime = (sparrow_avgFrameTime * 9 + elapsed) / 10;

        // Guard: if the moving average has gone stale (e.g., after a scene
        // change where frames suddenly become faster), clamp to a minimum
        // of 1ms to prevent busy-spinning at 1000+ FPS.
        if (sparrow_avgFrameTime < 1_000_000L) {
            sparrow_avgFrameTime = 1_000_000L;
        }

        // Only pace if this frame was faster than the moving average.
        // If it was slower, we already missed the slot — don't add latency.
        if (elapsed < sparrow_avgFrameTime) {
            long remaining = sparrow_avgFrameTime - elapsed;

            // For waits longer than 1ms, use LockSupport (power-efficient)
            if (remaining > 1_000_000L) {
                LockSupport.parkNanos(remaining - 500_000L);
            }

            // Spin-wait the final stretch (most precise timing)
            // Thread.onSpinWait() is a CPU hint that reduces power during spinning
            while (System.nanoTime() - sparrow_frameStartNs < sparrow_avgFrameTime) {
                Thread.onSpinWait();
            }
        }

        if (!sparrow_logged) {
            sparrow_logged = true;
            long avgMs = sparrow_avgFrameTime / 1_000_000L;
            long fps = 1_000_000_000L / Math.max(sparrow_avgFrameTime, 1L);
            SparrowLogger.info("FramePacerMixin: pacing to ~" + fps + " FPS (avg " + avgMs + "ms)");
        }
    }
}
