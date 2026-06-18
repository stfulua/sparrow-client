package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public class NoMiningFatigueMixin {

    @Inject(method = "addStatusEffect(Lnet/minecraft/entity/effect/StatusEffectInstance;)Z", at = @At("HEAD"), cancellable = true)
    private void blockMiningFatigue(StatusEffectInstance effect, CallbackInfoReturnable<Boolean> cir) {
        if (!ConfigCache.noMiningFatigue) return;
        if ((Object) this != MinecraftClient.getInstance().player) return;
        if (effect.getEffectType() == StatusEffects.MINING_FATIGUE) {
            cir.setReturnValue(false);
        }
    }
}
