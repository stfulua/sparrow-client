package xyz.vprolabs.sparrow.config;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
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

/**
 * Minimal JSON config loader/saver. Delegates ALL feature values to ConfigRegister.
 * No duplicate field declarations, no getter-per-feature — ConfigRegister owns the data.
 */
public class ConfigReader {
    private static final String FILE_NAME = "config.json";
    private static volatile ConfigReader INSTANCE;
    private static final Gson GSON = new Gson();
    private static final Gson PRETTY_GSON = new GsonBuilder().setPrettyPrinting().create();

    private final boolean allDefaults;

    public static ConfigReader getInstance() { return INSTANCE; }
    public boolean isAllDefaults() { return allDefaults; }

    private ConfigReader(boolean allDefaults) {
        this.allDefaults = allDefaults;
    }

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
                map = GSON.fromJson(reader, type);
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
        ConfigRegister.loadFromMap(map);
        xyz.vprolabs.sparrow.state.HudPositions.loadFromMap(map);
        INSTANCE = new ConfigReader(missing);
        if (missing) INSTANCE.save();
        return INSTANCE;
    }

    public void save() {
        saveFromCache();
    }

    public static void saveFromCache() {
        File cfgFile = new File(System.getProperty("user.dir"), FILE_NAME);
        Map<String, Object> map = new HashMap<>();
        map.put("version", "1");
        ConfigRegister.putToMap(map);
        xyz.vprolabs.sparrow.state.HudPositions.putToMap(map);
        try (FileWriter writer = new FileWriter(cfgFile, StandardCharsets.UTF_8)) {
            PRETTY_GSON.toJson(map, writer);
            SparrowLogger.info("Config saved to: " + cfgFile.getAbsolutePath());
        } catch (Exception e) {
            SparrowLogger.error("Failed to save config: " + e.getMessage());
        }
    }
}
