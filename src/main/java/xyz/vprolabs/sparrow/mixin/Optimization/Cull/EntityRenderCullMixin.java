package xyz.vprolabs.sparrow.mixin.Optimization.Cull;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import xyz.vprolabs.sparrow.state.EntityAggregationState;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Cull-and-aggregate decision hook on {@link EntityRenderManager#shouldRender}.
 *
 * Per-frame bucket rep / aggregate state is precomputed by
 * {@link EntityCullPrepassMixin} (HEAD of fillEntityRenderStates) — this mixin
 * just looks up the result and culls or allows the entity.
 *
 * Culling rules:
 *   - ItemEntity: culled if id in culledItemIds (distance), or if id != rep for
 *     its bucket (another entity represents the cluster). Non-culled item is
 *     the rep and has aggregatedItemCounts[repId] populated for the redirect mixin.
 *   - ExperienceOrbEntity: same pattern with orb maps.
 *   - Other entities: culled if id in culledEntityIds (distance). Plus the
 *     decorative throttling (armor stand / item frame / painting / etc.) — limit
 *     to SPARROW_MAX_DECORATIVE per frame.
 */
@Mixin(EntityRenderManager.class)
public class EntityRenderCullMixin {

    @Unique private static final int SPARROW_MAX_DECORATIVE = 10;

    @Unique private static boolean sparrow_typeLimiterLogged = false;

    @Unique
    private static long sparrow_bucketKey(int x, int z, int typeHash) {
        int bx = x >> 3;
        int bz = z >> 3;
        return ((long) bx << 32) | (bz & 0xFFFFFFFFL) ^ ((long) typeHash << 16);
    }

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void sparrow_cullAndLimit(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        int id = entity.getId();

        if (entity instanceof ItemEntity item) {
            if (EntityAggregationState.culledItemIds.contains(id)) {
                cir.setReturnValue(false);
                return;
            }
            int itemHash = item.getStack().getItem().hashCode();
            long key = sparrow_bucketKey(entity.getBlockX(), entity.getBlockZ(), itemHash);
            Integer repId = EntityAggregationState.itemBucketRepIds.get(key);
            if (repId == null || repId != id) {
                cir.setReturnValue(false);
                return;
            }
            return;
        }

        if (entity instanceof ExperienceOrbEntity orb) {
            if (EntityAggregationState.culledOrbIds.contains(id)) {
                cir.setReturnValue(false);
                return;
            }
            long key = sparrow_bucketKey(entity.getBlockX(), entity.getBlockZ(), 0);
            Integer repId = EntityAggregationState.orbBucketRepIds.get(key);
            if (repId == null || repId != id) {
                cir.setReturnValue(false);
                return;
            }
            return;
        }

        // Non-item / non-orb: general distance cull + decorative throttling.
        if (EntityAggregationState.culledEntityIds.contains(id)) {
            cir.setReturnValue(false);
            return;
        }

        EntityType<?> type = entity.getType();
        if (type == EntityType.ARMOR_STAND || type == EntityType.ITEM_FRAME ||
            type == EntityType.GLOW_ITEM_FRAME || type == EntityType.PAINTING ||
            type == EntityType.LEASH_KNOT || type == EntityType.FALLING_BLOCK) {

            EntityAggregationState.decorativeCount++;
            if (EntityAggregationState.decorativeCount > SPARROW_MAX_DECORATIVE) {
                if (!sparrow_typeLimiterLogged) {
                    sparrow_typeLimiterLogged = true;
                    SparrowLogger.debug("EntityRenderCullMixin: throttling decorative entities (> " + SPARROW_MAX_DECORATIVE + "/frame)");
                }
                cir.setReturnValue(false);
            }
        }
    }
}
