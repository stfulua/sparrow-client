package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.state.StoragePreviewState;
import xyz.vprolabs.sparrow.tweaks.SparrowGlintLayers;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class DisconnectClearMixin {
@Inject(method = "disconnect(Lnet/minecraft/text/Text;)V", at = @At("HEAD"))
    private void sparrow_onDisconnect(Text reason, CallbackInfo ci) {
            StoragePreviewState.reset();
            SparrowGlintLayers.clearStaticTextures();
            SparrowLogger.debug("DisconnectClearMixin: released static storage + glint refs");
}

}
