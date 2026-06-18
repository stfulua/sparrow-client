package com.vprolabs.sparrow.mixin;

import com.mojang.brigadier.suggestion.Suggestion;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public class ChatInputSuggestorMixin {

    private static final List<CommandEntry> COMMANDS = Arrays.asList(
        new CommandEntry("help",          "List all Sparrow commands"),
        new CommandEntry("smooth",        "Toggle smooth elytra flight"),
        new CommandEntry("elytra",        "Toggle smooth elytra flight"),
        new CommandEntry("fast",          "Toggle fast block placement"),
        new CommandEntry("place",         "Toggle fast block placement"),
        new CommandEntry("viewmodel",     "Adjust view model position or size"),
        new CommandEntry("vm",            "Adjust view model position or size"),
        new CommandEntry("smalltotem",    "Toggle small totem popup"),
        new CommandEntry("totem",         "Toggle small totem popup"),
        new CommandEntry("oldpotions",    "Toggle old potion colors"),
        new CommandEntry("oldpotion",     "Toggle old potion colors"),
        new CommandEntry("customglint",   "Set custom glint color or toggle"),
        new CommandEntry("glint",         "Set custom glint color or toggle"),
        new CommandEntry("utilityscale",  "Set utility item scale (0.1-2.0)"),
        new CommandEntry("utilscale",     "Set utility item scale (0.1-2.0)"),
        new CommandEntry("zoom",          "Set zoom magnification level"),
        new CommandEntry("zoomsmooth",    "Set zoom transition smoothness"),
        new CommandEntry("particles",     "Set particle mode (off/minimal/on)"),
        new CommandEntry("particle",      "Set particle mode (off/minimal/on)")
    );

    private static final List<CommandEntry> GLINT_ARGS = Arrays.asList(
        new CommandEntry("r",      "Red channel (0-255)"),
        new CommandEntry("g",      "Green channel (0-255)"),
        new CommandEntry("b",      "Blue channel (0-255)"),
        new CommandEntry("toggle", "Toggle custom glint on/off")
    );

    private static final List<CommandEntry> VIEWMODEL_ARGS = Arrays.asList(
        new CommandEntry("x",     "Horizontal position offset"),
        new CommandEntry("y",     "Vertical position offset"),
        new CommandEntry("z",     "Depth position offset"),
        new CommandEntry("size",  "Item render scale")
    );

    private record SubSpec(String[] triggers, List<CommandEntry> args) {}

    private static final List<SubSpec> SUB_COMMANDS = Arrays.asList(
        new SubSpec(new String[]{"customglint", "glint"}, GLINT_ARGS),
        new SubSpec(new String[]{"viewmodel", "vm"}, VIEWMODEL_ARGS)
    );

    @Inject(method = "refresh", at = @At("HEAD"), cancellable = true)
    private void onRefresh(CallbackInfo ci) {
        ChatInputSuggestorAccessor acc = (ChatInputSuggestorAccessor)(Object)this;
        String text = acc.getTextField().getText();
        if (!text.startsWith("!")) return;

        String after = text.substring(1);
        SuggestionsBuilder builder = new SuggestionsBuilder(text, 0);

        if (after.isEmpty() || (after.length() < 7 && "sparrow".startsWith(after))) {
            builder.suggest("!sparrow ", Text.literal("Sparrow client commands"));
            apply(builder, ci);
            return;
        }

        if (!after.startsWith("sparrow")) return;

        String rest = after.substring(7);

        if (rest.isEmpty()) {
            for (CommandEntry cmd : COMMANDS) {
                builder.suggest("!sparrow " + cmd.command, Text.literal(cmd.description));
            }
            apply(builder, ci);
            return;
        }

        if (!rest.startsWith(" ")) return;
        String argPart = rest.substring(1);

        for (SubSpec spec : SUB_COMMANDS) {
            String matched = null;
            for (String t : spec.triggers()) {
                if (argPart.equals(t) || argPart.startsWith(t + " ")) {
                    matched = t;
                    break;
                }
            }
            if (matched == null) continue;

            String prefix = "!sparrow " + matched + " ";
            String sub = argPart.equals(matched) ? "" : argPart.substring(matched.length() + 1);
            for (CommandEntry arg : spec.args()) {
                if (arg.command.startsWith(sub)) {
                    builder.suggest(prefix + arg.command + " ", Text.literal(arg.description));
                }
            }
            if (!builder.build().isEmpty()) {
                apply(builder, ci);
                return;
            }
        }

        for (CommandEntry cmd : COMMANDS) {
            if (cmd.command.startsWith(argPart)) {
                builder.suggest("!sparrow " + cmd.command, Text.literal(cmd.description));
            }
        }

        if (!builder.build().isEmpty()) {
            apply(builder, ci);
        }
    }

    private void apply(SuggestionsBuilder builder, CallbackInfo ci) {
        ChatInputSuggestorAccessor acc = (ChatInputSuggestorAccessor)(Object)this;
        ChatInputSuggestor self = (ChatInputSuggestor)(Object)this;
        Suggestions suggestions = builder.build();
        acc.setPendingSuggestions(CompletableFuture.completedFuture(suggestions));
        self.setWindowActive(true);
        self.show(true);
        ci.cancel();
    }

    private record CommandEntry(String command, String description) {}
}
