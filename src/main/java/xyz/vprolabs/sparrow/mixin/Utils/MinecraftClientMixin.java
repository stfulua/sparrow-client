package xyz.vprolabs.sparrow.mixin.Utils;

// DEFERRED-INIT WARNING:
// ConfigRegister is loaded here on the first render frame, not in SparrowMod.onInitializeClient().
// This means any code that runs in a constructor, static initializer, or early mixin
// (before the first render tick) must NOT read ConfigRegister -- it will see Java defaults.
// Contributors: do not move ConfigRegister reads earlier than this point.

import xyz.vprolabs.sparrow.config.ConfigReader;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import xyz.vprolabs.sparrow.mixin.Utils.SimpleOptionAccessor;
import xyz.vprolabs.sparrow.tweaks.SparrowGlintLayers;
import net.minecraft.client.MinecraftClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Unique
    private boolean sparrow_initialized = false;

    @Unique
    private long sparrow_lastFullbrightCheck = 0;

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_onFirstRender(boolean tick, CallbackInfo ci) {
            if (sparrow_initialized) return;
            sparrow_initialized = true;

            SparrowLogger.info("=== Sparrow: deferred init (post-game-load) ===");

            MinecraftClient client = MinecraftClient.getInstance();

            // Gamma fullbright — always on
            ((SimpleOptionAccessor)(Object)client.options.getGamma()).setValue(15.0);
            SparrowLogger.info("Forced gamma to 15.0 (fullbright)");

            ConfigReader.load();
            SparrowGlintLayers.init();

            if (ConfigReader.getInstance().isAllDefaults()) {
                SparrowLogger.warn("Config not found or all-defaults -- check sparrow-minecraft/config.json");
            }
    }

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_checkFullbright(boolean tick, CallbackInfo ci) {
        if (!sparrow_initialized) return;

        long now = System.currentTimeMillis();
        if (now - sparrow_lastFullbrightCheck < 60000) return;
        sparrow_lastFullbrightCheck = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options == null) return;
        double gamma = client.options.getGamma().getValue();
        if (gamma < 15.0) {
            ((SimpleOptionAccessor)(Object) client.options.getGamma()).setValue(15.0);
            SparrowLogger.info("Fullbright has been disabled, fixing now.");
        }
    }
}
