package xyz.vprolabs.sparrow.state;

import net.minecraft.util.math.BlockPos;
import java.util.HashMap;
import java.util.Map;

public final class GhostBlockState {
    public static final Map<BlockPos, Long> ghostBlocks = new HashMap<>();
    private static final long GHOST_DISPLAY_MS = 2000;

    private GhostBlockState() {}

    public static void markGhost(BlockPos pos) {
        ghostBlocks.put(pos, System.currentTimeMillis());
    }

    public static void tick() {
        if (ghostBlocks.isEmpty()) return;
        long now = System.currentTimeMillis();
        ghostBlocks.entrySet().removeIf(e -> now - e.getValue() > GHOST_DISPLAY_MS);
    }

}
