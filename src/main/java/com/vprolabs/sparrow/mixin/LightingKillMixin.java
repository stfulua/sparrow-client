package com.vprolabs.sparrow.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.ChunkSectionPos;
import net.minecraft.world.LightType;
import net.minecraft.world.chunk.ChunkNibbleArray;
import net.minecraft.world.chunk.light.LightingProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Kills the client-side lighting pipeline. Combined with {@link FullbrightMixin}
 * (returns brightness 1.0) and {@link LightmapTextureManagerMixin} (forces gamma=15
 * per frame), this removes both the CPU cost of lighting calculations AND the
 * visual block shadows the user is complaining about.
 *
 * <p>Why this is safe:
 * <ul>
 *   <li>Outbound packets untouched — no anticheat risk.</li>
 *   <li>Server still computes its own lighting; client just stops processing
 *       the same data redundantly.</li>
 *   <li>The lightmap texture is overridden in {@link LightmapTextureManagerMixin}
 *       to a uniform white, so the absence of light data has no visual effect.</li>
 * </ul>
 *
 * <p>Methods killed (all on the client-side {@code LightingProvider}):
 * <ul>
 *   <li>{@code setSectionStatus} — the chunk build pipeline uses this to mark sections as dirty.
 *       Cancelling means new chunks still load (they do their own rebuild), but no work
 *       re-runs after load. Net: less CPU per frame.</li>
 *   <li>{@code doLightUpdates} — the per-frame lighting work. Returns 0 means "no work was
 *       done", which is the truth when we cancel everything else.</li>
 *   <li>{@code hasUpdates} — the chunk build pipeline polls this. Returning false stops the
 *       pipeline from spinning on empty work.</li>
 *   <li>{@code setColumnEnabled} — turns lighting on/off for a chunk column. Cancelling
 *       is safe because the pipeline is dead.</li>
 *   <li>{@code enqueueSectionData} — receives light data from the server. Cancelling drops
 *       the data; the lightmap is uniform anyway.</li>
 *   <li>{@code propagateLight} — light propagation across chunk boundaries. Cancelling
 *       means newly-revealed chunks stay dark in the lighting data, but the lightmap
 *       override makes them render bright. Visually identical to fullbright.</li>
 *   <li>{@code checkBlock} — per-block light check. Cancelling is safe for the same reason.</li>
 * </ul>
 */
@Mixin(LightingProvider.class)
public class LightingKillMixin {

    @Inject(method = "setSectionStatus", at = @At("HEAD"), cancellable = true)
    private void sparrow_killSetSectionStatus(ChunkSectionPos pos, boolean notReady, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "doLightUpdates", at = @At("HEAD"), cancellable = true)
    private void sparrow_killDoLightUpdates(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(0);
    }

    @Inject(method = "hasUpdates", at = @At("HEAD"), cancellable = true)
    private void sparrow_killHasUpdates(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "setColumnEnabled", at = @At("HEAD"), cancellable = true)
    private void sparrow_killSetColumnEnabled(ChunkPos chunkPos, boolean retainData, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "enqueueSectionData", at = @At("HEAD"), cancellable = true)
    private void sparrow_killEnqueueSectionData(LightType lightType, ChunkSectionPos pos, ChunkNibbleArray data, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "propagateLight", at = @At("HEAD"), cancellable = true)
    private void sparrow_killPropagateLight(ChunkPos chunkPos, CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "checkBlock", at = @At("HEAD"), cancellable = true)
    private void sparrow_killCheckBlock(BlockPos pos, CallbackInfo ci) {
        ci.cancel();
    }
}
