package xyz.vprolabs.sparrow.mixin.broken;

import xyz.vprolabs.sparrow.state.EntityAggregationState;
import net.minecraft.client.render.entity.ExperienceOrbEntityRenderer;
import net.minecraft.entity.ExperienceOrbEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Substitutes the bucket's aggregated XP value (mapped to the appropriate orb
 * size) for the rep orb's size in
 * {@link ExperienceOrbEntityRenderer#updateRenderState}, so a single visible orb
 * displays the total XP of its cluster.
 *
 * Rep identity and aggregate value are precomputed by EntityCullPrepassMixin.
 */
@Mixin(ExperienceOrbEntityRenderer.class)
public class ExperienceOrbRenderAggregationMixin {

    @Redirect(
        method = "updateRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ExperienceOrbEntity;getOrbSize()I"
        )
    )
    private int sparrow_getAggregatedOrbSize(ExperienceOrbEntity orb) {
        int total = EntityAggregationState.aggregatedOrbAmounts.getOrDefault(orb.getId(), 0);
        if (total > 0 && total != orb.getValue()) {
            return EntityAggregationState.orbSizeForValue(total);
        }
        return orb.getOrbSize();
    }
}
