package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.SparrowMod;
import net.minecraft.client.option.GameOptions;
import net.minecraft.client.option.KeyBinding;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GameOptions.class)
public class GameOptionsMixin {

    @Shadow @Final @Mutable
    private KeyBinding[] allKeys;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void registerSparrowKeybinds(CallbackInfo ci) {
        KeyBinding[] extended = new KeyBinding[this.allKeys.length + 3];
        System.arraycopy(this.allKeys, 0, extended, 0, this.allKeys.length);
        extended[this.allKeys.length] = SparrowMod.ZOOM_KEY;
        extended[this.allKeys.length + 1] = SparrowMod.STORAGE_PREVIEW_KEY;
        extended[this.allKeys.length + 2] = SparrowMod.TOGGLE_SNEAK_KEY;
        this.allKeys = extended;
        KeyBinding.updateKeysByCode();
    }
}
