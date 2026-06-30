package xyz.vprolabs.sparrow.mixin.Brand;

import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(MinecraftClient.class)
public class ClientBrandMixin {

    @Inject(method = "getVersionType", at = @At("HEAD"), cancellable = true)
    private void sparrow_getVersionType(CallbackInfoReturnable<String> cir) {
            cir.setReturnValue("release");
    }
}
