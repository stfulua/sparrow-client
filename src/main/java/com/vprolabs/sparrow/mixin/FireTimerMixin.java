package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class FireTimerMixin {

    @Unique
    private static boolean sparrow_fireLogged = false;

    // Cache the last formatted text + width to avoid 60 StringBuilder/Object[]/Float
    // allocations per second when the player is on fire. Format only changes when
    // the tick count's displayed value (ticks/20.0) crosses a 0.1s boundary.
    @Unique
    private static int sparrow_lastFireTicks = -1;
    @Unique
    private static String sparrow_lastFireText = null;
    @Unique
    private static int sparrow_lastFireWidth = 0;

    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_renderFireTimer(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ConfigCache.fireTimer) return;

        MinecraftClient mc = MinecraftClient.getInstance();
        InGameHud self = (InGameHud)(Object)this;
        LivingEntity living = mc.player;
        String source = "client.player";
        if (living == null && mc.getCameraEntity() instanceof LivingEntity le) {
            living = le;
            source = "getCameraEntity";
        }
        if (living == null) return;
        if (living.isRemoved() || !living.isAlive() || living.isSpectator()) return;

        int ticks = living.getFireTicks();
        if (ticks <= 0) {
            sparrow_lastFireTicks = -1;
            sparrow_lastFireText = null;
            sparrow_lastFireWidth = 0;
            return;
        }

        if (!sparrow_fireLogged) {
            sparrow_fireLogged = true;
            SparrowLogger.debug("FireTimerMixin: fire timer enabled via " + source + ", showing fire ticks: " + ticks);
        }

        // Recompute format only when the displayed value (tenths of a second) changes.
        // 20 ticks/sec, so each 0.1s boundary = 2 ticks.
        int displayBucket = ticks / 2;
        TextRenderer font = self.getTextRenderer();
        if (displayBucket != sparrow_lastFireTicks / 2 || sparrow_lastFireText == null) {
            sparrow_lastFireTicks = ticks;
            sparrow_lastFireText = String.format("\uD83D\uDD25 Fire: %.1fs", ticks / 20.0f);
            sparrow_lastFireWidth = font.getWidth(sparrow_lastFireText);
        }
        String text = sparrow_lastFireText;
        int textWidth = sparrow_lastFireWidth;
        int textHeight = font.fontHeight;

        int x, y;
        String pos = ConfigCache.fireTimerPos;

        if ("TOP_LEFT".equals(pos)) {
            x = 5;
            y = 5;
        } else if ("TOP_RIGHT".equals(pos)) {
            x = context.getScaledWindowWidth() - textWidth - 5;
            y = 5;
        } else { // BOTTOM_CENTER
            x = (context.getScaledWindowWidth() - textWidth) / 2;
            y = context.getScaledWindowHeight() - 55;
            int maxY = context.getScaledWindowHeight() - textHeight - 25;
            if (y > maxY) y = maxY;
        }

        if (y < 5) y = 5;

        int bgX = x - 2;
        int bgY = y - 2;
        int bgW = textWidth + 4;
        int bgH = textHeight + 4;
        context.fill(bgX, bgY, bgX + bgW, bgY + bgH, 0x88000000);

        context.drawTextWithShadow(font, text, x, y, 0xFFDDDD);
    }
}
