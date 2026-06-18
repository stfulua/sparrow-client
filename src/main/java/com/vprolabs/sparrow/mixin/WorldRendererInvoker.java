package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.entity.state.EntityRenderState;
import net.minecraft.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(WorldRenderer.class)
public interface WorldRendererInvoker {
    @Invoker("getAndUpdateRenderState")
    EntityRenderState invokeGetAndUpdateRenderState(Entity entity, float tickDelta);
}
