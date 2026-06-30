package xyz.vprolabs.sparrow.mixin.Optimization;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.locks.LockSupport;

@Mixin(MinecraftClient.class)
public class FramePacerMixin {

    private static final long MAX_FRAME_TIME_NS = 33_333_334L; // 30 FPS floor — NEVER below this
    private static final long MIN_FRAME_TIME_NS = 1_000_000L;  // 1000 FPS ceiling

    @Unique
    private long sparrow_frameStartNs = 0L;

    @Unique
    private boolean sparrow_logged = false;

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_recordFrameStart(boolean tick, CallbackInfo ci) {
        sparrow_frameStartNs = System.nanoTime();
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_paceFrame(boolean tick, CallbackInfo ci) {
        // Read user's configured maxFps; target a FIXED frame time (not a moving average)
        MinecraftClient client = (MinecraftClient) (Object) this;
        int maxFps = client.options.getMaxFps().getValue();

        // Unlimited or extremely high? Skip pacing entirely.
        if (maxFps <= 0 || maxFps >= 1000) return;

        long targetNs = 1_000_000_000L / maxFps;

        // CRITICAL: hard cap at 30 FPS minimum — this is what prevents the 0.1 FPS spiral
        if (targetNs > MAX_FRAME_TIME_NS) targetNs = MAX_FRAME_TIME_NS;
        if (targetNs < MIN_FRAME_TIME_NS) return; // 1000 FPS+ target, no pacing needed

        long now = System.nanoTime();
        long elapsed = now - sparrow_frameStartNs;
        long remaining = targetNs - elapsed;

        if (remaining > 0) {
            if (remaining > 1_000_000L) {
                LockSupport.parkNanos(remaining - 500_000L);
            }
            // Precise spin for the remaining sub-millisecond
            while (System.nanoTime() - sparrow_frameStartNs < targetNs) {
                Thread.onSpinWait();
            }
        }

        if (!sparrow_logged) {
            sparrow_logged = true;
            long fps = 1_000_000_000L / targetNs;
            SparrowLogger.info("FramePacerMixin: pacing to ~" + fps + " FPS (fixed target, min 30)");
        }
    }
}
