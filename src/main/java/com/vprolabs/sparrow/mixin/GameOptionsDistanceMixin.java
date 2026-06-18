package com.vprolabs.sparrow.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsDistanceMixin {

    @Shadow @Final @Mutable
    private SimpleOption<Integer> viewDistance;

    @Shadow @Final @Mutable
    private SimpleOption<Integer> simulationDistance;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void expandDistances(CallbackInfo ci) {
        int oldRender = this.viewDistance.getValue();
        if (oldRender > 8 || oldRender < 2) oldRender = 8;
        int oldSim = this.simulationDistance.getValue();

        this.viewDistance = new SimpleOption<>(
            "options.renderDistance",
            SimpleOption.emptyTooltip(),
            (optionText, value) -> GameOptions.getGenericValueText(optionText, value),
            new SimpleOption.ValidatingIntSliderCallbacks(1, 64, false),
            oldRender,
            value -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client == null) return;
                client.options.write();
                if (client.worldRenderer != null) {
                    client.worldRenderer.reload();
                }
            }
        );

        this.simulationDistance = new SimpleOption<>(
            "options.simulationDistance",
            SimpleOption.emptyTooltip(),
            (optionText, value) -> GameOptions.getGenericValueText(optionText, value),
            new SimpleOption.ValidatingIntSliderCallbacks(1, 32, false),
            oldSim,
            value -> {
                MinecraftClient client = MinecraftClient.getInstance();
                if (client == null) return;
                client.options.write();
                if (client.worldRenderer != null) {
                    client.worldRenderer.reload();
                }
            }
        );
    }
}
