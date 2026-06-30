package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.SparrowMod;
import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.config.ConfigReader;
import xyz.vprolabs.sparrow.state.StoragePreviewState;
import xyz.vprolabs.sparrow.tweaks.SparrowZoomState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.Mouse;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Mouse.class)
public class MouseScrollMixin {
    @Unique private static long sparrow_lastSaveTime = 0L;

    @Inject(method = "onMouseScroll", at = @At("HEAD"), cancellable = true)
    private void sparrow_onMouseScroll(long window, double horizontal, double vertical, CallbackInfo ci) {
MinecraftClient client = MinecraftClient.getInstance();

        // Zoom scroll: if zoom key is held, adjust zoom level and cancel scroll
        if (SparrowMod.ZOOM_KEY.isPressed()) {
            double direction = Math.signum(vertical);
            if (direction != 0) {
                double min = ConfigRegister.zoomMin.get();
                double max = ConfigRegister.zoomMax.get();
                double next = SparrowZoomState.targetZoom + direction;
                if (next < min) next = min;
                if (next > max) next = max;
                if (next != SparrowZoomState.targetZoom) {
                    SparrowZoomState.targetZoom = next;
                    ConfigRegister.zoomLevel.set((float) next);
                    long now = System.currentTimeMillis();
                    if (now - sparrow_lastSaveTime > 500) {
                        sparrow_lastSaveTime = now;
                        ConfigReader.saveFromCache();
                    }
                }
            }
            ci.cancel();
            return;
        }

        // Storage preview navigation: if storage preview key is pressed and hovering a storage item, navigate
        if (SparrowMod.isPreviewKeyPressed()
                && StoragePreviewState.lastHoveredSlot != null
                && client.player != null) {
            ItemStack hovered = StoragePreviewState.lastHoveredSlot.getStack();
            if (!hovered.isEmpty()) {
                List<ItemStack> items = xyz.vprolabs.sparrow.tweaks.StorageTooltipRenderer.extractItems(hovered);
                if (!items.isEmpty()) {
                    StoragePreviewState.onScroll(vertical, items.size());
                    ci.cancel();
                    return;
                }
            }
        }

        // Normal mouse wheel disable (for hotbar etc.)
        if (ConfigRegister.disableMouseWheel.get() && client.currentScreen == null) {
            ci.cancel();
        }
}

}
