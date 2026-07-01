package xyz.vprolabs.sparrow.mixin.UI;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import xyz.vprolabs.sparrow.mixin.Utils.SimpleOptionAccessor;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.option.GameOptionsScreen;
import net.minecraft.client.gui.screen.option.VideoOptionsScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VideoOptionsScreen.class)
public class VideoOptionsScreenMixin {

    @Unique
    private static boolean sparrow_gammaLogged = false;

    @Inject(method = "removed", at = @At("HEAD"))
    private void sparrow_lockGammaOnClose(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        SimpleOptionAccessor gammaAccessor = (SimpleOptionAccessor) (Object) client.options.getGamma();
        double currentGamma = client.options.getGamma().getValue();

        if (currentGamma < 15.0) {
            gammaAccessor.setValue(15.0);
            if (!sparrow_gammaLogged) {
                sparrow_gammaLogged = true;
                SparrowLogger.info("VideoOptionsScreenMixin: gamma forced to 15.0 (user tried to lower it)");
            }
        }
    }
}
