package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.gui.DrawContext;
import xyz.vprolabs.sparrow.state.HudState;

public final class ShieldChargeRenderer {
    private static final int SIZE = 8;
    private static final long ACTIVE_DISPLAY_MS = 500;
    private static long activeDisplayStart = 0;

    private ShieldChargeRenderer() {}

    public static void render(DrawContext ctx) {
        if (HudState.isChargingNow()) {
            activeDisplayStart = 0;
            drawIcon(ctx, 0xFFFFA500, 1.0f);
        } else if (HudState.isActiveNow()) {
            if (activeDisplayStart == 0) activeDisplayStart = System.currentTimeMillis();
            long elapsed = System.currentTimeMillis() - activeDisplayStart;
            if (elapsed >= ACTIVE_DISPLAY_MS) return;
            float alpha = 1.0f - (elapsed / (float) ACTIVE_DISPLAY_MS);
            drawIcon(ctx, 0xFF00CC00, alpha);
        } else {
            activeDisplayStart = 0;
        }
    }

    private static void drawIcon(DrawContext ctx, int color, float alpha) {
        int w = ctx.getScaledWindowWidth();
        int h = ctx.getScaledWindowHeight();
        // Bottom-center, just above hotbar
        int x = (w - SIZE) / 2;
        int y = h - 46;

        int a = (int) (alpha * 255);
        if (a < 0) a = 0;
        if (a > 255) a = 255;
        int col = (a << 24) | (color & 0xFFFFFF);

        try {
            ctx.getMatrices().pushMatrix();
            ctx.fill(x, y, x + SIZE, y + SIZE, col);
            // Lighter border
            int border = (a << 24) | 0xFFFFFF;
            ctx.fill(x - 1, y - 1, x, y + SIZE + 1, border);
            ctx.fill(x - 1, y - 1, x + SIZE + 1, y, border);
            ctx.fill(x + SIZE, y - 1, x + SIZE + 1, y + SIZE + 1, border);
            ctx.fill(x - 1, y + SIZE, x + SIZE + 1, y + SIZE + 1, border);
        } finally {
            ctx.getMatrices().popMatrix();
        }
    }
}
