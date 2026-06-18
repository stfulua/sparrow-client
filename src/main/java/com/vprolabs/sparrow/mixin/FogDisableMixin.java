package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.FogRenderer;
import net.minecraft.client.world.ClientWorld;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogDisableMixin {

    @Shadow private static boolean fogEnabled;

    @Unique
    private static boolean sparrow_fogLogged = false;

    @Inject(method = "toggleFog", at = @At("HEAD"), cancellable = true)
    private static void sparrow_blockFogToggle(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(false);
    }

    @Inject(method = "applyFog(Lnet/minecraft/client/render/Camera;ILnet/minecraft/client/render/RenderTickCounter;FLnet/minecraft/client/world/ClientWorld;)Lorg/joml/Vector4f;", at = @At("HEAD"))
    private void sparrow_forceNoFog(Camera camera, int i, RenderTickCounter tickCounter, float f, ClientWorld clientWorld, CallbackInfoReturnable<Vector4f> cir) {
        if (fogEnabled) {
            fogEnabled = false;
            if (!sparrow_fogLogged) {
                sparrow_fogLogged = true;
                SparrowLogger.debug("FogDisableMixin: forced fogEnabled=false");
            }
        }
    }
}
