package xyz.vprolabs.sparrow.mixin.IllegalVanilla;

import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class PortalGuiUnlockMixin {

    @Inject(method = "hasPortalCooldown", at = @At("HEAD"), cancellable = true)
    private void unlockGuiInPortal(CallbackInfoReturnable<Boolean> cir) {
            cir.setReturnValue(false);
    }
}
