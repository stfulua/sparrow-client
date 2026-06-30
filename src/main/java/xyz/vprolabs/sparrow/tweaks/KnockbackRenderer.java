package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import xyz.vprolabs.sparrow.state.HudMoveState;
import xyz.vprolabs.sparrow.state.HudPositions;
import xyz.vprolabs.sparrow.state.KnockbackState;

public final class KnockbackRenderer {
    private static final int KB_COLOR = 0xFF44AAFF;
    private static final int BG_COLOR = 0x88000000;

    private KnockbackRenderer() {}

    public static void render(DrawContext ctx, TextRenderer font) {
        if (!KnockbackState.isShowing()) return;

        String text = String.format("KB: %.1f", KnockbackState.kbStrength);
        int[] off = HudPositions.getOffset("knockback");
        int x = 5 + off[0];
        int y = 30 + off[1];
        if (HudMoveState.active) {
            int tw = font.getWidth(text);
            HudMoveState.elementBounds.put("knockback", new int[]{x, y, tw + 2, font.fontHeight + 2});
            HudHelper.drawBorder(ctx, x - 1, y - 1, tw + 2, font.fontHeight + 2, 0xFFFFFFFF);
        }
        HudHelper.drawBoxedText(ctx, font, text, x, y, BG_COLOR, KB_COLOR);
    }
}
