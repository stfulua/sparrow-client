package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.mixin.Utils.PlayerEntityAccessor;
import xyz.vprolabs.sparrow.state.HudState;
import net.minecraft.client.network.ClientPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public class TrueCooldownTrackerMixin {
    @Inject(method = "tick", at = @At("TAIL"))
    private void sparrow_trackCooldown(CallbackInfo ci) {
        if (!ConfigRegister.trueCooldown.get()) return;
            ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;
            int ticks = ((PlayerEntityAccessor) player).getTicksSinceLastAttack();
            float period = player.getAttackCooldownProgressPerTick();
            int maxTicks = period > 0 ? (int) Math.ceil(period) : 10;
            HudState.update(ticks, maxTicks);
    }
}
