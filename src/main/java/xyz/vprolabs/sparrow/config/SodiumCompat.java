package xyz.vprolabs.sparrow.config;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.fabricmc.loader.api.FabricLoader;

public final class SodiumCompat {
    private static volatile boolean loaded;
    private static volatile boolean initialized;

    private SodiumCompat() {
    }

    public static void init() {
        if (initialized) return;
        loaded = FabricLoader.getInstance().isModLoaded("sodium");
        if (loaded) {
            SparrowLogger.info("Sodium detected -- disabling redundant Sparrow features");
        }
        initialized = true;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    public static boolean isInitialized() {
        return initialized;
    }

    public static boolean isSodiumLoaded() {
        return loaded;
    }
}
