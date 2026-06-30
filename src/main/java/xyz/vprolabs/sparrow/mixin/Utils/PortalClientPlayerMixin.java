package xyz.vprolabs.sparrow.mixin.Utils;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class PortalClientPlayerMixin {

    @Inject(method = "tickNausea", at = @At("HEAD"), cancellable = true)
    private void sparrow_keepScreenOpenInPortal(boolean fromPortal, CallbackInfo ci) {
            if (fromPortal) {
                ci.cancel();
            }
    }
}
