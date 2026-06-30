package xyz.vprolabs.sparrow.mixin.Bugfix;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(GameRenderer.class)
public class SprintFovSmoothingMixin {

    @Unique
    private float sparrow_lastFov = -1.0f;

    @Unique
    private static final float SPARROW_EMA_FACTOR = 0.25f;

    @Unique
    private static final float SPARROW_THRESHOLD = 3.0f;

    @ModifyVariable(method = "getFov", at = @At("RETURN"), ordinal = 0)
    private float sparrow_smoothFov(float fov) {
            if (fov < 0) return fov;
            if (sparrow_lastFov < 0) {
                sparrow_lastFov = fov;
                return fov;
            }

            float diff = Math.abs(fov - sparrow_lastFov);
            if (diff > SPARROW_THRESHOLD) {
                float smoothed = sparrow_lastFov + (fov - sparrow_lastFov) * SPARROW_EMA_FACTOR;
                sparrow_lastFov = smoothed;
                return smoothed;
            }

            sparrow_lastFov = fov;
            return fov;
    }
}
