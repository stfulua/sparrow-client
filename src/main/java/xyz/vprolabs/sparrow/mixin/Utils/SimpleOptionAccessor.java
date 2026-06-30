package xyz.vprolabs.sparrow.mixin.Utils;

import net.minecraft.client.option.SimpleOption;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SimpleOption.class)
public interface SimpleOptionAccessor {

    @Accessor("value")
    void setValue(Object value);
}
