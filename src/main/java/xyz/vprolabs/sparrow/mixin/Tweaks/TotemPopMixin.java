package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.config.ConfigRegister;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(ClientPlayNetworkHandler.class)
public class TotemPopMixin {
@ModifyArg(
        method = "onEntityStatus",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/particle/ParticleManager;addEmitter(Lnet/minecraft/entity/Entity;Lnet/minecraft/particle/ParticleEffect;I)V"
        ),
        index = 2
    )
    private int sparrow_reduceTotemParticles(int count) {
        return ConfigRegister.smallTotem.get() ? 8 : count;
}
}
