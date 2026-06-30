package xyz.vprolabs.sparrow.mixin.Tweaks;
import xyz.vprolabs.sparrow.mixin.Utils.FogRendererAccessor;

import xyz.vprolabs.sparrow.config.SodiumCompat;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.fog.FogRenderer;
import org.joml.Vector4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(FogRenderer.class)
public class FogDisableMixin {
    @Unique private static boolean sparrow_fogLogged = false;

    @Inject(method = "toggleFog", at = @At("HEAD"), cancellable = true)
    private static void sparrow_blockFogToggle(CallbackInfoReturnable<Boolean> cir) {
            if (SodiumCompat.isSodiumLoaded()) return;
            cir.setReturnValue(false);
    }

    @Inject(method = "applyFog", at = @At("HEAD"))
    private void sparrow_disableFog(CallbackInfoReturnable<Vector4f> cir) {
            if (SodiumCompat.isSodiumLoaded()) return;
            if (FogRendererAccessor.getFogEnabled()) {
                FogRendererAccessor.setFogEnabled(false);
                if (!sparrow_fogLogged) {
                    sparrow_fogLogged = true;
                    SparrowLogger.debug("FogDisableMixin: fog fully disabled (empty buffer, correct clear color)");
                }
            }
    }
}
