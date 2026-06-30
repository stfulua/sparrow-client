package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.BossBarHud;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BossBarHud.class)
public class BossBarSkipMixin {

    @Unique
    private static boolean sparrow_bossBarLogged = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void sparrow_skipBossBar(DrawContext context, CallbackInfo ci) {
            if (!sparrow_bossBarLogged) {
                sparrow_bossBarLogged = true;
                SparrowLogger.debug("BossBarSkipMixin: disabled boss bar rendering");
            }
            ci.cancel();
    }
}
