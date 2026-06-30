package xyz.vprolabs.sparrow.mixin.Optimization.Render;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class CloudRenderKillMixin {

    @Unique
    private static boolean sparrow_cloudsLogged = false;

    @Inject(method = "renderClouds", at = @At("HEAD"), cancellable = true)
    private void sparrow_renderClouds(CallbackInfo ci) {
            if (!sparrow_cloudsLogged) {
                sparrow_cloudsLogged = true;
                SparrowLogger.debug("CloudRenderKillMixin: cloud rendering fully disabled");
            }
            ci.cancel();
    }
}
