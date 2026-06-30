package xyz.vprolabs.sparrow.mixin.broken;

import xyz.vprolabs.sparrow.state.EntityAggregationState;
import net.minecraft.client.render.entity.ItemEntityRenderer;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Substitutes the bucket's aggregated count for the rep entity's stack in
 * {@link ItemEntityRenderer#updateRenderState} so a single visible entity displays
 * the total item count of its cluster.
 *
 * Rep identity and aggregate count are precomputed by EntityCullPrepassMixin.
 * Only the rep entity is allowed through shouldRender for a given bucket, so this
 * redirect only runs for the rep — no flicker.
 */
@Mixin(ItemEntityRenderer.class)
public class ItemEntityRenderAggregationMixin {

    @Redirect(
        method = "updateRenderState",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/entity/ItemEntity;getStack()Lnet/minecraft/item/ItemStack;"
        )
    )
    private ItemStack sparrow_getAggregatedStack(ItemEntity entity) {
        ItemStack original = entity.getStack();
        int total = EntityAggregationState.aggregatedItemCounts.getOrDefault(entity.getId(), 0);
        if (total > original.getCount()) {
            return original.copyWithCount(total);
        }
        return original;
    }
}
