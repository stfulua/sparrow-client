package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.state.HudState;

import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class CooldownResetTrackerMixin {
    @Unique
    private boolean sparrow_wasUsingItem;

    @Inject(method = "tick", at = @At("HEAD"))
    private void sparrow_detectCooldownReset(CallbackInfo ci) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            boolean isUsing = player.isUsingItem();

            if (isUsing && !sparrow_wasUsingItem) {
                float progress = player.getAttackCooldownProgress(0.0f);
                if (progress < 0.99f) {
                    HudState.markReset();
                }
            }

            sparrow_wasUsingItem = isUsing;
            HudState.tickReset();
    }
}
