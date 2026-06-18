package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(DebugRenderer.class)
public class DebugRendererSkipMixin {

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void skipWhenEmpty(Frustum frustum, double camX, double camY, double camZ, float tickDelta, CallbackInfo ci) {
        if (((DebugRendererAccessor)(Object)this).getRenderers().isEmpty()) {
            ci.cancel();
        }
    }
}
