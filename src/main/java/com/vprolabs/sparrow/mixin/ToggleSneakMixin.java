package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.SparrowMod;
import com.vprolabs.sparrow.state.ToggleSneakState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Toggle Sneak — injects at KeyboardInput.tick() RETURN to override the sneak field
 * of PlayerInput when the toggle is enabled. Targets KeyboardInput (not Input)
 * because KeyboardInput overrides tick() without calling super.tick().
 * Rather than toggling sneakKey.setPressed() which was unreliable and caused
 * sticky-crouch bugs.
 *
 * Does NOT use @Shadow for playerInput — it's inherited from Input, and @Shadow
 * on inherited fields requires the refmap which is not in the JAR. Instead,
 * accesses via ((Input)(Object)this).playerInput since the field is public.
 */
@Mixin(value = KeyboardInput.class, priority = 2000)
public class ToggleSneakMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void sparrow_toggleSneak(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.options == null) return;

        Input self = (Input)(Object)this;

        if (SparrowMod.TOGGLE_SNEAK_KEY.wasPressed()) {
            ToggleSneakState.toggle();
        }

        if (ToggleSneakState.enabled) {
            // Guard: only allocate a new PlayerInput if the sneak bit actually
            // differs. This eliminates ~20 allocations/sec when toggle is ON
            // and the player is already crouched (the common case).
            PlayerInput current = self.playerInput;
            if (current.sneak()) return;
            self.playerInput = new PlayerInput(
                current.forward(),
                current.backward(),
                current.left(),
                current.right(),
                current.jump(),
                true,                          // force sneak ON
                current.sprint()
            );
        }
    }
}
