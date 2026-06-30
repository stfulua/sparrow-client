package xyz.vprolabs.sparrow.crash;

import xyz.vprolabs.sparrow.logging.SparrowLogger;

import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public final class SparrowCrashHandler {

    private static volatile boolean registered;

    private SparrowCrashHandler() {
    }

    public static void register() {
        if (registered) return;
        synchronized (SparrowCrashHandler.class) {
            if (registered) return;

            Thread.UncaughtExceptionHandler existing = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                String fullReport = buildCrashReport(thread, throwable);

                // Log to logger
                SparrowLogger.error(fullReport);

                // Write crash dump file
                writeCrashDump(fullReport);

                // Also print to stderr
                System.err.println(fullReport);

                // Chain to existing handler
                if (existing != null) {
                    try {
                        existing.uncaughtException(thread, throwable);
                    } catch (Throwable delegateFailure) {
                        SparrowLogger.error("Previous uncaught exception handler threw: " + delegateFailure);
                    }
                }
            });
            registered = true;
        }
    }

    private static String buildCrashReport(Thread thread, Throwable throwable) {
        StringBuilder sb = new StringBuilder(32768);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        sb.append("===== SPARROW UNHANDLED EXCEPTION =====").append('\n');
        sb.append("Time: ").append(sdf.format(new Date())).append('\n');
        sb.append("Thread: ").append(thread.getName()).append(" (id=").append(thread.threadId()).append(", priority=").append(thread.getPriority()).append(')').append('\n');
        sb.append("Thread group: ").append(
            thread.getThreadGroup() != null ? thread.getThreadGroup().getName() : "null"
        ).append('\n');
        sb.append('\n');

        // ── Exception chain (causes + suppressed) ──
        sb.append("-- Exception Chain --").append('\n');
        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            throwable.printStackTrace(pw);
            pw.flush();
            sb.append(sw);
        } catch (Exception ignored) {
            sb.append("(failed to render stack trace)\n");
        }
        sb.append('\n');

        // ── Suppressed exceptions (extra detail) ──
        Throwable[] suppressed = throwable.getSuppressed();
        if (suppressed.length > 0) {
            sb.append("-- Suppressed (").append(suppressed.length).append(") --").append('\n');
            for (int i = 0; i < suppressed.length; i++) {
                sb.append("  [").append(i).append("] ");
                try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                    suppressed[i].printStackTrace(pw);
                    pw.flush();
                    sb.append(sw.toString().replace("\n", "\n    "));
                } catch (Exception ignored) {
                    sb.append(suppressed[i].getMessage()).append('\n');
                }
            }
            sb.append('\n');
        }

        // ── All live threads ──
        sb.append("-- All Threads --").append('\n');
        Map<Thread, StackTraceElement[]> allStacks = Thread.getAllStackTraces();
        for (Map.Entry<Thread, StackTraceElement[]> entry : allStacks.entrySet()) {
            Thread t = entry.getKey();
            sb.append("  \"").append(t.getName()).append("\" (id=").append(t.threadId())
              .append(", ").append(t.getState()).append(')').append('\n');
            StackTraceElement[] stack = entry.getValue();
            for (int i = 0; i < Math.min(stack.length, 8); i++) {
                sb.append("    at ").append(stack[i]).append('\n');
            }
            if (stack.length > 8) {
                sb.append("    ... (").append(stack.length - 8).append(" more)\n");
            }
        }
        sb.append('\n');

        // ── System info ──
        sb.append("-- System --").append('\n');
        sb.append("  Java: ").append(System.getProperty("java.version", "?")).append(" (").append(System.getProperty("java.vendor", "?")).append(")\n");
        sb.append("  OS: ").append(System.getProperty("os.name", "?")).append(' ').append(System.getProperty("os.version", "?")).append('\n');
        sb.append("  Arch: ").append(System.getProperty("os.arch", "?")).append('\n');
        sb.append("  Processors: ").append(Runtime.getRuntime().availableProcessors()).append('\n');
        sb.append("  Max memory: ").append(Runtime.getRuntime().maxMemory() / 1048576).append(" MB\n");
        sb.append("  User dir: ").append(System.getProperty("user.dir", "?")).append('\n');

        sb.append("===== END SPARROW CRASH REPORT =====");
        return sb.toString();
    }

    private static void writeCrashDump(String report) {
        try {
            File dir = new File(System.getProperty("user.dir", "."), "crash-reports");
            dir.mkdirs();
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date());
            File dump = new File(dir, "sparrow-crash-" + timestamp + ".txt");
            Files.writeString(dump.toPath(), report, StandardCharsets.UTF_8);
            SparrowLogger.info("Crash dump written to: " + dump.getAbsolutePath());
        } catch (Exception e) {
            SparrowLogger.error("Failed to write crash dump: " + e.getMessage());
        }
    }
}
