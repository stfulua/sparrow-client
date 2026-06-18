package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class SmoothElytraMixin {

    @Inject(method = "tickMovement", at = @At("HEAD"))
    private void sparrow_fixElytraState(CallbackInfo ci) {
        if (!ConfigCache.smoothElytra) return;
        LivingEntity self = (LivingEntity)(Object)this;
        if (self.isGliding() && self.isOnGround()) {
            self.stopGliding();
        }
    }
}
