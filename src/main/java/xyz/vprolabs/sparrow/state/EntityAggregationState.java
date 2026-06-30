package xyz.vprolabs.sparrow.state;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Non-mixin holder for aggregation state — accessed by aggregation render mixins.
 * Must NOT be in the mixin package because Fabric Loader tries to mixin-transform
 * all classes in that package, causing "Mixin transformation of ... failed" errors.
 *
 * Populated by EntityCullPrepassMixin (HEAD of WorldRenderer.fillEntityRenderStates),
 * read by EntityRenderCullMixin (EntityRenderManager.shouldRender) for cull decisions,
 * and read by ItemEntityRenderAggregationMixin / ExperienceOrbRenderAggregationMixin
 * (Renderer.updateRenderState) to display the aggregated count on the representative
 * entity's render state.
 *
 * The prepass builds the maps/sets deterministically per frame:
 *   - For each ItemEntity / ExperienceOrbEntity in render range, compute a bucket key.
 *   - The representative (rep) per bucket is the entity with the lowest id — stable
 *     across frames as long as the same set of entities is in the bucket. This is
 *     the fix for the rep-instability visual flicker (broken mixin #2 in the
 *     2026-06-21 lessons learned).
 *   - The rep's id keys aggregatedItemCounts / aggregatedOrbAmounts with the bucket
 *     total. The redirect mixin then displays that count on the rep.
 *   - culledItemIds / culledOrbIds / culledEntityIds hold distance-culled entity ids
 *     for fast lookup in shouldRender.
 */
public class EntityAggregationState {
    public static final Map<Integer, Integer> aggregatedItemCounts = new HashMap<>();
    public static final Map<Integer, Integer> aggregatedOrbAmounts = new HashMap<>();

    /** Bucket key -> rep entity id (min id in the bucket, stable across frames). */
    public static final Map<Long, Integer> itemBucketRepIds = new HashMap<>();
    public static final Map<Long, Integer> orbBucketRepIds = new HashMap<>();

    /** Entities outside render range (populated by prepass, checked in shouldRender). */
    public static final Set<Integer> culledItemIds = new HashSet<>();
    public static final Set<Integer> culledOrbIds = new HashSet<>();
    public static final Set<Integer> culledEntityIds = new HashSet<>();

    /** Decorative entity counter for per-frame throttling. Reset by prepass. */
    public static int decorativeCount = 0;

    // Replicates ExperienceOrbEntity.getOrbSize(int) -- maps XP value to visual orb size.
    // Used by ExperienceOrbRenderAggregationMixin when merging multiple orbs.
    public static int orbSizeForValue(int value) {
        if (value >= 2477) return 10;
        if (value >= 1237) return 9;
        if (value >= 617)  return 8;
        if (value >= 307)  return 7;
        if (value >= 149)  return 6;
        if (value >= 73)   return 5;
        if (value >= 37)   return 4;
        if (value >= 17)   return 3;
        if (value >= 7)    return 2;
        if (value >= 3)    return 1;
        return 0;
    }

    /** Reset all per-frame state. Called at HEAD of fillEntityRenderStates. */
    public static void clearPerFrame() {
        aggregatedItemCounts.clear();
        aggregatedOrbAmounts.clear();
        itemBucketRepIds.clear();
        orbBucketRepIds.clear();
        culledItemIds.clear();
        culledOrbIds.clear();
        culledEntityIds.clear();
        decorativeCount = 0;
    }

    private EntityAggregationState() {}
}
