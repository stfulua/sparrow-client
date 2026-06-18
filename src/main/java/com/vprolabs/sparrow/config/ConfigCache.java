package com.vprolabs.sparrow.config;

import com.vprolabs.sparrow.tweaks.SparrowZoomState;
import java.util.HashMap;
import java.util.Map;

public class ConfigCache {
    public static volatile boolean smoothElytra, disableMouseWheel, fastPlace, smallTotem, oldPotions, customGlint, fireTimer, betterMovement, removeShadows, disableEntityAI, fullbright, noMiscOverlays, noMiningFatigue, alwaysDay;
    public static volatile float viewModelX = 0.0f, viewModelY = 0.0f, viewModelZ = 0.0f, viewModelSize = 1.0f, utilityScale = 0.65f, zoomLevel = 4.0f, zoomSmoothness = 8.0f, zoomMin = 1.0f, zoomMax = 15.0f, itemCullingDistance = 40.0f, entityCullingDistance = 128.0f;
    public static volatile int glintR = 0, glintG = 255, glintB = 0, lodDistance = 0, netherRenderCap = 6;
    public static volatile String chunkLoadingPreset = "Fast", fireTimerPos = "BOTTOM_CENTER", particleMode = "off";
    public static volatile boolean storageTooltip = true;

    public static void load(ConfigReader config) {
        if (config == null) return;
        synchronized (ConfigCache.class) {
            smoothElytra = config.isSmoothElytra();
            disableMouseWheel = config.isDisableMouseWheel();
            fastPlace = config.isFastPlace();
            smallTotem = config.isSmallTotem();
            utilityScale = config.getUtilityScale();
            zoomLevel = config.getZoomLevel();
            zoomSmoothness = config.getZoomSmoothness();
            zoomMin = config.getZoomMin();
            zoomMax = config.getZoomMax();
            SparrowZoomState.targetZoom = zoomLevel;
            SparrowZoomState.currentZoom = 1.0;
            viewModelX = config.getViewModelX();
            viewModelY = config.getViewModelY();
            viewModelZ = config.getViewModelZ();
            viewModelSize = config.getViewModelSize();
            glintR = config.getGlintR();
            glintG = config.getGlintG();
            glintB = config.getGlintB();
            customGlint = config.isCustomGlint();
            chunkLoadingPreset = config.getChunkLoadingPreset();
            oldPotions = config.isOldPotions();
            fireTimer = config.isFireTimer();
            fireTimerPos = config.getFireTimerPos();
            particleMode = config.getParticleMode();
            storageTooltip = config.isStorageTooltip();
            betterMovement = config.isBetterMovement();
            removeShadows = config.isRemoveShadows();
            disableEntityAI = config.isDisableEntityAI();
            itemCullingDistance = config.getItemCullingDistance();
            entityCullingDistance = config.getEntityCullingDistance();
            lodDistance = config.getLodDistance();
            fullbright = config.isFullbright();
            noMiscOverlays = config.isNoMiscOverlays();
            netherRenderCap = config.getNetherRenderCap();
            noMiningFatigue = config.isNoMiningFatigue();
            alwaysDay = config.isAlwaysDay();
        }
    }

    public static Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("version", "1");
        map.put("smooth_elytra", smoothElytra);
        map.put("disable_mouse_wheel", disableMouseWheel);
        map.put("fast_place", fastPlace);
        map.put("small_totem", smallTotem);
        map.put("utility_scale", utilityScale);
        map.put("zoom_level", zoomLevel);
        map.put("zoom_smoothness", zoomSmoothness);
        map.put("zoom_min", zoomMin);
        map.put("zoom_max", zoomMax);
        map.put("view_model_x", viewModelX);
        map.put("view_model_y", viewModelY);
        map.put("view_model_z", viewModelZ);
        map.put("view_model_size", viewModelSize);
        map.put("glint_r", glintR);
        map.put("glint_g", glintG);
        map.put("glint_b", glintB);
        map.put("chunk_loading_preset", chunkLoadingPreset);
        map.put("old_potions", oldPotions);
        map.put("custom_glint", customGlint);
        map.put("fire_timer", fireTimer);
        map.put("fire_timer_pos", fireTimerPos);
        map.put("particle_mode", particleMode);
        map.put("storage_tooltip", storageTooltip);
        map.put("better_movement", betterMovement);
        map.put("remove_shadows", removeShadows);
        map.put("disable_entity_ai", disableEntityAI);
        map.put("item_culling_distance", itemCullingDistance);
        map.put("entity_culling_distance", entityCullingDistance);
        map.put("lod_distance", lodDistance);
        map.put("fullbright", fullbright);
        map.put("no_misc_overlays", noMiscOverlays);
        map.put("nether_render_cap", netherRenderCap);
        map.put("always_day", alwaysDay);
        return map;
    }
}
