package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import com.vprolabs.sparrow.config.ConfigReader;
import com.vprolabs.sparrow.state.ToggleSneakState;
import com.vprolabs.sparrow.tweaks.ServerSafetyState;
import com.vprolabs.sparrow.tweaks.SparrowGlintLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Locale;

@Mixin(ClientPlayNetworkHandler.class)
public class ChatCommandMixin {

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo ci) {
        if (!message.startsWith("!sparrow")) return;
        ci.cancel();
        handleCommand(message.substring(8).trim().toLowerCase(Locale.ROOT));
    }

    @Unique
    private void msg(String s) {
        MinecraftClient.getInstance().inGameHud.getChatHud().addMessage(Text.literal("§8[§7S§8] §7" + s));
    }

    @Unique
    private void handleCommand(String args) {
        if (args.isEmpty() || args.equals("help")) {
            msg("§f!sparrow commands:");
            msg("§7zoom [level] §7| zoomsmooth [val]");
            msg("§7viewmodel x/y/z/size [val]");
            msg("§7particles off|minimal|on");
            msg("§7smooth elytra §8- toggle");
            msg("§7fast place §8- toggle");
            msg("§7smalltotem §8- toggle");
            msg("§7oldpotions §8- toggle");
            msg("§7customglint r/g/b [0-255] §8| toggle");
            msg("§7utilityscale [val]");
            msg("§7bettermovement §8- toggle (snap-tap, ban risk)");
            msg("§7togglesneak §8- toggle");
            msg("§7nominingfatigue §8- toggle (ghost block risk)");
            return;
        }

        String[] parts = args.split("\\s+");

        switch (parts[0]) {
            case "smooth", "elytra" -> {
                ConfigCache.smoothElytra = !ConfigCache.smoothElytra;
                msg("smooth elytra §8" + (ConfigCache.smoothElytra ? "§aON" : "§cOFF"));
                save();
            }
            case "fast", "place" -> {
                boolean enabling = !ConfigCache.fastPlace;
                if (enabling && ServerSafetyState.isOnRestrictedServer()) {
                    msg("§cYou can't do that, buddy.");
                    return;
                }
                ConfigCache.fastPlace = enabling;
                msg("fast place §8" + (ConfigCache.fastPlace ? "§aON" : "§cOFF"));
                if (ConfigCache.fastPlace) {
                    msg("§cYou may get banned on multiplayer servers!");
                }
                save();
            }
            case "zoom" -> {
                if (parts.length < 2) {
                    msg("zoom §8= §f" + ConfigCache.zoomLevel + "x §8(smooth: §f" + ConfigCache.zoomSmoothness + "§8)");
                    return;
                }
                float val;
                try { val = Float.parseFloat(parts[1]); } catch (NumberFormatException e) { msg("§cinvalid number"); return; }
                if (val < 1.0f) val = 1.0f;
                ConfigCache.zoomLevel = val;
                msg("zoom §8= §f" + val + "x");
                save();
            }
            case "zoomsmooth" -> {
                if (parts.length < 2) {
                    msg("zoom smoothness §8= §f" + ConfigCache.zoomSmoothness);
                    return;
                }
                float val;
                try { val = Float.parseFloat(parts[1]); } catch (NumberFormatException e) { msg("§cinvalid number"); return; }
                if (val < 1.0f) val = 1.0f;
                ConfigCache.zoomSmoothness = val;
                msg("zoom smoothness §8= §f" + val);
                save();
            }
            case "viewmodel", "vm" -> {
                if (parts.length < 3) {
                    msg("§cusage: !sparrow viewmodel x/y/z/size <value>");
                    return;
                }
                float val;
                try {
                    val = Float.parseFloat(parts[2]);
                } catch (NumberFormatException e) {
                    msg("§cinvalid number");
                    return;
                }
                switch (parts[1]) {
                    case "x" -> { ConfigCache.viewModelX = val; msg("view x §8= §f" + val); }
                    case "y" -> { ConfigCache.viewModelY = val; msg("view y §8= §f" + val); }
                    case "z" -> { ConfigCache.viewModelZ = val; msg("view z §8= §f" + val); }
                    case "size" -> {
                        if (val < 0.01f) val = 0.01f;
                        ConfigCache.viewModelSize = val;
                        msg("view size §8= §f" + val);
                    }
                    default -> msg("§cunknown axis. use: x, y, z, size");
                }
                save();
            }
            case "smalltotem", "totem" -> {
                ConfigCache.smallTotem = !ConfigCache.smallTotem;
                msg("small totem §8" + (ConfigCache.smallTotem ? "§aON" : "§cOFF"));
                save();
            }
            case "customglint", "glint" -> {
                if (parts.length < 2) {
                    msg("§ccustomglint r/g/b <0-255> or toggle");
                    return;
                }
                switch (parts[1]) {
                    case "r" -> {
                        if (parts.length < 3) { msg("§cusage: !sparrow customglint r <0-255>"); return; }
                        int val = parseInt(parts[2]);
                        if (val < 0 || val > 255) { msg("§cmust be 0-255"); return; }
                        ConfigCache.glintR = val;
                        ConfigCache.customGlint = true;
                        msg("glint R §8= §f" + val);
                        sparrow_refreshGlint();
                    }
                    case "g" -> {
                        if (parts.length < 3) { msg("§cusage: !sparrow customglint g <0-255>"); return; }
                        int val = parseInt(parts[2]);
                        if (val < 0 || val > 255) { msg("§cmust be 0-255"); return; }
                        ConfigCache.glintG = val;
                        ConfigCache.customGlint = true;
                        msg("glint G §8= §f" + val);
                        sparrow_refreshGlint();
                    }
                    case "b" -> {
                        if (parts.length < 3) { msg("§cusage: !sparrow customglint b <0-255>"); return; }
                        int val = parseInt(parts[2]);
                        if (val < 0 || val > 255) { msg("§cmust be 0-255"); return; }
                        ConfigCache.glintB = val;
                        ConfigCache.customGlint = true;
                        msg("glint B §8= §f" + val);
                        sparrow_refreshGlint();
                    }
                    case "toggle" -> {
                        ConfigCache.customGlint = !ConfigCache.customGlint;
                        msg("custom glint §8" + (ConfigCache.customGlint ? "§aON" : "§cOFF"));
                        sparrow_refreshGlint();
                    }
                    default -> msg("§cusage: r/g/b <0-255> or toggle");
                }
                save();
            }
            case "oldpotions", "oldpotion" -> {
                ConfigCache.oldPotions = !ConfigCache.oldPotions;
                msg("old potions §8" + (ConfigCache.oldPotions ? "§aON" : "§cOFF"));
                save();
            }
            case "utilityscale", "utilscale" -> {
                if (parts.length < 2) {
                    msg("utility scale §8= §f" + ConfigCache.utilityScale);
                    return;
                }
                float val;
                try { val = Float.parseFloat(parts[1]); } catch (NumberFormatException e) { msg("§cinvalid number"); return; }
                if (val < 0.1f) val = 0.1f;
                if (val > 2.0f) val = 2.0f;
                ConfigCache.utilityScale = val;
                msg("utility scale §8= §f" + val);
                save();
            }
            case "firetimer" -> {
                ConfigCache.fireTimer = !ConfigCache.fireTimer;
                msg("fire timer §8" + (ConfigCache.fireTimer ? "§aON" : "§cOFF"));
                save();
            }
            case "firetimerpos" -> {
                if (parts.length < 2) {
                    msg("fire timer pos §8= §f" + ConfigCache.fireTimerPos);
                    msg("§7Use: !sparrow firetimerpos <TOP_LEFT/TOP_RIGHT/BOTTOM_CENTER>");
                    return;
                }
                String pos = parts[1].toUpperCase();
                if (pos.equals("TOP_LEFT") || pos.equals("TOP_RIGHT") || pos.equals("BOTTOM_CENTER")) {
                    ConfigCache.fireTimerPos = pos;
                    msg("fire timer pos §8= §f" + pos);
                } else {
                    msg("§cInvalid position! Use: TOP_LEFT, TOP_RIGHT, or BOTTOM_CENTER");
                }
                save();
            }
            case "bettermovement", "bettermove" -> {
                boolean enabling = !ConfigCache.betterMovement;
                if (enabling && ServerSafetyState.isOnRestrictedServer()) {
                    msg("§cYou can't do that, buddy.");
                    return;
                }
                ConfigCache.betterMovement = enabling;
                msg("better movement §8" + (ConfigCache.betterMovement ? "§aON" : "§cOFF"));
                if (ConfigCache.betterMovement) {
                    msg("§cYou may get banned on multiplayer servers!");
                }
                save();
            }
            case "particles", "particle" -> {
                if (parts.length < 2) {
                    msg("particles §8= §f" + ConfigCache.particleMode);
                    return;
                }
                String mode = parts[1].toLowerCase(Locale.ROOT);
                if (!mode.equals("off") && !mode.equals("minimal") && !mode.equals("on")) {
                    msg("§cuse: off, minimal, or on");
                    return;
                }
                ConfigCache.particleMode = mode;
                msg("particles §8= " + getColor(mode) + mode);
                save();
            }
            case "togglesneak" -> {
                ToggleSneakState.toggle();
                msg("toggle sneak §8" + (ToggleSneakState.enabled ? "§aON" : "§cOFF"));
            }
            case "nominingfatigue", "nominefatigue" -> {
                ConfigCache.noMiningFatigue = !ConfigCache.noMiningFatigue;
                msg("no mining fatigue §8" + (ConfigCache.noMiningFatigue ? "§aON" : "§cOFF"));
                if (ConfigCache.noMiningFatigue) {
                    msg("§cWarning: Ghost blocks! Server rejects your mining.");
                }
                save();
            }
            default -> msg("§cunknown command. try §7!sparrow help");
        }
    }

    @Unique
    private static String getColor(String mode) {
        return switch (mode) {
            case "off" -> "§c";
            case "minimal" -> "§e";
            case "on" -> "§a";
            default -> "§7";
        };
    }

    @Unique
    private static int parseInt(String s) {
        try { return Integer.parseInt(s); } catch (NumberFormatException e) { return -1; }
    }

    @Unique
    private static void sparrow_refreshGlint() {
        SparrowGlintLayers.refresh();
    }

    @Unique
    private static void save() {
        if (ConfigReader.getInstance() != null) {
            ConfigReader.saveFromCache();
        }
    }
}
