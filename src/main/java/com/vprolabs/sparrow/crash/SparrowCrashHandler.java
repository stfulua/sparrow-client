package com.vprolabs.sparrow.crash;

import com.vprolabs.sparrow.logging.SparrowLogger;

import java.io.PrintWriter;
import java.io.StringWriter;

public final class SparrowCrashHandler {

    private static volatile boolean registered;

    private SparrowCrashHandler() {
    }

    public static void register() {
        if (registered) {
            return;
        }
        synchronized (SparrowCrashHandler.class) {
            if (registered) {
                return;
            }
            Thread.UncaughtExceptionHandler existing = Thread.getDefaultUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler((thread, throwable) -> {
                String msg;
                try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
                    throwable.printStackTrace(pw);
                    msg = "Unhandled exception in thread " + thread.getName() + ":\n" + sw;
                } catch (java.io.IOException e) {
                    msg = "Unhandled exception in thread " + thread.getName();
                }
                System.err.println(msg);
                SparrowLogger.error(msg);

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
}
