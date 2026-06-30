package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.state.GhostBlockState;
import xyz.vprolabs.sparrow.state.HudState;

public final class HudRenderer {
    private static final int TEXT_COLOR = 0xFFFFFFFF;
    private static final int WARN_COLOR = 0xFFFF4444;
    private static final int BG_COLOR = 0x88000000;
    private static final int PADDING = 4;

    private static int desyncWarnW = -1;
    private static int pingW = -1;

    private HudRenderer() {}

    public static void render(DrawContext ctx, TextRenderer font) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;
        if (client.options.hudHidden) return;

        GhostBlockState.tick();

        int scaledW = ctx.getScaledWindowWidth();
        int scaledH = ctx.getScaledWindowHeight();

        // Coords
        if (ConfigRegister.coords.get()) {
            String text = String.format("XYZ: %.0f / %.0f / %.0f",
                client.player.getX(), client.player.getY(), client.player.getZ());
            int x = PADDING;
            int y = scaledH - 55;
            HudHelper.drawBoxedText(ctx, font, text, x, y, BG_COLOR, TEXT_COLOR);
        }

        // Ping (cached width)
        if (ConfigRegister.ping.get() && HudState.currentPing > 0) {
            String text = "Ping: " + HudState.currentPing + "ms";
            int tw = font.getWidth(text);
            if (pingW < 0) pingW = tw;
            int x = scaledW - tw - PADDING - 1;
            int y = PADDING;
            HudHelper.drawBoxedText(ctx, font, text, x, y, BG_COLOR, TEXT_COLOR);
        }

        // Desync warning (moved 50px higher, cached width)
        if (ConfigRegister.desync.get()) {
            long elapsed = System.currentTimeMillis() - HudState.lastDesyncTime;
            if (elapsed < HudState.DESYNC_HIDE_DURATION) {
                String text = "\u00a7c\u26a0 Desync detected!";
                if (desyncWarnW < 0) desyncWarnW = font.getWidth(text);
                int x = (scaledW - desyncWarnW) / 2;
                int y = scaledH / 2 - 70;
                HudHelper.drawBoxedText(ctx, font, text, x, y, BG_COLOR, WARN_COLOR);
            }
        }

        // These have their own matrix push/pop internally
        GhostBlockRenderer.render(ctx, font);
        KnockbackRenderer.render(ctx, font);
    }
}
