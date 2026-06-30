package xyz.vprolabs.sparrow.mixin.Bugfix;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.collection.IndexedIterable;
import net.minecraft.world.chunk.ArrayPalette;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fixes ArrayIndexOutOfBoundsException in ArrayPalette.readPacket() when a server
 * sends a chunk section with more palette entries than the bit count can address.
 *
 * Root cause: Paper/uwuPaper anti-xray can produce chunk sections where bitsPerEntry=4
 * (16-slot palette) but the palette contains 17+ unique block states, causing
 * ArrayPalette to crash with IndexOutOfBoundsException when writing array[16].
 *
 * Fix: cap palette reads at array.length, skip extra entries from the buffer,
 * and log a warning. This prevents the disconnect without affecting block storage
 * (4-bit storage can only reference indices 0-15 anyway).
 */
@Mixin(ArrayPalette.class)
public class PalettedContainerReadFixMixin<T> {

    @Shadow private T[] array;
    @Shadow private int size;

    @Inject(
        method = "readPacket(Lnet/minecraft/network/PacketByteBuf;Lnet/minecraft/util/collection/IndexedIterable;)V",
        at = @At("HEAD"),
        cancellable = true
    )
    private void sparrow_preventPaletteOverflow(PacketByteBuf buf, IndexedIterable<T> idList, CallbackInfo ci) {
        int paletteSize = buf.readVarInt();
        int capacity = this.array.length;

        // Read up to capacity entries into the array
        int readCount = Math.min(paletteSize, capacity);
        for (int i = 0; i < readCount; i++) {
            int id = buf.readVarInt();
            this.array[i] = idList.get(id);
        }
        this.size = readCount;

        // Skip remaining entries from buffer to maintain correct reader position
        // for the block storage data that follows the palette
        for (int i = readCount; i < paletteSize; i++) {
            buf.readVarInt();
        }

        if (paletteSize > capacity) {
            SparrowLogger.warn("ArrayPalette overflow: " + paletteSize
                + " entries for capacity " + capacity
                + " (bitsPerEntry=" + (31 - Integer.numberOfLeadingZeros(capacity))
                + "), truncated to " + readCount + " entries. "
                + "This is likely a server-side anti-xray issue.");
        }

        ci.cancel();
    }
}
