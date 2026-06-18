package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.render.chunk.NormalizedRelativePos;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WorldRenderer.class)
public class DistantChunkLodMixin {

    @Unique
    private static boolean sparrow_lodLogged = false;

    @Inject(method = "scheduleChunkTranslucencySort", at = @At("HEAD"), cancellable = true)
    private void sparrow_skipTranslucencySort(ChunkBuilder.BuiltChunk chunk, NormalizedRelativePos pos, Vec3d vec3d, boolean bl, boolean bl2, CallbackInfo ci) {

        int lodDist = ConfigCache.lodDistance;
        if (lodDist <= 0) return;

        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null) return;

        BlockPos origin = chunk.getOrigin();
        double dx = origin.getX() - client.player.getX();
        double dz = origin.getZ() - client.player.getZ();
        double maxDist = lodDist * 16.0;
        if ((dx * dx + dz * dz) >= maxDist * maxDist) {
            if (!sparrow_lodLogged) {
                sparrow_lodLogged = true;
                SparrowLogger.debug("DistantChunkLodMixin: LOD active at " + lodDist + " chunks");
            }
            ci.cancel();
        }
    }
}
