package xyz.vprolabs.sparrow.mixin.Optimization.Cull;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.chunk.SectionBuilder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SectionBuilder.class)
public class SectionBuilderBlockEntityCullingMixin {

    @Unique
    private static final int SPARROW_MAX_BE_DIST = 48;

    @Unique
    private static boolean sparrow_beCullLogged = false;

    @Unique
    private static boolean sparrow_shouldCull(BlockEntity blockEntity) {

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return false;

        double dx = blockEntity.getPos().getX() + 0.5 - client.player.getX();
        double dy = blockEntity.getPos().getY() + 0.5 - client.player.getY();
        double dz = blockEntity.getPos().getZ() + 0.5 - client.player.getZ();
        double distSq = dx * dx + dy * dy + dz * dz;
        return distSq > (double) SPARROW_MAX_BE_DIST * SPARROW_MAX_BE_DIST;
    }

    @Inject(method = "addBlockEntity", at = @At("HEAD"), cancellable = true)
    private <E extends BlockEntity> void sparrow_skipFarBlockEntity(SectionBuilder.RenderData renderData, E blockEntity, CallbackInfo ci) {
            if (!sparrow_shouldCull(blockEntity)) return;

            if (!sparrow_beCullLogged) {
                sparrow_beCullLogged = true;
                SparrowLogger.debug("SectionBuilderBlockEntityCullingMixin: skipping distant block entity render data");
            }
            ci.cancel();
    }
}
