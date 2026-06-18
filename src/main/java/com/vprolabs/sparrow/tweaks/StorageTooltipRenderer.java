package com.vprolabs.sparrow.tweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public final class StorageTooltipRenderer {

    private static final int SLOT_SIZE = 18;
    private static final int SLOT_GAP = 2;
    private static final int MAX_COLS = 9;
    private static final int PADDING = 5;
    private static final int LINE_GAP = 2;
    private static final int TEXT_GRID_GAP = 4;
    private static final int DETAIL_PANEL_GAP = 4;
    private static final int HINT_GAP = 2;
    private static final int MOUSE_OFFSET = 12;
    private static final int SCREEN_MARGIN = 2;
    private static final int EMPTY_LINE_FALLBACK_H = 10;

    private static final int BG_COLOR      = 0xF0100010;
    private static final int BORDER_TOP    = 0xFF500050;
    private static final int BORDER_BOTTOM = 0xFF300030;
    private static final int SLOT_BG       = 0x44000000;
    private static final int SLOT_LINE     = 0x22AAAAAA;
    private static final int SELECTED_BORDER_COLOR = 0xFFFFD700;
    private static final int SELECTED_BORDER_THICK = 2;
    private static final int HINT_COLOR    = 0xFFAAAAAA;

    private static final String EMPTY_TEXT = "Empty";
    private static final String SCROLL_HINT_TEXT = "\u25C0 Scroll to navigate \u25B6";

    private StorageTooltipRenderer() {}

    public static List<ItemStack> extractItems(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return List.of();

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            List<ItemStack> out = new ArrayList<>();
            // iterateNonEmpty() skips empty slots and avoids the stream wrapper +
            // Spliterator allocation of .stream(). (1.21.11 renamed iterate() to
            // iterateNonEmpty() — empty-slot iteration is no longer public API.)
            for (ItemStack item : container.iterateNonEmpty()) {
                out.add(item);
            }
            return out;
        }

        BundleContentsComponent bundle = stack.get(DataComponentTypes.BUNDLE_CONTENTS);
        if (bundle != null) {
            List<ItemStack> out = new ArrayList<>();
            for (ItemStack item : bundle.iterate()) {
                out.add(item);
            }
            return out;
        }

        return List.of();
    }

    public static void renderBasic(DrawContext ctx, ItemStack sourceStack, List<ItemStack> items, int mouseX, int mouseY) {
        render(ctx, sourceStack, items, -1, mouseX, mouseY, false);
    }

    public static void renderWithDetail(DrawContext ctx, ItemStack sourceStack, List<ItemStack> items, int selectedIndex, int mouseX, int mouseY) {
        render(ctx, sourceStack, items, selectedIndex, mouseX, mouseY, true);
    }

    private static void render(DrawContext ctx, ItemStack sourceStack, List<ItemStack> items, int selectedIndex, int mouseX, int mouseY, boolean enhanced) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null) return;
        if (sourceStack == null || sourceStack.isEmpty()) return;
        if (items == null) items = List.of();
        TextRenderer font = client.textRenderer;
        if (font == null) return;

        Item.TooltipContext tooltipCtx = client.world != null
            ? Item.TooltipContext.create(client.world)
            : Item.TooltipContext.DEFAULT;

        // Build source tooltip lines explicitly so the name is always shown
        // (custom OR default) and empty component sections are skipped.
        // Pull the name from getName() directly — it returns the formatted name
        // regardless of whether a custom name is set.
        // Defensive fallback chain:
        //   1. CUSTOM_NAME data component (set via anvil)
        //   2. Item.getName(stack) (default localized name like "White Shulker Box")
        //   3. Translation key fallback (when item registry name is the only signal)
        List<Text> sourceLines = new ArrayList<>();
        Text sourceName = sourceStack.getName();
        if (sourceName == null || sourceName.getString().isEmpty()) {
            sourceName = sourceStack.getItem().getName(sourceStack);
        }
        if (sourceName == null || sourceName.getString().isEmpty()) {
            String key = sourceStack.getItem().getTranslationKey();
            sourceName = Text.translatable(key);
        }
        sourceLines.add(sourceName);

        // Add component tooltip lines only if the source stack actually has data
        // for them. We use getTooltip(BASIC) for the component text but strip the
        // first line (the name) to avoid duplication, and only keep the rest if
        // the component sections produced anything beyond the name.
        if (client.player != null) {
            List<Text> fullTooltip = sourceStack.getTooltip(tooltipCtx, client.player, TooltipType.BASIC);
            if (fullTooltip != null && fullTooltip.size() > 1) {
                for (int i = 1; i < fullTooltip.size(); i++) {
                    Text extra = fullTooltip.get(i);
                    if (extra == null) continue;
                    sourceLines.add(extra);
                }
            }
        }

        int totalSlots = items.size();
        int cols = totalSlots == 0 ? 1 : Math.min(MAX_COLS, totalSlots);
        int rows = totalSlots == 0 ? 1 : (int) Math.ceil((double) totalSlots / cols);
        int gridW = cols * SLOT_SIZE + (cols - 1) * SLOT_GAP;
        int gridH = rows * SLOT_SIZE + (rows - 1) * SLOT_GAP;

        List<Text> detailLines = List.of();
        int detailH = 0;
        int detailW = 0;
        if (enhanced && selectedIndex >= 0 && selectedIndex < totalSlots) {
            ItemStack sel = items.get(selectedIndex);
            if (sel != null && !sel.isEmpty()) {
                if (client.player != null) {
                    detailLines = sel.getTooltip(tooltipCtx, client.player, TooltipType.BASIC);
                }
                if (detailLines == null || detailLines.isEmpty()) {
                    Text fallback = sel.getName();
                    if (fallback == null || fallback.getString().isEmpty()) {
                        fallback = sel.getItem().getName(sel);
                    }
                    detailLines = fallback == null ? List.of() : List.of(fallback);
                } else {
                    // Defensive: ensure the first line is non-null and non-empty
                    List<Text> cleaned = new ArrayList<>(detailLines.size());
                    for (Text t : detailLines) {
                        if (t != null) cleaned.add(t);
                    }
                    detailLines = cleaned;
                }
            }
            for (Text line : detailLines) {
                int w = font.getWidth(line);
                if (w > detailW) detailW = w;
                detailH += font.fontHeight + LINE_GAP;
            }
            if (detailH > 0) detailH -= LINE_GAP;
        }

        List<Text> gridLines;
        if (totalSlots == 0) {
            Text empty = Text.literal(EMPTY_TEXT);
            gridLines = List.of(empty);
        } else {
            gridLines = List.of();
        }
        int gridLabelW = 0;
        int gridLabelH = 0;
        for (Text line : gridLines) {
            int w = font.getWidth(line);
            if (w > gridLabelW) gridLabelW = w;
            gridLabelH += font.fontHeight + LINE_GAP;
        }
        if (gridLabelH > 0) gridLabelH -= LINE_GAP;

        int sourceW = 0;
        int sourceH = 0;
        for (Text line : sourceLines) {
            int w = font.getWidth(line);
            if (w > sourceW) sourceW = w;
            sourceH += font.fontHeight + LINE_GAP;
        }
        if (sourceH > 0) sourceH -= LINE_GAP;

        Text hintLine = enhanced ? Text.literal(SCROLL_HINT_TEXT) : null;
        int hintW = 0;
        int hintH = 0;
        if (hintLine != null) {
            hintW = font.getWidth(hintLine);
            hintH = font.fontHeight;
        }

        int innerW = sourceW;
        int innerH = sourceH;
        if (totalSlots == 0) {
            innerW = Math.max(innerW, gridLabelW);
            innerH += (sourceH > 0 ? TEXT_GRID_GAP : 0) + gridLabelH;
        } else {
            innerW = Math.max(innerW, gridW);
            innerH += (sourceH > 0 ? TEXT_GRID_GAP : 0) + gridH;
        }
        if (enhanced && detailH > 0) {
            innerW = Math.max(innerW, detailW);
            innerH += (gridH > 0 || totalSlots == 0 ? DETAIL_PANEL_GAP : 0) + detailH;
        }
        if (enhanced && hintH > 0) {
            innerW = Math.max(innerW, hintW);
            innerH += (innerH > 0 ? HINT_GAP : 0) + hintH;
        }
        if (innerH <= 0) innerH = EMPTY_LINE_FALLBACK_H;

        int panelW = innerW + PADDING * 2;
        int panelH = innerH + PADDING * 2;
        int screenW = client.getWindow().getScaledWidth();
        int screenH = client.getWindow().getScaledHeight();

        if (panelW > screenW - SCREEN_MARGIN * 2) panelW = screenW - SCREEN_MARGIN * 2;
        if (panelH > screenH - SCREEN_MARGIN * 2) panelH = screenH - SCREEN_MARGIN * 2;

        int px = mouseX - panelW / 2;
        int py = mouseY - panelH - MOUSE_OFFSET;
        if (py < SCREEN_MARGIN) py = mouseY + MOUSE_OFFSET;
        if (py + panelH > screenH - SCREEN_MARGIN) py = screenH - panelH - SCREEN_MARGIN;
        if (py < SCREEN_MARGIN) py = SCREEN_MARGIN;
        if (px < SCREEN_MARGIN) px = SCREEN_MARGIN;
        if (px + panelW > screenW - SCREEN_MARGIN) px = screenW - panelW - SCREEN_MARGIN;
        if (px < SCREEN_MARGIN) px = SCREEN_MARGIN;

        ctx.fill(px, py, px + panelW, py + panelH, BG_COLOR);
        ctx.fill(px, py, px + panelW, py + 1, BORDER_TOP);
        ctx.fill(px, py + panelH - 1, px + panelW, py + panelH, BORDER_BOTTOM);
        ctx.fill(px, py, px + 1, py + panelH, BORDER_TOP);
        ctx.fill(px + panelW - 1, py, px + panelW, py + panelH, BORDER_BOTTOM);

        int textX = px + PADDING;
        int textY = py + PADDING;
        for (Text line : sourceLines) {
            ctx.drawText(font, line, textX, textY, 0xFFFFFF, true);
            textY += font.fontHeight + LINE_GAP;
        }
        if (sourceH > 0) textY -= LINE_GAP;

        int labelX = px + (panelW - gridLabelW) / 2;
        int gridY0;
        if (totalSlots == 0) {
            if (sourceH > 0) textY += TEXT_GRID_GAP;
            gridY0 = textY;
            for (Text line : gridLines) {
                ctx.drawText(font, line, labelX, gridY0, 0xFFFFFF, true);
                gridY0 += font.fontHeight + LINE_GAP;
            }
            textY = gridY0;
            if (gridLabelH > 0) textY -= LINE_GAP;
        } else {
            if (sourceH > 0) textY += TEXT_GRID_GAP;
            gridY0 = textY;
            int gridX0 = px + (panelW - gridW) / 2;
            for (int i = 0; i < totalSlots; i++) {
                ItemStack item = items.get(i);
                int col = i % cols;
                int row = i / cols;
                int sx = gridX0 + col * (SLOT_SIZE + SLOT_GAP);
                int sy = gridY0 + row * (SLOT_SIZE + SLOT_GAP);

                ctx.fill(sx, sy, sx + SLOT_SIZE, sy + SLOT_SIZE, SLOT_BG);
                ctx.fill(sx, sy, sx + SLOT_SIZE, sy + 1, SLOT_LINE);

                if (item != null && !item.isEmpty()) {
                    int ix = sx + 1;
                    int iy = sy + 1;
                    ctx.drawItem(item, ix, iy);
                    ctx.drawStackOverlay(font, item, ix, iy);
                }

                if (enhanced && i == selectedIndex) {
                    int t = SELECTED_BORDER_THICK;
                    ctx.fill(sx, sy, sx + SLOT_SIZE, sy + t, SELECTED_BORDER_COLOR);
                    ctx.fill(sx, sy + SLOT_SIZE - t, sx + SLOT_SIZE, sy + SLOT_SIZE, SELECTED_BORDER_COLOR);
                    ctx.fill(sx, sy, sx + t, sy + SLOT_SIZE, SELECTED_BORDER_COLOR);
                    ctx.fill(sx + SLOT_SIZE - t, sy, sx + SLOT_SIZE, sy + SLOT_SIZE, SELECTED_BORDER_COLOR);
                }
            }
            textY = gridY0 + gridH;
        }

        if (enhanced && detailH > 0) {
            textY += DETAIL_PANEL_GAP;
            int detailX = px + PADDING;
            for (Text line : detailLines) {
                ctx.drawText(font, line, detailX, textY, 0xFFFFFF, true);
                textY += font.fontHeight + LINE_GAP;
            }
            if (detailH > 0) textY -= LINE_GAP;
        }

        if (enhanced && hintH > 0) {
            textY += HINT_GAP;
            int hintX = px + (panelW - hintW) / 2;
            if (hintX < px + PADDING) hintX = px + PADDING;
            ctx.drawText(font, hintLine, hintX, textY, HINT_COLOR, true);
        }
    }
}
