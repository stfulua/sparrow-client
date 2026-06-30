package xyz.vprolabs.sparrow.mixin.NotVanilla;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class SkyRenderKillMixin {

    @Unique
    private static boolean sparrow_skyLogged = false;

    @Inject(
        method = "renderSky(Lnet/minecraft/client/render/FrameGraphBuilder;Lnet/minecraft/client/render/Camera;Lcom/mojang/blaze3d/buffers/GpuBufferSlice;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sparrow_onRenderSky(CallbackInfo ci) {
        if (!sparrow_skyLogged) {
            sparrow_skyLogged = true;
            SparrowLogger.debug("SkyRenderKillMixin: sky rendering fully disabled (stars/sun/moon/celestial)");
        }
        ci.cancel();
    }
}
