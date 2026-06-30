package xyz.vprolabs.sparrow.mixin.Bugfix;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class BlockResyncMixin {

    @Unique
    private long sparrow_lastBlockResync = 0;

    @Inject(method = "tick", at = @At("TAIL"))
    private void sparrow_autoBlockResync(CallbackInfo ci) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.getNetworkHandler() == null) return;

            // Skip first 20 ticks (~1s) after spawn — lets the client's
            // block-action sequence counter stabilize. Sending a STOP_DESTROY_BLOCK
            // at age=0 bypasses ClientPlayerInteractionManager.sequence, causing
            // anticheat BadPacketsH flags (expected=1, id=0) on the next real interaction.
            if (player.age < 20) return;

            long now = System.currentTimeMillis();
            if (now - sparrow_lastBlockResync < 60000) return;
            sparrow_lastBlockResync = now;

            BlockPos pos = player.getBlockPos();
            client.getNetworkHandler().sendPacket(
                new PlayerActionC2SPacket(PlayerActionC2SPacket.Action.STOP_DESTROY_BLOCK, pos, Direction.DOWN)
            );
    }
}
