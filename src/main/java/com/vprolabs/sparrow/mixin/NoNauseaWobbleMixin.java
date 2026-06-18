package com.vprolabs.sparrow.mixin;

import net.minecraft.client.render.GameRenderer;
import org.spongepowered.asm.mixin.Mixin;

// TODO: redundant mixin — bobView cancel moved to BobDisableMixin (canonical),
// getSkyDarkness cancel moved to AlwaysDayMixin (config-gated). Kept as a stub
// per project hard rule "NEVER remove features without explicit user instruction".
@Mixin(GameRenderer.class)
public class NoNauseaWobbleMixin {
}
