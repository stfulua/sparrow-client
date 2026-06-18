package com.vprolabs.sparrow.mixin;

import com.mojang.brigadier.suggestion.Suggestions;
import net.minecraft.client.gui.screen.ChatInputSuggestor;
import net.minecraft.client.gui.widget.TextFieldWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.CompletableFuture;

@Mixin(ChatInputSuggestor.class)
public interface ChatInputSuggestorAccessor {
    @Accessor("textField")
    TextFieldWidget getTextField();

    @Accessor("pendingSuggestions")
    CompletableFuture<Suggestions> getPendingSuggestions();

    @Accessor("pendingSuggestions")
    void setPendingSuggestions(CompletableFuture<Suggestions> suggestions);
}
