package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.config.ConfigReader;
import com.vprolabs.sparrow.logging.SparrowLogger;
import com.vprolabs.sparrow.tweaks.ServerSafetyState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientConnectionState;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.s2c.play.GameJoinS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ServerFastPlaceMixin {
    @Inject(method = "<init>", at = @At("TAIL"))
    private void sparrow_onConstruct(MinecraftClient client, ClientConnection connection, ClientConnectionState state, CallbackInfo ci) {
        if (connection == null) return;
        try {
            String address = connection.getAddressAsString(true);
            ServerSafetyState.rememberConnect(address);
        } catch (Throwable t) {
            SparrowLogger.warn("Failed to capture connection address: " + t.getMessage());
        }
    }

    @Inject(method = "onGameJoin", at = @At("HEAD"))
    private void sparrow_onGameJoin(GameJoinS2CPacket packet, CallbackInfo ci) {
        if (ServerSafetyState.isOnRestrictedServer()) {
            if (ConfigCache.fastPlace) {
                ServerSafetyState.fastPlaceOriginalState = true;
                ServerSafetyState.fastPlaceWasForcedOff = true;
                ConfigCache.fastPlace = false;
                ConfigReader.saveFromCache();
                SparrowLogger.info("FastPlace auto-disabled for restricted server");
            }
        } else if (ServerSafetyState.fastPlaceWasForcedOff) {
            ConfigCache.fastPlace = ServerSafetyState.fastPlaceOriginalState;
            ServerSafetyState.fastPlaceWasForcedOff = false;
            ServerSafetyState.fastPlaceOriginalState = false;
            ConfigReader.saveFromCache();
            SparrowLogger.info("FastPlace restored to original state after leaving restricted server");
        }

        ServerSafetyState.clearPending();
    }
}
