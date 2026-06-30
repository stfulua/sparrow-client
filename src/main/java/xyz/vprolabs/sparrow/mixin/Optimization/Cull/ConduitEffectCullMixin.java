package xyz.vprolabs.sparrow.mixin.Optimization.Cull;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.block.entity.ConduitBlockEntityRenderer;
import net.minecraft.client.render.block.entity.state.ConduitBlockEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ConduitBlockEntityRenderer.class)
public class ConduitEffectCullMixin {

    @Unique
    private static final int SPARROW_MAX_CONDUIT_DIST = 48;

    @Unique
    private static boolean sparrow_conduitLogged = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void sparrow_cullDistantConduit(ConduitBlockEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, CameraRenderState camera, CallbackInfo ci) {
            if (state.pos == null || camera.pos == null) return;

            double dx = state.pos.getX() + 0.5 - camera.pos.x;
            double dy = state.pos.getY() + 0.5 - camera.pos.y;
            double dz = state.pos.getZ() + 0.5 - camera.pos.z;
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > SPARROW_MAX_CONDUIT_DIST * SPARROW_MAX_CONDUIT_DIST) {
                if (!sparrow_conduitLogged) {
                    sparrow_conduitLogged = true;
                    SparrowLogger.debug("ConduitEffectCullMixin: culling conduit effects > " + SPARROW_MAX_CONDUIT_DIST + " blocks");
                }
                ci.cancel();
            }
    }
}
