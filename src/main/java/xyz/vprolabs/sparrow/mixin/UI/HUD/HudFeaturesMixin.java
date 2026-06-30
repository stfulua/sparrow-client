package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.state.HudState;
import xyz.vprolabs.sparrow.tweaks.HudRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class HudFeaturesMixin {
    @Unique private long sparrow_lastPingTime;

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_trackPing(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        // Update ping every 1s using wall-clock time (not frame-counter — frame rate varies)
        long now = System.currentTimeMillis();
        if (now - sparrow_lastPingTime < 1000) return;
        sparrow_lastPingTime = now;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.getNetworkHandler() == null) return;
        var entry = client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
        if (entry != null) {
            HudState.currentPing = entry.getLatency();
        }
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void onRenderHudFeatures(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        HudRenderer.render(context, MinecraftClient.getInstance().textRenderer);
    }
}
