package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

/**
 * Utility helpers for HUD rendering — reduces pushMatrix/popMatrix/drawText boilerplate.
 */
public final class HudHelper {

    private HudHelper() {}

    /** Draw filled-background text with push/pop. Returns the text width. */
    public static int drawBoxedText(DrawContext ctx, TextRenderer font, String text, int x, int y, int bgColor, int textColor) {
        int tw = font.getWidth(text);
        try {
            ctx.getMatrices().pushMatrix();
            ctx.fill(x - 1, y - 1, x + tw + 1, y + font.fontHeight + 1, bgColor);
            ctx.drawText(font, text, x, y, textColor, true);
        } finally {
            ctx.getMatrices().popMatrix();
        }
        return tw;
    }

    /** Draw text with push/pop (no background). */
    public static int drawText(DrawContext ctx, TextRenderer font, String text, int x, int y, int color) {
        int tw = font.getWidth(text);
        try {
            ctx.getMatrices().pushMatrix();
            ctx.drawText(font, text, x, y, color, true);
        } finally {
            ctx.getMatrices().popMatrix();
        }
        return tw;
    }

    /** Draw a colored border around a rectangle for move-mode highlight */
    public static void drawBorder(DrawContext ctx, int x, int y, int w, int h, int color) {
        ctx.fill(x - 2, y - 2, x + w + 2, y, color);       // top
        ctx.fill(x - 2, y + h, x + w + 2, y + h + 2, color); // bottom
        ctx.fill(x - 2, y - 2, x, y + h + 2, color);         // left
        ctx.fill(x + w, y - 2, x + w + 2, y + h + 2, color); // right
    }
}
