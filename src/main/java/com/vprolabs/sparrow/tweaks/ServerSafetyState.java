package com.vprolabs.sparrow.tweaks;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ServerInfo;
import java.util.Locale;
import java.util.Set;

public class ServerSafetyState {
    public static boolean fastPlaceWasForcedOff = false;
    public static boolean fastPlaceOriginalState = false;
    public static volatile String pendingConnectHost = null;

    private static final Set<String> RESTRICTED_HOSTNAMES = Set.of(
        "saf.baby",
        "horizonsmc.net",
        "horizonsmp.net"
    );

    private static final Set<String> RESTRICTED_SUFFIXES = Set.of(
        ".saf.baby",
        ".horizonsmc.net",
        ".horizonsmp.net"
    );

    public static boolean isOnRestrictedServer() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.isInSingleplayer()) return false;

        String host = currentHost();
        if (host == null) return false;

        if (RESTRICTED_HOSTNAMES.contains(host)) return true;
        for (String suffix : RESTRICTED_SUFFIXES) {
            if (host.endsWith(suffix)) return true;
        }
        return host.contains("saf.baby") || host.contains("horizons");
    }

    public static void rememberConnect(String address) {
        pendingConnectHost = stripPort(address);
    }

    public static void clearPending() {
        pendingConnectHost = null;
    }

    private static String currentHost() {
        ServerInfo server = MinecraftClient.getInstance().getCurrentServerEntry();
        if (server != null && server.address != null) {
            return stripPort(server.address);
        }
        return pendingConnectHost;
    }

    private static String stripPort(String address) {
        if (address == null) return null;
        String normalized = address.trim().toLowerCase(Locale.ROOT);
        if (normalized.startsWith("[") && normalized.contains("]")) {
            int close = normalized.indexOf(']');
            return normalized.substring(1, close);
        }
        int colon = normalized.lastIndexOf(':');
        if (colon > 0 && normalized.indexOf(':') == colon) {
            return normalized.substring(0, colon);
        }
        return normalized;
    }
}
