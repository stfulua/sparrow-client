package xyz.vprolabs.sparrow.state;

import xyz.vprolabs.sparrow.BuildInfo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;

public class VersionCheck {
    private static final String VERSION_URL =
        "https://raw.githubusercontent.com/stfulua/sparrow-client/main/version.txt";

    private static boolean checked;

    public static void checkOnce() {
        if (checked) return;
        checked = true;

        new Thread(() -> {
            try {
                HttpURLConnection conn = (HttpURLConnection) URI.create(VERSION_URL).toURL().openConnection();
                conn.setConnectTimeout(5000);
                conn.setReadTimeout(5000);
                if (conn.getResponseCode() != 200) return;

                String latest;
                try (BufferedReader r = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
                    latest = r.readLine();
                }
                if (latest == null || latest.isBlank()) return;
                final String fLatest = latest.trim();

                String current = BuildInfo.BUILD_TAG.trim();
                if (fLatest.equals(current)) return;

                MinecraftClient client = MinecraftClient.getInstance();
                client.execute(() -> {
                    if (client.player == null) return;
                    client.player.sendMessage(Text.literal(
                        "§7[Sparrow] §eNew version available: §f" + fLatest +
                        " §7→ §7[§fhttps://github.com/stfulua/sparrow-client/releases§7]"
                    ), false);
                });
            } catch (Exception ignored) {
            }
        }, "Sparrow-VersionCheck").start();
    }
}
