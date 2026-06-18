package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.DarknessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Completely removes the Warden's darkness fog effect.
 * DarknessEffectFogModifier applies darkness fog when the player
 * has the Darkness status effect (from Warden). We cancel both
 * the fog modification and the darkness modifier to eliminate it.
 */
@Mixin(DarknessEffectFogModifier.class)
public class DarknessFogRemovalMixin {

    @Unique
    private static boolean sparrow_darknessLogged = false;

    @Inject(method = "applyStartEndModifier", at = @At("HEAD"), cancellable = true)
    private void sparrow_blockDarknessFog(FogData fogData, Camera camera, ClientWorld world,
                                          float tickDelta, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!sparrow_darknessLogged) {
            sparrow_darknessLogged = true;
            SparrowLogger.debug("DarknessFogRemovalMixin: blocking darkness fog effect");
        }
        ci.cancel();
    }

    @Inject(method = "applyDarknessModifier", at = @At("HEAD"), cancellable = true)
    private void sparrow_blockDarknessModifier(net.minecraft.entity.LivingEntity entity, float f, float g, CallbackInfoReturnable<Float> cir) {
        cir.setReturnValue(0.0f);
    }
}
