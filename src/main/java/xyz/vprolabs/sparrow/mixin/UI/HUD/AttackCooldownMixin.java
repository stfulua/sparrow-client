package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.mixin.Utils.PlayerEntityAccessor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class AttackCooldownMixin {

    @Unique
    private int sparrow_lastSlot = -1;

    @Inject(method = "setSelectedSlot", at = @At("TAIL"))
    private void sparrow_onSlotChange(int slot, CallbackInfo ci) {
            if (slot == sparrow_lastSlot) return;
            sparrow_lastSlot = slot;
            PlayerEntity player = ((PlayerInventory)(Object)this).player;
            int ticks = ((PlayerEntityAccessor) player).getTicksSinceLastAttack();
            float period = player.getAttackCooldownProgressPerTick();
            if (ticks > (int) Math.ceil(period)) {
                ((PlayerEntityAccessor) player).setTicksSinceLastAttack((int) Math.ceil(period));
            }
    }
}
