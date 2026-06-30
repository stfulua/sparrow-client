package xyz.vprolabs.sparrow.mixin.NotVanilla;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.ScreenHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ScreenHandler.class)
public class CreativeFeatureMixin {

    @Unique
    private static boolean sparrow_cfLogged = false;

    @Inject(method = "onButtonClick", at = @At("HEAD"), cancellable = true)
    private void blockHotbarSaveLoad(PlayerEntity player, int id, CallbackInfoReturnable<Boolean> cir) {
            if (id >= 1 && id <= 9) {
                if (!sparrow_cfLogged) {
                    sparrow_cfLogged = true;
                    SparrowLogger.debug("CreativeFeatureMixin: blocking hotbar save/load");
                }
                cir.setReturnValue(false);
            }
    }
}
