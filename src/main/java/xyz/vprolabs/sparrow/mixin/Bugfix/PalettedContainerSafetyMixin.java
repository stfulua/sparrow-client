package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.world.chunk.Palette;
import net.minecraft.world.chunk.PalettedContainer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Fixes EntryMissingException crash when servers send corrupted chunk data
 * where palette storage references indices exceeding the palette size.
 *
 * Root cause: uwuPaper/Paper anti-xray or custom world generators can produce
 * chunk sections where the palette storage data contains palette IDs that
 * exceed the palette's actual entry count. This causes Palette.get(id) to
 * throw EntryMissingException, crashing Sodium's chunk builder (and the game).
 *
 * Fix: @Redirect the Palette.get() call within PalettedContainer.get(int) to
 * catch EntryMissingException and scan valid palette entries as fallback.
 * The warning is one-shot per session to avoid log spam.
 *
 * CRITICAL NOTE: palette.getSize() returns ENTRY COUNT, not the max valid index.
 * BiMapPalette can have entries {1: X, 2: Y} where 0 is missing — getSize()=2
 * but get(0) throws. Our fallback MUST also catch and scan further.
 *
 * Differs from PalettedContainerReadFixMixin (which fixes ArrayPalette.readPacket
 * overflow during PACKET READING) in that this fixes the RUNTIME ACCESS crash
 * that occurs when reading already-loaded biome/block data from corrupted storage.
 */
@Mixin(PalettedContainer.class)
public class PalettedContainerSafetyMixin<T> {

    @Unique
    private static boolean sparrow_missingEntryLogged = false;

    @Redirect(
        method = "get(I)Ljava/lang/Object;",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/world/chunk/Palette;get(I)Ljava/lang/Object;")
    )
    private T sparrow_catchMissingEntry(Palette<T> palette, int id) {
        // Fast path: ID is in bounds AND the palette has it
        if (id >= 0 && id < palette.getSize()) {
            try {
                return palette.get(id);
            } catch (Exception ignored) {
                // intentional — entry missing despite in-range id, try fallback
            }
        }

        // Slow path: scan valid entries starting from 0
        int size = palette.getSize();
        for (int i = 0; i < size; i++) {
            try {
                T result = palette.get(i);
                if (!sparrow_missingEntryLogged) {
                    sparrow_missingEntryLogged = true;
                    SparrowLogger.warn("EntryMissingException for id=" + id
                        + " (size=" + size + "), recovered with id=" + i);
                }
                return result;
            } catch (Exception ignored) {
                // intentional — entry i also missing, try next
            }
        }

        // Complete failure — all entries missing. Log and return null.
        if (!sparrow_missingEntryLogged) {
            sparrow_missingEntryLogged = true;
            SparrowLogger.warn("EntryMissingException for id=" + id
                + " (size=" + size + ") — ALL entries missing, returning null");
        }
        return null;
    }
}
