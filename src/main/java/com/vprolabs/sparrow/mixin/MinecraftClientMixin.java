package com.vprolabs.sparrow.mixin;

// DEFERRED-INIT WARNING:
// ConfigCache is loaded here on the first render frame, not in SparrowMod.onInitializeClient().
// This means any code that runs in a constructor, static initializer, or early mixin
// (before the first render tick) must NOT read ConfigCache -- it will see Java defaults.
// Contributors: do not move ConfigCache reads earlier than this point.

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.config.ConfigReader;
import com.vprolabs.sparrow.logging.SparrowLogger;
import com.vprolabs.sparrow.tweaks.SparrowGlintLayers;
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

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_onFirstRender(boolean tick, CallbackInfo ci) {
        if (sparrow_initialized) return;
        sparrow_initialized = true;

        SparrowLogger.info("=== Sparrow: deferred init (post-game-load) ===");

        MinecraftClient client = MinecraftClient.getInstance();

        int dist = client.options.getViewDistance().getValue();
        if (dist > 8 || dist < 2) {
            ((SimpleOptionAccessor)(Object) client.options.getViewDistance()).setValue(8);
            client.options.write();
            if (client.worldRenderer != null) client.worldRenderer.reload();
            SparrowLogger.info("Forced render distance from " + dist + " to 8");
        }

        int sim = client.options.getSimulationDistance().getValue();
        if (sim > 2 || sim < 1) {
            ((SimpleOptionAccessor)(Object) client.options.getSimulationDistance()).setValue(2);
            client.options.write();
            SparrowLogger.info("Forced simulation distance from " + sim + " to 2");
        }

        ((SimpleOptionAccessor)(Object)client.options.getGamma()).setValue(15.0);
        SparrowLogger.info("Forced gamma to 15.0 (fullbright)");

        ConfigReader config = ConfigReader.load();
        ConfigCache.load(config);
        SparrowGlintLayers.init();

        if (config.isAllDefaults()) {
            SparrowLogger.warn("Config not found or all-defaults -- check sparrow-minecraft/config.json");
        }
    }
}
