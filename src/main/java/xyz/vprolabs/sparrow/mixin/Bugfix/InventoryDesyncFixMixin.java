package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.state.InventoryState;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class InventoryDesyncFixMixin {

    @Unique
    private int sparrow_lastSelectedSlot = -1;

    @Inject(method = "tick", at = @At("HEAD"))
    private void sparrow_checkInventory(CallbackInfo ci) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            long now = System.currentTimeMillis();
            if (now - InventoryState.lastCheckTime < InventoryState.CHECK_INTERVAL) return;
            InventoryState.lastCheckTime = now;

            int total = 0;
            var inv = player.getInventory();
            for (int i = 0; i < inv.size(); i++) {
                if (!inv.getStack(i).isEmpty()) total++;
            }

            if (InventoryState.expectedTotalCount < 0) {
                InventoryState.expectedTotalCount = total;
            } else if (Math.abs(total - InventoryState.expectedTotalCount) >= 3) {
                int selected = player.getInventory().getSelectedSlot();
                if (sparrow_lastSelectedSlot >= 0 && sparrow_lastSelectedSlot != selected) {
                    player.getInventory().setSelectedSlot(sparrow_lastSelectedSlot);
                }
            }

            InventoryState.expectedTotalCount = total;
            sparrow_lastSelectedSlot = player.getInventory().getSelectedSlot();
    }
}
