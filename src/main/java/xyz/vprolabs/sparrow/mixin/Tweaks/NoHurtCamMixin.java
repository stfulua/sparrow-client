package xyz.vprolabs.sparrow.mixin.Tweaks;

import net.minecraft.client.render.GameRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameRenderer.class)
public class NoHurtCamMixin {
@Inject(method = "tiltViewWhenHurt", at = @At("HEAD"), cancellable = true)
    private void skipHurtCam(MatrixStack matrices, float tickDelta, CallbackInfo ci) {
    ci.cancel();
}

}
