package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.state.ServerSafety;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.item.consume.UseAction;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ShieldDesyncFixMixin {

    @Unique
    private int sparrow_shieldResyncCooldown = 0;

    @Inject(method = "tick", at = @At("HEAD"))
    private void sparrow_fixShieldDesync(CallbackInfo ci) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            if (player == null) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.interactionManager == null) return;
            if (ServerSafety.isFeatureDisabled("shield-desync-fix")) return;

            if (this.sparrow_shieldResyncCooldown > 0) {
                this.sparrow_shieldResyncCooldown--;
                return;
            }

            boolean holdingShield = player.getMainHandStack().isOf(Items.SHIELD)
                || player.getOffHandStack().isOf(Items.SHIELD);
            if (!holdingShield) return;
            if (!client.options.useKey.isPressed()) return;
            if (player.isUsingItem()) return;

            if (player.getOffHandStack().isOf(Items.SHIELD)) {
                UseAction mainUse = player.getMainHandStack().getUseAction();
                if (mainUse == UseAction.EAT || mainUse == UseAction.DRINK) {
                    return;
                }
            }

            Hand hand = player.getMainHandStack().isOf(Items.SHIELD)
                ? Hand.MAIN_HAND
                : Hand.OFF_HAND;
            client.interactionManager.interactItem(player, hand);
            this.sparrow_shieldResyncCooldown = 5;
    }
}
