package com.vprolabs.sparrow.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class BlockBreakOverlayMixin {

    @Inject(method = "setBlockBreakingInfo", at = @At("HEAD"), cancellable = true)
    private void sparrow_disableBlockBreakOverlay(int entityId, BlockPos pos, int stage, CallbackInfo ci) {
        ci.cancel();
    }
}
