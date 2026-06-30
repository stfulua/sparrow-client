package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.Camera;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.client.render.fog.BlindnessEffectFogModifier;
import net.minecraft.client.render.fog.FogData;
import net.minecraft.client.world.ClientWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlindnessEffectFogModifier.class)
public class BlindnessFogRemovalMixin {

    @Unique
    private static boolean sparrow_blindnessLogged = false;

    @Inject(method = "applyStartEndModifier", at = @At("HEAD"), cancellable = true)
    private void sparrow_blockBlindnessFog(FogData fogData, Camera camera, ClientWorld world,
                                           float tickDelta, RenderTickCounter tickCounter, CallbackInfo ci) {
            if (!sparrow_blindnessLogged) {
                sparrow_blindnessLogged = true;
                SparrowLogger.debug("BlindnessFogRemovalMixin: blocking blindness fog effect");
            }
            ci.cancel();
    }

    @Inject(method = "applyDarknessModifier", at = @At("HEAD"), cancellable = true)
    private void sparrow_blockBlindnessModifier(net.minecraft.entity.LivingEntity entity, float f, float g, CallbackInfoReturnable<Float> cir) {
            cir.setReturnValue(0.0f);
    }
}
