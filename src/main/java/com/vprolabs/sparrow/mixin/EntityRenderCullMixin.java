package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.Frustum;
import net.minecraft.client.render.entity.EntityRenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderManager.class)
public class EntityRenderCullMixin {

    @Unique private double sparrow_playerX = 0.0;
    @Unique private double sparrow_playerY = 0.0;
    @Unique private double sparrow_playerZ = 0.0;
    @Unique private double sparrow_decorativeCount = 0.0;
    @Unique private long sparrow_lastFrameNs = 0L;
    @Unique private boolean sparrow_cullDistLogged = false;
    @Unique private boolean sparrow_typeLimiterLogged = false;

    @Unique private static final int SPARROW_MAX_DECORATIVE = 10;
    @Unique private static final long SPARROW_FRAME_INTERVAL_NS = 1_000_000L;

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    private <E extends Entity> void cullAndLimit(E entity, Frustum frustum, double x, double y, double z, CallbackInfoReturnable<Boolean> cir) {
        long now = System.nanoTime();
        if (now - sparrow_lastFrameNs > SPARROW_FRAME_INTERVAL_NS) {
            sparrow_playerX = x;
            sparrow_playerY = y;
            sparrow_playerZ = z;
            sparrow_decorativeCount = 0.0;
            sparrow_lastFrameNs = now;
        }

        double dx = entity.getX() - sparrow_playerX;
        double dy = entity.getY() - sparrow_playerY;
        double dz = entity.getZ() - sparrow_playerZ;
        double distSq = dx * dx + dy * dy + dz * dz;

        float itemDist = ConfigCache.itemCullingDistance;
        float entityDist = ConfigCache.entityCullingDistance;
        double itemDistSq = (double) itemDist * itemDist;
        double entityDistSq = (double) entityDist * entityDist;

        if (entity instanceof ItemEntity) {
            if (distSq > itemDistSq) {
                cir.setReturnValue(false);
                return;
            }
        } else if (distSq > entityDistSq) {
            if (!sparrow_cullDistLogged) {
                sparrow_cullDistLogged = true;
                SparrowLogger.debug("EntityRenderCullMixin: culling entities past " + entityDist + " blocks");
            }
            cir.setReturnValue(false);
            return;
        }

        EntityType<?> type = entity.getType();
        if (type != EntityType.ARMOR_STAND && type != EntityType.ITEM_FRAME &&
            type != EntityType.GLOW_ITEM_FRAME && type != EntityType.PAINTING &&
            type != EntityType.LEASH_KNOT && type != EntityType.FALLING_BLOCK) {
            return;
        }

        sparrow_decorativeCount += 1.0;
        if (sparrow_decorativeCount > SPARROW_MAX_DECORATIVE) {
            if (!sparrow_typeLimiterLogged) {
                sparrow_typeLimiterLogged = true;
                SparrowLogger.debug("EntityRenderCullMixin: throttling decorative entities (> " + SPARROW_MAX_DECORATIVE + "/frame)");
            }
            cir.setReturnValue(false);
        }
    }
}
