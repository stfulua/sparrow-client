package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.mixin.Utils.PlayerEntityAccessor;
import xyz.vprolabs.sparrow.state.HudState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class SwapCooldownHudMixin {

    @Inject(method = "setSelectedSlot", at = @At("TAIL"))
    private void sparrow_updateCooldownOnSwap(int slot, CallbackInfo ci) {
            PlayerInventory inv = (PlayerInventory)(Object)this;
            PlayerEntity player = inv.player;
            if (player == null) return;

            int ticks = ((PlayerEntityAccessor) player).getTicksSinceLastAttack();
            float period = player.getAttackCooldownProgressPerTick();
            int maxTicks = period > 0 ? (int) Math.ceil(1.0f / period) : 10;

            HudState.update(ticks, maxTicks);
    }
}
