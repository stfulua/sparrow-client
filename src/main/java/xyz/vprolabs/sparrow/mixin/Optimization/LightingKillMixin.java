package xyz.vprolabs.sparrow.mixin.Optimization;

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
