package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.state.HudState;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.item.Items;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class ShieldTrackerMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void sparrow_trackShield(CallbackInfo ci) {
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            boolean usingShield = player.isUsingItem()
                && (player.getMainHandStack().isOf(Items.SHIELD)
                || player.getOffHandStack().isOf(Items.SHIELD));
            HudState.update(usingShield);
    }
}
