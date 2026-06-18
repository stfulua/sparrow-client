package com.vprolabs.sparrow.mixin;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class DemoBlockMixin {
    @Inject(method = "isDemo", at = @At("HEAD"), cancellable = true)
    private void forceNotDemo(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }
}
