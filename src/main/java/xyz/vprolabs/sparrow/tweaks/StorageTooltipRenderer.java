package xyz.vprolabs.sparrow.tweaks;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.BundleContentsComponent;
import net.minecraft.component.type.ContainerComponent;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.component.type.LoreComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;

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
    private static final int MIN_PANEL_W = 64;
    private static final int MIN_PANEL_H = 24;

    private static final int BG_COLOR      = 0xF0100010;
    private static final int BORDER_TOP    = 0xFF500050;
    private static final int BORDER_BOTTOM = 0xFF300030;
    private static final int SLOT_BG       = 0x44000000;
    private static final int SLOT_LINE     = 0x22AAAAAA;
    private static final int SELECTED_BORDER_COLOR = 0xFFFFD700;
    private static final int SELECTED_BORDER_THICK = 2;
    private static final int HINT_COLOR    = 0xFFAAAAAA;
    private static final int TEXT_COLOR    = 0xFFFFFFFF;
    private static final int EMPTY_COLOR   = 0xFFCCCCCC;

    private static final String EMPTY_TEXT = "Empty";
    private static final String SCROLL_HINT_TEXT = "\u25C0 Scroll to navigate \u25B6";

    private StorageTooltipRenderer() {}

    public static List<ItemStack> extractItems(ItemStack stack) {
            if (stack == null || stack.isEmpty()) return List.<ItemStack>of();

        ContainerComponent container = stack.get(DataComponentTypes.CONTAINER);
        if (container != null) {
            List<ItemStack> out = new ArrayList<>();
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

        return List.<ItemStack>of();
    }

    public static boolean hasStorageContent(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return false;
        return stack.get(DataComponentTypes.CONTAINER) != null
            || stack.get(DataComponentTypes.BUNDLE_CONTENTS) != null;
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
        if (items == null) items = List.<ItemStack>of();
        TextRenderer font = client.textRenderer;
        if (font == null) return;

        Item.TooltipContext tooltipCtx = client.world != null
            ? Item.TooltipContext.create(client.world)
            : Item.TooltipContext.DEFAULT;

        List<Text> sourceTexts = buildSourceTexts(sourceStack, tooltipCtx, client);
        List<Text> detailTexts = buildDetailTexts(items, selectedIndex, tooltipCtx, client);
        Text hintText = enhanced ? Text.literal(SCROLL_HINT_TEXT) : null;
        Text emptyText = items.isEmpty() ? Text.literal(EMPTY_TEXT) : null;

        int totalSlots = items.size();
        int cols = totalSlots == 0 ? 1 : Math.min(MAX_COLS, totalSlots);
        int rows = totalSlots == 0 ? 1 : (int) Math.ceil((double) totalSlots / cols);
        int gridW = cols * SLOT_SIZE + (cols - 1) * SLOT_GAP;
        int gridH = rows * SLOT_SIZE + (rows - 1) * SLOT_GAP;

        int sourceH = linesHeight(font, sourceTexts.size());

        int innerW = gridW;
        if (innerW < MIN_PANEL_W - PADDING * 2) innerW = MIN_PANEL_W - PADDING * 2;

        List<OrderedText> wrappedSource = wrapAll(font, sourceTexts, innerW);
        List<OrderedText> wrappedDetail = wrapAll(font, detailTexts, innerW);
        List<OrderedText> wrappedHint = hintText == null ? List.<OrderedText>of() : font.wrapLines(hintText, innerW);
        List<OrderedText> wrappedEmpty = emptyText == null ? List.<OrderedText>of() : font.wrapLines(emptyText, innerW);

        int wrappedSourceW = measureOrderedMax(font, wrappedSource);
        int wrappedDetailW = measureOrderedMax(font, wrappedDetail);
        int wrappedHintW = measureOrderedMax(font, wrappedHint);
        int wrappedEmptyW = measureOrderedMax(font, wrappedEmpty);

        int wrappedSourceH = linesHeight(font, wrappedSource.size());
        int wrappedDetailH = linesHeight(font, wrappedDetail.size());
        int wrappedHintH = linesHeight(font, wrappedHint.size());
        int wrappedEmptyH = linesHeight(font, wrappedEmpty.size());

        innerW = wrappedSourceW;
        if (totalSlots == 0) innerW = Math.max(innerW, wrappedEmptyW);
        else innerW = Math.max(innerW, gridW);
        if (enhanced && wrappedDetailH > 0) innerW = Math.max(innerW, wrappedDetailW);
        if (enhanced && wrappedHintH > 0) innerW = Math.max(innerW, wrappedHintW);
        if (innerW < MIN_PANEL_W - PADDING * 2) innerW = MIN_PANEL_W - PADDING * 2;

        int innerH = wrappedSourceH;
        if (sourceH > 0 && (totalSlots == 0 ? wrappedEmptyH > 0 : gridH > 0)) innerH += TEXT_GRID_GAP;
        if (totalSlots == 0) innerH += wrappedEmptyH;
        else innerH += gridH;
        if (enhanced && wrappedDetailH > 0) {
            innerH += (gridH > 0 || totalSlots == 0 ? DETAIL_PANEL_GAP : 0) + wrappedDetailH;
        }
        if (enhanced && wrappedHintH > 0) {
            innerH += (innerH > 0 ? HINT_GAP : 0) + wrappedHintH;
        }
        if (innerH <= 0) innerH = EMPTY_LINE_FALLBACK_H;
        if (innerH < MIN_PANEL_H - PADDING * 2) innerH = MIN_PANEL_H - PADDING * 2;

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

        try {
            ctx.getMatrices().pushMatrix();

            ctx.fill(px, py, px + panelW, py + panelH, BG_COLOR);
            ctx.fill(px, py, px + panelW, py + 1, BORDER_TOP);
            ctx.fill(px, py + panelH - 1, px + panelW, py + panelH, BORDER_BOTTOM);
            ctx.fill(px, py, px + 1, py + panelH, BORDER_TOP);
            ctx.fill(px + panelW - 1, py, px + panelW, py + panelH, BORDER_BOTTOM);

            int textX = px + PADDING;
            int textY = py + PADDING;
            int n = wrappedSource.size();
            for (int i = 0; i < n; i++) {
                OrderedText line = wrappedSource.get(i);
                ctx.drawText(font, line, textX, textY, TEXT_COLOR, true);
                if (i < n - 1) textY += font.fontHeight + LINE_GAP;
            }
            textY += font.fontHeight;
            if (n == 0) textY -= font.fontHeight;

            int gridY0;
            if (totalSlots == 0) {
                if (wrappedSourceH > 0) textY += TEXT_GRID_GAP;
                gridY0 = textY;
                int emptyLabelX = px + (panelW - wrappedEmptyW) / 2;
                int en = wrappedEmpty.size();
                for (int i = 0; i < en; i++) {
                    OrderedText line = wrappedEmpty.get(i);
                    ctx.drawText(font, line, emptyLabelX, gridY0, EMPTY_COLOR, true);
                    if (i < en - 1) gridY0 += font.fontHeight + LINE_GAP;
                }
                textY = gridY0 + font.fontHeight;
                if (en == 0) textY = gridY0;
            } else {
                if (wrappedSourceH > 0) textY += TEXT_GRID_GAP;
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

                    if (!item.isEmpty()) {
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

            if (enhanced && wrappedDetailH > 0) {
                textY += DETAIL_PANEL_GAP;
                int detailX = px + PADDING;
                int dn = wrappedDetail.size();
                for (int i = 0; i < dn; i++) {
                    OrderedText line = wrappedDetail.get(i);
                    ctx.drawText(font, line, detailX, textY, TEXT_COLOR, true);
                    if (i < dn - 1) textY += font.fontHeight + LINE_GAP;
                }
                textY += font.fontHeight;
                if (dn == 0) textY -= font.fontHeight;
            }

            if (enhanced && wrappedHintH > 0) {
                textY += HINT_GAP;
                int hn = wrappedHint.size();
                for (int i = 0; i < hn; i++) {
                    OrderedText line = wrappedHint.get(i);
                    int lw = font.getWidth(line);
                    int lx = px + (panelW - lw) / 2;
                    if (lx < px + PADDING) lx = px + PADDING;
                    ctx.drawText(font, line, lx, textY, HINT_COLOR, true);
                    if (i < hn - 1) textY += font.fontHeight + LINE_GAP;
                }
            }
        } finally {
            ctx.getMatrices().popMatrix();
        }
    }

    private static List<Text> buildSourceTexts(ItemStack sourceStack, Item.TooltipContext tooltipCtx, MinecraftClient client) {
        List<Text> built = new ArrayList<>();
        Text sourceName = sourceStack.getName();
        if (sourceName == null || sourceName.getString().isEmpty()) {
            sourceName = sourceStack.getItem().getName(sourceStack);
        }
        if (sourceName == null || sourceName.getString().isEmpty()) {
            String key = sourceStack.getItem().getTranslationKey();
            if (key != null && !key.isEmpty()) {
                sourceName = Text.translatable(key);
            } else {
                sourceName = Text.empty();
            }
        }
        built.add(sourceName);

        LoreComponent lore = sourceStack.get(DataComponentTypes.LORE);
        if (lore != null) {
            for (Text line : lore.lines()) {
                if (line != null) built.add(line);
            }
        }

        appendEnchantLines(built, sourceStack.get(DataComponentTypes.ENCHANTMENTS));
        appendEnchantLines(built, sourceStack.get(DataComponentTypes.STORED_ENCHANTMENTS));

        return built;
    }

    private static List<Text> buildDetailTexts(List<ItemStack> items, int selectedIndex, Item.TooltipContext tooltipCtx, MinecraftClient client) {
        if (selectedIndex < 0 || selectedIndex >= items.size()) return List.<Text>of();
        ItemStack sel = items.get(selectedIndex);
        if (sel == null || sel.isEmpty()) return List.<Text>of();
        List<Text> result = null;
        if (client.player != null) {
            result = sel.getTooltip(tooltipCtx, client.player, TooltipType.BASIC);
        }
        if (result == null || result.isEmpty()) {
            Text fallback = sel.getName();
            if (fallback == null || fallback.getString().isEmpty()) {
                fallback = sel.getItem().getName(sel);
            }
            if (fallback == null) return List.<Text>of();
            return List.<Text>of(fallback);
        }
        List<Text> cleaned = new ArrayList<>(result.size());
        for (Text t : result) {
            if (t != null) cleaned.add(t);
        }
        return cleaned;
    }

    private static int measureMax(TextRenderer font, List<Text> texts) {
        int w = 0;
        for (Text t : texts) {
            if (t == null) continue;
            int tw = font.getWidth(t);
            if (tw > w) w = tw;
        }
        return w;
    }

    private static int measureOrderedMax(TextRenderer font, List<OrderedText> lines) {
        int w = 0;
        for (OrderedText o : lines) {
            if (o == null) continue;
            int ow = font.getWidth(o);
            if (ow > w) w = ow;
        }
        return w;
    }

    private static List<OrderedText> wrapAll(TextRenderer font, List<Text> texts, int maxWidth) {
        if (texts.isEmpty()) return List.<OrderedText>of();
        List<OrderedText> out = new ArrayList<>();
        for (Text t : texts) {
            if (t == null) continue;
            out.addAll(font.wrapLines(t, maxWidth));
        }
        return out;
    }

    private static int linesHeight(TextRenderer font, int lineCount) {
        if (lineCount <= 0) return 0;
        return lineCount * font.fontHeight + (lineCount - 1) * LINE_GAP;
    }

    private static void appendEnchantLines(List<Text> out, ItemEnchantmentsComponent ench) {
        if (ench == null || ench.isEmpty()) return;
        for (RegistryEntry<Enchantment> entry : ench.getEnchantments()) {
            int level = ench.getLevel(entry);
            if (level <= 0) continue;
            out.add(Enchantment.getName(entry, level));
        }
    }
}
