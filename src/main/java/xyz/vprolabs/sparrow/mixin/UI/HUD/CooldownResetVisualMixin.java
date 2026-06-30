package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.state.HudState;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class CooldownResetVisualMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_renderCooldownReset(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (MinecraftClient.getInstance().options.hudHidden) return;
            if (!HudState.isResetShowing()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            long elapsed = System.currentTimeMillis() - HudState.resetTime;
            int alpha = (int) (200 * (1.0 - (double) elapsed / 400.0));
            if (alpha < 0) alpha = 0;
            if (alpha > 200) alpha = 200;

            int color = (alpha << 24) | 0xFF4444;
            String text = "COOLDOWN RESET";
            int textWidth = client.textRenderer.getWidth(text);
            int scaledW = context.getScaledWindowWidth();
            int scaledH = context.getScaledWindowHeight();
            int x = (scaledW - textWidth) / 2;
            int y = scaledH / 2 + 25;

            try {
                context.getMatrices().pushMatrix();
                context.drawText(client.textRenderer, text, x, y, color, true);
            } finally {
                context.getMatrices().popMatrix();
            }
    }
}
