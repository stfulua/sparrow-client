package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.WorldRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(WorldRenderer.class)
public class EntityRenderStateCullMixin {

    @Redirect(
        method = "fillEntityRenderStates",
        at = @At(
            value = "INVOKE",
            target = "Ljava/util/List;add(Ljava/lang/Object;)Z",
            ordinal = 0
        )
    )
    private boolean sparrow_skipNullRenderStates(List<Object> list, Object value) {
        if (value == null) {
            return false;
        }
        list.add(value);
        return true;
    }
}
