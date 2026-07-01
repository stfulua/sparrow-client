package xyz.vprolabs.sparrow.mixin.Optimization.Chunk;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.chunk.ChunkRenderTaskScheduler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkRenderTaskScheduler.class)
public class ChunkPrioritizedTaskLimitMixin {

    @Shadow
    private int remainingPrioritizableTasks;

    @Unique
    private static boolean sparrow_taskLimitLogged = false;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void sparrow_increasePrioritizedLimit(CallbackInfo ci) {
        this.remainingPrioritizableTasks = 12;
        if (!sparrow_taskLimitLogged) {
            sparrow_taskLimitLogged = true;
            SparrowLogger.debug("ChunkPrioritizedTaskLimitMixin: remainingPrioritizableTasks=12 (was 2)");
        }
    }
}
