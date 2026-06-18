package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.state.ToggleSneakState;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Allows opening storage blocks (chests, crafting tables, furnaces, etc.) while
 * ToggleSneak is active.
 *
 * <p>The {@code ClientPlayerInteractionManager.interactBlockInternal} method short-circuits
 * the block interaction when the player is sneaking AND holding an item. The check
 * it performs is {@code if (player.shouldCancelInteraction() && !getStackInHand(hand).isEmpty())}
 * — at the bytecode level, the actual call is {@code invokevirtual
 * ClientPlayerEntity.shouldCancelInteraction()Z}. That method is inherited from
 * {@code PlayerEntity}, so we have to redirect the call site (not the method
 * definition) to return {@code false} when ToggleSneak is on.
 *
 * <p>After this redirect, the {@code if} in the manager falls through and the
 * {@code PlayerInteractBlockC2SPacket} is sent; the server routes the interaction
 * to the block's use action and the UI opens.
 *
 * <p>No-op when ToggleSneak is off — vanilla behavior is preserved.
 */
@Mixin(ClientPlayerInteractionManager.class)
public class ToggleSneakInteractionMixin {

    @Redirect(
        method = "interactBlockInternal",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/network/ClientPlayerEntity;shouldCancelInteraction()Z")
    )
    private boolean sparrow_bypassSneakForInteraction(ClientPlayerEntity player) {
        if (ToggleSneakState.enabled) {
            return false;
        }
        return player.shouldCancelInteraction();
    }
}
