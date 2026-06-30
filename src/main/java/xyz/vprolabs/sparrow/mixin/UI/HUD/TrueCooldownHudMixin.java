package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.tweaks.CooldownRenderer;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.render.RenderTickCounter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class TrueCooldownHudMixin {
    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_renderCooldownHud(DrawContext context, RenderTickCounter tickCounter, CallbackInfo ci) {
        if (!ConfigRegister.trueCooldown.get()) return;
        if (MinecraftClient.getInstance().options.hudHidden) return;
            CooldownRenderer.render(context, MinecraftClient.getInstance().textRenderer);
    }
}
