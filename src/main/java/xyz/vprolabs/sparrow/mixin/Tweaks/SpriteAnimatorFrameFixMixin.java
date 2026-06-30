package xyz.vprolabs.sparrow.mixin.Tweaks;

import com.mojang.blaze3d.buffers.GpuBufferSlice;
import xyz.vprolabs.sparrow.mixin.Utils.SpriteAnimatorAccessor;
import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpriteContents.Animator.class)
public class SpriteAnimatorFrameFixMixin {
@ModifyVariable(method = "getBufferSlice", at = @At("HEAD"), argsOnly = true)
    private int sparrow_clampFrameIndex(int frame) {
    GpuBufferSlice[] infos = ((SpriteAnimatorAccessor)(Object)this).getAnimationInfosByFrame();
            if (infos == null || infos.length == 0) {
                return 0;
            }
            if (frame < 0) {
                return 0;
            }
            if (frame >= infos.length) {
                return infos.length - 1;
            }
        return frame;
}
}
