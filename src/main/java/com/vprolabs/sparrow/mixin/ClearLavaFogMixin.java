package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.render.fog.LavaFogModifier;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LavaFogModifier.class)
public class ClearLavaFogMixin {

    @Inject(method = "applyStartEndModifier", at = @At("TAIL"))
    private void sparrow_clearLavaFog(FogData data, Camera camera, ClientWorld world, float viewDistance, RenderTickCounter tickCounter, CallbackInfo ci) {
        data.environmentalStart = -8.0f;
        data.environmentalEnd = 1000.0f;
        data.skyEnd = 1000.0f;
        data.cloudEnd = 1000.0f;
    }
}
