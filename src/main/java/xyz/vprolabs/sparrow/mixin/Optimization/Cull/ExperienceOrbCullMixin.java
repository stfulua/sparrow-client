package xyz.vprolabs.sparrow.mixin.Optimization.Cull;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.client.render.entity.state.ExperienceOrbEntityRenderState;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.state.CameraRenderState;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceOrbEntityRenderer.class)
public class ExperienceOrbCullMixin {

    @Unique
    private static final int SPARROW_MAX_ORB_DIST = 32;

    @Unique
    private static boolean sparrow_orbLogged = false;

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void sparrow_cullDistantOrb(ExperienceOrbEntityRenderState state, MatrixStack matrices, OrderedRenderCommandQueue commandQueue, CameraRenderState camera, CallbackInfo ci) {
        if (camera.pos == null) return;

        double dx = state.x - camera.pos.x;
        double dy = state.y - camera.pos.y;
        double dz = state.z - camera.pos.z;
        double distSq = dx * dx + dy * dy + dz * dz;

        if (distSq > SPARROW_MAX_ORB_DIST * SPARROW_MAX_ORB_DIST) {
            if (!sparrow_orbLogged) {
                sparrow_orbLogged = true;
                SparrowLogger.debug("ExperienceOrbCullMixin: culling orbs > " + SPARROW_MAX_ORB_DIST + " blocks");
            }
            ci.cancel();
        }
    }
}
