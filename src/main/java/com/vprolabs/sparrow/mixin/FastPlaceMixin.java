package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class FastPlaceMixin {

    @Inject(method = "tick", at = @At("HEAD"))
    private void sparrow_fastPlace(CallbackInfo ci) {
        if (ConfigCache.fastPlace) {
            ((MinecraftClientAccessor)(Object)this).setItemUseCooldown(0);
        }
    }
}
