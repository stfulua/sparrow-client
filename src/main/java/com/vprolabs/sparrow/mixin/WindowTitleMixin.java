package com.vprolabs.sparrow.mixin;

import net.minecraft.client.util.Window;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(Window.class)
public class WindowTitleMixin {

    @ModifyArg(
        method = "setTitle",
        at = @At(
            value = "INVOKE",
            target = "Lorg/lwjgl/glfw/GLFW;glfwSetWindowTitle(JLjava/lang/CharSequence;)V",
            remap = false
        ),
        index = 1
    )
    private CharSequence sparrow_modifyTitle(CharSequence title) {
        return "Sparrow Client 1.21.11";
    }
}
