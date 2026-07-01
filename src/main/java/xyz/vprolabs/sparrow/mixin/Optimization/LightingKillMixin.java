package xyz.vprolabs.sparrow.mixin.Optimization;

import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(LightingProvider.class)
public class LightingKillMixin {
    // Temporarily disabled for debugging.
    // All 4 @Inject cancellations (doLightUpdates, hasUpdates, propagateLight, checkBlock)
    // removed. If chunks render without this mixin, the cause is confirmed as the
    // server-side LightingProvider being affected by our cancellations.
    // Re-implement with MinecraftClient.getInstance().isOnThread() guards for client-only.
}