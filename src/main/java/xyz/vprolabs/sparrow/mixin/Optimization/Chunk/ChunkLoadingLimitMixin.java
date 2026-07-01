package xyz.vprolabs.sparrow.mixin.Optimization.Chunk;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(WorldRenderer.class)
public class ChunkLoadingLimitMixin {

    @Unique
    private static final double SPARROW_PRIORITY_RADIUS_SQ = 16384.0;

    @Unique
    private static boolean sparrow_priorityLogged = false;

    @ModifyConstant(method = "updateChunks", constant = @Constant(doubleValue = 768.0))
    private double sparrow_expandPriorityRadius(double original) {
        if (!sparrow_priorityLogged) {
            sparrow_priorityLogged = true;
            SparrowLogger.debug("ChunkLoadingLimitMixin: expanded priority radius to 128 blocks (" + (int) SPARROW_PRIORITY_RADIUS_SQ + " sq)");
        }
        return SPARROW_PRIORITY_RADIUS_SQ;
    }
}
