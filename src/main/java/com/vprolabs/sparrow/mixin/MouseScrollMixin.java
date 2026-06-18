package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.SparrowMod;
import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.config.ConfigReader;
import com.vprolabs.sparrow.state.StoragePreviewState;
import com.vprolabs.sparrow.tweaks.SparrowZoomState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Mouse.class)
public class MouseScrollMixin {

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void sparrow_onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();

        // Zoom scroll: if zoom key is held, adjust zoom level and cancel scroll
        if (SparrowMod.ZOOM_KEY.isPressed()) {
            double direction = Math.signum(vertical);
            if (direction != 0) {
                double min = ConfigCache.zoomMin;
                double max = ConfigCache.zoomMax;
                double next = SparrowZoomState.targetZoom + direction;
                if (next < min) next = min;
                if (next > max) next = max;
                if (next != SparrowZoomState.targetZoom) {
                    SparrowZoomState.targetZoom = next;
                    ConfigCache.zoomLevel = (float) next;
                    ConfigReader.saveFromCache();
                }
            }
            ci.cancel();
            return;
        }

        // Storage preview navigation: if storage preview key is held and hovering a storage item, navigate
        if (SparrowMod.isPreviewKeyPressed()
                && StoragePreviewState.lastHoveredSlot != null
                && client.player != null) {
            ItemStack hovered = StoragePreviewState.lastHoveredSlot.getStack();
            if (!hovered.isEmpty()) {
                List<ItemStack> items = com.vprolabs.sparrow.tweaks.StorageTooltipRenderer.extractItems(hovered);
                if (!items.isEmpty()) {
                    StoragePreviewState.onScroll(vertical, items.size());
                    ci.cancel();
                    return;
                }
            }
        }

        // Normal mouse wheel disable (for hotbar etc.)
        if (ConfigCache.disableMouseWheel && client.currentScreen == null) {
            ci.cancel();
        }
    }
}
