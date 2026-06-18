/*
 * Made By: vProLabs (https://www.vprolabs.xyz)
 * Discord: discord.gg/SNzUYWbc5Q
 * Donations: ko-fi.com/v4bi
 */

package com.vprolabs.sparrow.mixin;

import net.minecraft.client.gui.Element;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Screen.class)
public interface ScreenInvoker {
    @Invoker("addDrawableChild")
    <T extends Element> T invokeAddDrawableChild(T element);
}
