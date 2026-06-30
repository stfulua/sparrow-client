package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import xyz.vprolabs.sparrow.state.GhostBlockState;

public final class GhostBlockRenderer {
    private static final int TEXT_COLOR = 0xFFFF4444;
    private static final int BG_COLOR = 0x88000000;
    private static int labelW = -1;
    private static final String PREFIX = "\u00a7cGhost: ";

    private GhostBlockRenderer() {}

    public static void render(DrawContext ctx, TextRenderer font) {
        int count = GhostBlockState.ghostBlocks.size();
        if (count == 0) return;

        String text = PREFIX + count + " blocks";
        int tw = font.getWidth(text);
        if (labelW < 0) labelW = tw;

        HudHelper.drawBoxedText(ctx, font, text, 5, 5, BG_COLOR, TEXT_COLOR);
    }
}
