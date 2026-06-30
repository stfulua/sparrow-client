package xyz.vprolabs.sparrow.mixin.Utils;
import xyz.vprolabs.sparrow.config.SodiumCompat;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsDistanceMixin {

    @Shadow @Final @Mutable
    private SimpleOption<Integer> viewDistance;

    @Shadow @Final @Mutable
    private SimpleOption<Integer> simulationDistance;

    @Unique
    private static final ThreadLocal<Integer> capturedViewDistance = new ThreadLocal<>();

    @Unique
    private static final ThreadLocal<Integer> capturedSimDistance = new ThreadLocal<>();

    @Inject(method = "load", at = @At("RETURN"))
    private void onLoadReturn(CallbackInfo ci) {
            capturedViewDistance.set(this.viewDistance.getValue());
            capturedSimDistance.set(this.simulationDistance.getValue());
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    private void expandDistances(CallbackInfo ci) {
            Integer capView = capturedViewDistance.get();
            capturedViewDistance.remove();
            Integer capSim = capturedSimDistance.get();
            capturedSimDistance.remove();

            // If Sodium is loaded, let Sodium handle render distance — only expand simulation distance
            boolean sodiumLoaded = SodiumCompat.isSodiumLoaded();

            if (!sodiumLoaded) {
                int oldRender = (capView != null) ? capView : this.viewDistance.getValue();
                if (oldRender > 8 || oldRender < 2) oldRender = 8;

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
            }

            int oldSim = (capSim != null) ? capSim : this.simulationDistance.getValue();
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
