package xyz.vprolabs.sparrow.mixin.Tweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import xyz.vprolabs.sparrow.mixin.Utils.SimpleOptionAccessor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {
private static final Double GAMMA_MAX = 15.0;

    @Inject(method = "update", at = @At("HEAD"))
    private void sparrow_forceFullbright(float delta, CallbackInfo ci) {
MinecraftClient client = MinecraftClient.getInstance();
            if (client == null || client.options == null) return;

            ((SimpleOptionAccessor)(Object) client.options.getGamma()).setValue(GAMMA_MAX);
}

}
