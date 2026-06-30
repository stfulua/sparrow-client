package xyz.vprolabs.sparrow.mixin.Bugfix;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class RubberbandRecoveryMixin {

    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void sparrow_onRubberband(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null || client.getNetworkHandler() == null) return;

            // Defer to main thread: onPlayerPositionLook runs on Netty IO thread,
            // but setSprinting/setSneaking mutate player state that must only
            // be touched from the render thread.
            client.execute(() -> {
                if (client.player == null) return;
                var pi = client.player.input.playerInput;
                if (pi.left() || pi.right() || pi.forward() || pi.backward()) {
                    if (client.options.sprintKey.isPressed()) {
                        client.player.setSprinting(true);
                    }
                    if (client.options.sneakKey.isPressed()) {
                        client.player.setSneaking(true);
                    }
                }
            });
    }
}
