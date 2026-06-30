package xyz.vprolabs.sparrow.mixin.Utils;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpriteContents.Animator.class)
public interface SpriteAnimatorAccessor {
    @Accessor("animationInfosByFrame")
    GpuBufferSlice[] getAnimationInfosByFrame();
}
