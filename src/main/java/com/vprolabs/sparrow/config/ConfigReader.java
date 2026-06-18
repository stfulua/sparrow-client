package com.vprolabs.sparrow.config;

import com.vprolabs.sparrow.logging.SparrowLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.HashMap;

public class ConfigReader {
    private static final String FILE_NAME = "config.json";
    private static volatile ConfigReader INSTANCE;

    private final boolean smoothElytra, disableMouseWheel, fastPlace, smallTotem, oldPotions, customGlint, fireTimer, betterMovement, removeShadows, disableEntityAI, fullbright, noMiscOverlays, noMiningFatigue, alwaysDay;
    private final float viewModelX, viewModelY, viewModelZ, viewModelSize, utilityScale, zoomLevel, zoomSmoothness, zoomMin, zoomMax, itemCullingDistance, entityCullingDistance;
    private final int glintR, glintG, glintB, lodDistance, netherRenderCap;
    private final String chunkLoadingPreset, fireTimerPos, particleMode;
    private final boolean storageTooltip;
    private final boolean allDefaults;

    private ConfigReader(Map<String, Object> map, boolean allDefaults) {
        this.smoothElytra = getBool(map, "smooth_elytra", false);
        this.disableMouseWheel = getBool(map, "disable_mouse_wheel", false);
        this.fastPlace = getBool(map, "fast_place", false);
        this.smallTotem = getBool(map, "small_totem", true);
        this.oldPotions = getBool(map, "old_potions", true);
        this.customGlint = getBool(map, "custom_glint", true);
        this.fireTimer = getBool(map, "fire_timer", true);
        this.utilityScale = getFloat(map, "utility_scale", 0.65f);
        this.zoomLevel = getFloat(map, "zoom_level", 4.0f);
        this.zoomSmoothness = getFloat(map, "zoom_smoothness", 8.0f);
        this.zoomMin = getFloat(map, "zoom_min", 1.0f);
        this.zoomMax = getFloat(map, "zoom_max", 15.0f);
        this.viewModelX = getFloat(map, "view_model_x", 0.0f);
        this.viewModelY = getFloat(map, "view_model_y", 0.0f);
        this.viewModelZ = getFloat(map, "view_model_z", 0.0f);
        this.viewModelSize = getFloat(map, "view_model_size", 1.0f);
        this.glintR = getColorInt(map, "glint_r", 0);
        this.glintG = getColorInt(map, "glint_g", 255);
        this.glintB = getColorInt(map, "glint_b", 0);
        this.chunkLoadingPreset = getString(map, "chunk_loading_preset", "Fast");
        this.fireTimerPos = getString(map, "fire_timer_pos", "BOTTOM_CENTER");
        this.particleMode = getString(map, "particle_mode", "off");
        this.storageTooltip = getBool(map, "storage_tooltip", true);
        this.betterMovement = getBool(map, "better_movement", false);
        this.removeShadows = getBool(map, "remove_shadows", true);
        this.disableEntityAI = getBool(map, "disable_entity_ai", false);
        this.itemCullingDistance = getFloat(map, "item_culling_distance", 40.0f);
        this.entityCullingDistance = getFloat(map, "entity_culling_distance", 128.0f);
        this.lodDistance = getInt(map, "lod_distance", 0);
        this.fullbright = getBool(map, "fullbright", true);
        this.noMiscOverlays = getBool(map, "no_misc_overlays", true);
        this.netherRenderCap = getInt(map, "nether_render_cap", 6);
        this.noMiningFatigue = getBool(map, "no_mining_fatigue", false);
        this.alwaysDay = getBool(map, "always_day", false);
        this.allDefaults = allDefaults;
    }

    public static ConfigReader getInstance() { return INSTANCE; }
    public boolean isAllDefaults() { return allDefaults; }

    public static ConfigReader load() {
        File sparrowDir = new File(System.getProperty("user.dir"));
        File cfgFile = new File(sparrowDir, FILE_NAME);
        SparrowLogger.info("Loading config from: " + cfgFile.getAbsolutePath());
        Map<String, Object> map;
        boolean missing = !cfgFile.exists();
        if (missing) {
            map = new HashMap<>();
        } else {
            try (FileReader reader = new FileReader(cfgFile, StandardCharsets.UTF_8)) {
                Type type = new TypeToken<Map<String, Object>>() {}.getType();
                map = new Gson().fromJson(reader, type);
                if (map == null) map = new HashMap<>();
            } catch (Exception e) {
                SparrowLogger.warn("Failed to parse config.json (" + e.getMessage() + ") — backing up and using defaults");
                try {
                    File bak = new File(cfgFile.getParentFile(), FILE_NAME + ".bak." + System.currentTimeMillis());
                    Files.move(cfgFile.toPath(), bak.toPath());
                    SparrowLogger.info("Backed up malformed config to: " + bak.getAbsolutePath());
                } catch (Exception moveEx) {
                    SparrowLogger.error("Failed to back up malformed config: " + moveEx.getMessage());
                }
                map = new HashMap<>();
            }
        }
        INSTANCE = new ConfigReader(map, missing);
        if (missing) INSTANCE.save();
        return INSTANCE;
    }

    public void save() {
        saveFromCache();
    }

    public static void saveFromCache() {
        File cfgFile = new File(System.getProperty("user.dir"), FILE_NAME);
        try (FileWriter writer = new FileWriter(cfgFile, StandardCharsets.UTF_8)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(ConfigCache.toMap(), writer);
            SparrowLogger.info("Config saved to: " + cfgFile.getAbsolutePath());
        } catch (Exception e) {
            SparrowLogger.error("Failed to save config from cache: " + e.getMessage());
        }
    }

    private static boolean getBool(Map<String, Object> map, String key, boolean defaultVal) {
        Object val = map.get(key);
        if (val instanceof Boolean) return (Boolean) val;
        return defaultVal;
    }
    private static float getFloat(Map<String, Object> map, String key, float defaultVal) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).floatValue();
        return defaultVal;
    }
    private static String getString(Map<String, Object> map, String key, String defaultVal) {
        Object val = map.get(key);
        if (val instanceof String) return (String) val;
        return defaultVal;
    }
    private static int getColorInt(Map<String, Object> map, String key, int defaultVal) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return defaultVal;
    }
    private static int getInt(Map<String, Object> map, String key, int defaultVal) {
        Object val = map.get(key);
        if (val instanceof Number) return ((Number) val).intValue();
        return defaultVal;
    }

    public boolean isSmoothElytra() { return smoothElytra; }
    public boolean isDisableMouseWheel() { return disableMouseWheel; }
    public boolean isFastPlace() { return fastPlace; }
    public boolean isSmallTotem() { return smallTotem; }
    public boolean isOldPotions() { return oldPotions; }
    public boolean isCustomGlint() { return customGlint; }
    public float getUtilityScale() { return utilityScale; }
    public float getZoomLevel() { return zoomLevel; }
    public float getZoomSmoothness() { return zoomSmoothness; }
    public float getZoomMin() { return zoomMin; }
    public float getZoomMax() { return zoomMax; }
    public float getViewModelX() { return viewModelX; }
    public float getViewModelY() { return viewModelY; }
    public float getViewModelZ() { return viewModelZ; }
    public float getViewModelSize() { return viewModelSize; }
    public int getGlintR() { return glintR; }
    public int getGlintG() { return glintG; }
    public int getGlintB() { return glintB; }
    public String getChunkLoadingPreset() { return chunkLoadingPreset; }
    public boolean isFireTimer() { return fireTimer; }
    public String getFireTimerPos() { return fireTimerPos; }
    public String getParticleMode() { return particleMode; }
    public boolean isStorageTooltip() { return storageTooltip; }
    public boolean isBetterMovement() { return betterMovement; }
    public boolean isRemoveShadows() { return removeShadows; }
    public boolean isDisableEntityAI() { return disableEntityAI; }
    public float getItemCullingDistance() { return itemCullingDistance; }
    public float getEntityCullingDistance() { return entityCullingDistance; }
    public int getLodDistance() { return lodDistance; }
    public boolean isFullbright() { return fullbright; }
    public boolean isNoMiscOverlays() { return noMiscOverlays; }
    public int getNetherRenderCap() { return netherRenderCap; }
    public boolean isNoMiningFatigue() { return noMiningFatigue; }
    public boolean isAlwaysDay() { return alwaysDay; }

    private static int clampInt(int v, int min, int max) {
        if (v < min) return min;
        if (v > max) return max;
        return v;
    }
}
