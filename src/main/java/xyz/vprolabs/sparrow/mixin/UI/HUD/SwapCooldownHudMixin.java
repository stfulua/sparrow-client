package xyz.vprolabs.sparrow.mixin.UI.HUD;

import net.minecraft.entity.player.PlayerInventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerInventory.class)
public class SwapCooldownHudMixin {

    // Kept for future cooldown reset tracking — true-cooldown bar was removed
}
