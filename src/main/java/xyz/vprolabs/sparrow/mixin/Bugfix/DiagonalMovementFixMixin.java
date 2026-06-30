package xyz.vprolabs.sparrow.mixin.Bugfix;

import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class DiagonalMovementFixMixin {

    @Inject(method = "tick", at = @At("RETURN"))
    private void sparrow_normalizeDiagonal(CallbackInfo ci) {
            Input self = (Input)(Object)this;
            PlayerInput pi = self.playerInput;
            if (!pi.sneak()) return;

            boolean fwd = pi.forward();
            boolean back = pi.backward();
            boolean left = pi.left();
            boolean right = pi.right();

            if (fwd && back) back = false;
            if (left && right) right = false;

            if (!((fwd || back) && (left || right))) return;

            // Sneak diagonal lacks 1/sqrt(2) normalization (MC-271065 revert).
            // Drop lateral component to normalize horizontal speed.
            left = false;
            right = false;

            PlayerInput current = pi;
            if (fwd != current.forward() || back != current.backward()
                || left != current.left() || right != current.right()) {
                self.playerInput = new PlayerInput(
                    fwd, back, left, right,
                    current.jump(), current.sneak(), current.sprint()
                );
            }
    }
}
