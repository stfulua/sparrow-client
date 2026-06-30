/*
 * Sparrow Mod - Mixin config plugin that tracks which mixins have been applied.
 * Made By: vProLabs (https://www.vprolabs.xyz)
 * Discord: discord.gg/SNzUYWbc5Q
 * Donations: ko-fi.com/v4bi
 */

package xyz.vprolabs.sparrow.plugin;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;
import xyz.vprolabs.sparrow.logging.SparrowLogger;

public class SparrowMixinPlugin implements IMixinConfigPlugin {
    private static final Set<String> EXPECTED_MIXINS = ConcurrentHashMap.newKeySet();
    private static final Set<String> APPLIED_MIXINS = ConcurrentHashMap.newKeySet();
    private static volatile String configPackage;
    private static volatile boolean initialized;

    public static int getExpectedCount() {
        return EXPECTED_MIXINS.size();
    }

    public static int getAppliedCount() {
        return APPLIED_MIXINS.size();
    }

    public static int getPendingCount() {
        int pending = EXPECTED_MIXINS.size() - APPLIED_MIXINS.size();
        return Math.max(0, pending);
    }

    public static Set<String> getExpectedMixins() {
        return Collections.unmodifiableSet(EXPECTED_MIXINS);
    }

    public static Set<String> getAppliedMixins() {
        return Collections.unmodifiableSet(APPLIED_MIXINS);
    }

    public static Set<String> getPendingMixins() {
        Set<String> pending = new HashSet<>(EXPECTED_MIXINS);
        pending.removeAll(APPLIED_MIXINS);
        return Collections.unmodifiableSet(pending);
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static void reset() {
        EXPECTED_MIXINS.clear();
        APPLIED_MIXINS.clear();
        configPackage = null;
        initialized = false;
    }

    @Override
    public void onLoad(String mixinPackage) {
        configPackage = mixinPackage;
        EXPECTED_MIXINS.clear();
        APPLIED_MIXINS.clear();
        loadExpectedMixinsFromConfig(mixinPackage);
        initialized = true;
        SparrowLogger.log("MIXIN_PLUGIN",
            "onLoad: package=" + mixinPackage + ", expected=" + EXPECTED_MIXINS.size());
    }

    private void loadExpectedMixinsFromConfig(String mixinPackage) {
        ClassLoader cl = SparrowMixinPlugin.class.getClassLoader();
        if (cl == null) {
            cl = ClassLoader.getSystemClassLoader();
        }
        try {
            Enumeration<URL> resources = cl.getResources("sparrow.mixins.json");
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                try (InputStream is = url.openStream();
                     InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8)) {
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();
                    String pkg = (json.has("package") && !json.get("package").isJsonNull())
                        ? json.get("package").getAsString() : null;
                    if (pkg != null && pkg.equals(mixinPackage) && json.has("client")) {
                        JsonArray arr = json.getAsJsonArray("client");
                        for (JsonElement el : arr) {
                            if (el != null && el.isJsonPrimitive()) {
                                EXPECTED_MIXINS.add(el.getAsString());
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            SparrowLogger.log("MIXIN_PLUGIN", "Failed to parse mixin config: " + e.getMessage());
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        String pkg = configPackage;
        String shortName;
        if (pkg != null && mixinClassName != null && mixinClassName.startsWith(pkg + ".")) {
            shortName = mixinClassName.substring(pkg.length() + 1);
            APPLIED_MIXINS.add(shortName);
        } else if (mixinClassName != null) {
            shortName = mixinClassName;
            APPLIED_MIXINS.add(shortName);
        } else {
            shortName = null;
        }
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
    }
}
