package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.config.ConfigReader;
import com.vprolabs.sparrow.logging.SparrowLogger;
import com.vprolabs.sparrow.state.StoragePreviewState;
import com.vprolabs.sparrow.tweaks.ServerSafetyState;
import com.vprolabs.sparrow.tweaks.SparrowGlintLayers;
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
        if (ServerSafetyState.fastPlaceWasForcedOff) {
            ConfigCache.fastPlace = ServerSafetyState.fastPlaceOriginalState;
            ServerSafetyState.fastPlaceWasForcedOff = false;
            ServerSafetyState.fastPlaceOriginalState = false;
            ConfigReader.saveFromCache();
            SparrowLogger.info("FastPlace restored to original state after disconnect");
        }

        // Release static refs that would otherwise pin dead world state.
        StoragePreviewState.reset();
        SparrowGlintLayers.clearStaticTextures();
        SparrowLogger.debug("DisconnectClearMixin: released static storage + glint refs");
    }
}
