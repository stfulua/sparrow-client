package com.vprolabs.sparrow.mixin;

import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GameOptions.class)
public interface GameOptionsAccessor {
    @Accessor("narratorHotkey")
    SimpleOption<Boolean> getNarratorHotkey();

    @Accessor("distortionEffectScale")
    SimpleOption<Double> getDistortionEffectScale();

    @Accessor("darknessEffectScale")
    SimpleOption<Double> getDarknessEffectScale();

    @Accessor("fovEffectScale")
    SimpleOption<Double> getFovEffectScale();

    @Accessor("damageTiltStrength")
    SimpleOption<Double> getDamageTiltStrength();

    @Accessor("ao")
    SimpleOption<Boolean> getAo();

    @Accessor("mipmapLevels")
    SimpleOption<Integer> getMipmapLevels();

    @Accessor("biomeBlendRadius")
    SimpleOption<Integer> getBiomeBlendRadius();

    @Accessor("maxAnisotropy")
    SimpleOption<Integer> getMaxAnisotropy();

    @Accessor("weatherRadius")
    SimpleOption<Integer> getWeatherRadius();

    @Accessor("menuBackgroundBlurriness")
    SimpleOption<Integer> getMenuBackgroundBlurriness();

    @Accessor("cloudRenderDistance")
    SimpleOption<Integer> getCloudRenderDistance();

    @Accessor("showAutosaveIndicator")
    SimpleOption<Boolean> getShowAutosaveIndicator();

    @Accessor("autoJump")
    SimpleOption<Boolean> getAutoJump();

    @Accessor("improvedTransparency")
    SimpleOption<Boolean> getImprovedTransparency();

    @Accessor("cutoutLeaves")
    SimpleOption<Boolean> getCutoutLeaves();

    @Accessor("vignette")
    SimpleOption<Boolean> getVignette();

    @Accessor("gamma")
    SimpleOption<Double> getGamma();

    @Accessor("bobView")
    SimpleOption<Boolean> getBobView();

    // ── Distance / keybind field replacements ──
    @Accessor("viewDistance")
    SimpleOption<Integer> getViewDistanceField();

    @Accessor("viewDistance")
    void setViewDistanceField(SimpleOption<Integer> value);

    @Accessor("simulationDistance")
    SimpleOption<Integer> getSimulationDistanceField();

    @Accessor("simulationDistance")
    void setSimulationDistanceField(SimpleOption<Integer> value);

    @Accessor("allKeys")
    net.minecraft.client.option.KeyBinding[] getAllKeys();

    @Accessor("allKeys")
    void setAllKeys(net.minecraft.client.option.KeyBinding[] keys);
}
