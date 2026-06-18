package com.vprolabs.sparrow.mixin;

import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ParticleManager.class)
public interface ParticleManagerAccessor {
    @Accessor("particles")
    Map<ParticleTextureSheet, ParticleRenderer<?>> getParticles();
}
