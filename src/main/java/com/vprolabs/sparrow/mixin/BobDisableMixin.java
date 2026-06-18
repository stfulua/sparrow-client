package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class BobDisableMixin {

    @Inject(method = "bobView", at = @At("HEAD"), cancellable = true)
    private void skipBobbing(MatrixStack matrices, float tickProgress, CallbackInfo ci) {
        ci.cancel();
    }
}
