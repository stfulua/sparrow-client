package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.state.KnockbackState;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class KnockbackPredictorMixin {

    @Inject(method = "takeKnockback", at = @At("HEAD"))
    private void sparrow_captureKnockback(double velocity, double x, double z, CallbackInfo ci) {
            LivingEntity entity = (LivingEntity)(Object)this;
            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player != null && entity.getId() == client.player.getId()) {
                KnockbackState.onKnockback(x, z);
            }
    }
}
