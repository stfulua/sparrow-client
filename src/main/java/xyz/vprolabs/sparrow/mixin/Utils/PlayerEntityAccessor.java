package xyz.vprolabs.sparrow.mixin.Utils;

import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(LivingEntity.class)
public interface PlayerEntityAccessor {
    @Accessor("ticksSinceLastAttack")
    int getTicksSinceLastAttack();

    @Accessor("ticksSinceLastAttack")
    void setTicksSinceLastAttack(int ticks);
}
