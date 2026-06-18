package com.vprolabs.sparrow.mixin;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor {

    @Accessor("value")
    Object getValue();

    @Accessor("value")
    void setValue(Object value);
}
