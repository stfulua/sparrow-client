package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ChunkBuilder.class)
public class ChunkBuilderStoppedResetMixin {

    @Shadow
    private volatile boolean stopped;

    @Unique
    private static boolean sparrow_stoppedLog = false;

    @Inject(method = "setWorld", at = @At("TAIL"))
    private void sparrow_resetStopped(ClientWorld world, CallbackInfo ci) {
        if (this.stopped) {
            this.stopped = false;
            if (!sparrow_stoppedLog) {
                sparrow_stoppedLog = true;
                SparrowLogger.debug("ChunkBuilderStoppedResetMixin: reset stopped=false on world switch");
            }
        }
    }
}
