package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.mixin.Utils.SpriteContentsAccessor;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SpriteContents.class)
public class SpriteAnimatorFixMixin {
@ModifyVariable(method = "upload", at = @At("HEAD"), argsOnly = true)
    private int sparrow_capMipLevel(int mipLevel) {
NativeImage[] images = ((SpriteContentsAccessor)(Object)this).getMipmapLevelsImages();
            if (images == null || images.length == 0) return 0;
            return Math.min(mipLevel, images.length - 1);
}

}
