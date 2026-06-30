package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import xyz.vprolabs.sparrow.state.HudState;

public final class CooldownRenderer {
    private static final int BAR_WIDTH = 80;
    private static final int BAR_HEIGHT = 4;
    private static final int BAR_Y_OFFSET = 55;
    private static final int BG_COLOR = 0x88000000;
    private static final int CHARGED_COLOR = 0xFF00FF00;
    private static final int CHARGING_COLOR = 0xFFFFAA00;
    private static final int EMPTY_COLOR = 0xFFFF4444;
    private static final int PADDING = 2;

    private CooldownRenderer() {}

    public static void render(DrawContext ctx, TextRenderer font) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        int scaledW = ctx.getScaledWindowWidth();
        int scaledH = ctx.getScaledWindowHeight();

        int x = (scaledW - BAR_WIDTH) / 2;
        int y = scaledH - BAR_Y_OFFSET;

        try {
            ctx.getMatrices().pushMatrix();

            ctx.fill(x - PADDING, y - PADDING, x + BAR_WIDTH + PADDING, y + BAR_HEIGHT + PADDING, BG_COLOR);

            int filledW = (int) (HudState.trueCooldownProgress * BAR_WIDTH);
            int color;
            if (HudState.trueCooldownProgress >= 0.99f) {
                color = CHARGED_COLOR;
            } else if (HudState.trueCooldownProgress >= 0.5f) {
                color = CHARGING_COLOR;
            } else {
                color = EMPTY_COLOR;
            }
            ctx.fill(x, y, x + filledW, y + BAR_HEIGHT, color);
        } finally {
            ctx.getMatrices().popMatrix();
        }
    }
}
