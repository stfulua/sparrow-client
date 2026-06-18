package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.debug.DebugRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(DebugRenderer.class)
public interface DebugRendererAccessor {
    @Accessor("renderers")
    List<DebugRenderer.Renderer> getRenderers();
}
