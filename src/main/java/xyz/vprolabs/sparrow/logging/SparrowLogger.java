/*
 * Sparrow Mod - Logger that writes to logs/sparrow-client.log with rotation.
 * Made By: vProLabs (https://www.vprolabs.xyz)
 * Discord: discord.gg/SNzUYWbc5Q
 * Donations: ko-fi.com/v4bi
 */

package xyz.vprolabs.sparrow.logging;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public final class SparrowLogger {
    private static final Logger LOGGER = Logger.getLogger("SparrowMod");
    private static final String LOG_DIR = "logs";
    private static final String LOG_FILE = "sparrow-client.log";
    private static final int LOG_LIMIT = 5 * 1024 * 1024;
    private static final int LOG_COUNT = 3;
    private static volatile FileHandler fileHandler;
    private static volatile ConsoleHandler consoleHandler;
    private static boolean shutdownHookRegistered;
    private static final Set<String> LOADED_MIXINS = ConcurrentHashMap.newKeySet();

    private SparrowLogger() {
    }

    public static synchronized void init() {
        if (LOGGER.getHandlers().length > 0) return;
        LOGGER.setUseParentHandlers(false);
        LOGGER.setLevel(Level.ALL);

        consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(Level.INFO);
        consoleHandler.setFormatter(new Formatter() {
            @Override
            public String format(LogRecord record) {
                return String.format("[%s] %s: %s%n",
                        record.getLevel().getName(),
                        record.getLoggerName(),
                        record.getMessage());
            }
        });
        LOGGER.addHandler(consoleHandler);

        try {
            File dir = new File(LOG_DIR);
            if (!dir.exists() && !dir.mkdirs()) {
                LOGGER.warning("SparrowLogger: could not create " + dir.getAbsolutePath() + " -- file logging disabled");
            } else {
                fileHandler = new FileHandler(LOG_DIR + File.separator + LOG_FILE, LOG_LIMIT, LOG_COUNT, true);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(new Formatter() {
                    @Override
                    public String format(LogRecord record) {
                        return String.format("%tF %<tT [%s] %s: %s%n",
                                record.getMillis(),
                                record.getLevel().getName(),
                                record.getLoggerName(),
                                record.getMessage());
                    }
                });
                LOGGER.addHandler(fileHandler);
            }
        } catch (IOException e) {
            LOGGER.warning("SparrowLogger: file logging unavailable (" + e.getMessage() + ") -- console only");
        }

        if (!shutdownHookRegistered) {
            Runtime.getRuntime().addShutdownHook(new Thread(SparrowLogger::close, "SparrowLogger-Shutdown"));
            shutdownHookRegistered = true;
        }
    }

    public static void debug(String msg) {
        LOGGER.fine(msg);
    }

    public static void info(String msg) {
        LOGGER.info(msg);
    }

    public static void warn(String msg) {
        LOGGER.warning(msg);
    }

    public static void error(String msg) {
        LOGGER.severe(msg);
    }

    public static void log(String type, String text) {
        String t = (type == null || type.isEmpty()) ? "INFO" : type;
        String m = (text == null) ? "" : text;
        LOGGER.info(String.format("[SPARROW] %s %s", t, m));
    }

    public static void mixinLoaded(String mixinId) {
        if (mixinId == null || mixinId.isEmpty()) {
            return;
        }
        if (LOADED_MIXINS.add(mixinId)) {
            log("MIXIN", "loaded " + mixinId);
        }
    }

    public static Set<String> getLoadedMixins() {
        return Collections.unmodifiableSet(LOADED_MIXINS);
    }

    public static int getLoadedMixinCount() {
        return LOADED_MIXINS.size();
    }

    public static void resetLoadedMixins() {
        LOADED_MIXINS.clear();
    }

    public static synchronized void close() {
        if (fileHandler != null) {
            try {
                fileHandler.flush();
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            try {
                fileHandler.close();
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            try {
                LOGGER.removeHandler(fileHandler);
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            fileHandler = null;
        }
        if (consoleHandler != null) {
            try {
                consoleHandler.flush();
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            try {
                consoleHandler.close();
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            try {
                LOGGER.removeHandler(consoleHandler);
            } catch (Exception ignored) {
                // intentional — logger shutdown, don't crash JVM
            }
            consoleHandler = null;
        }
    }
}
