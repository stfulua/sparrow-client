package xyz.vprolabs.sparrow.mixin.Tweaks;

import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.state.ServerSafety;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.input.Input;
import net.minecraft.client.input.KeyboardInput;
import net.minecraft.client.option.GameOptions;
import net.minecraft.util.PlayerInput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardInput.class)
public class BetterMovementMixin {
    @Unique private boolean sparrow_forwardPriority;
    @Unique private boolean sparrow_leftPriority;

    @Inject(method = "tick", at = @At("RETURN"))
    private void sparrow_onTick(CallbackInfo ci) {
            if (!ConfigRegister.betterMovement.get() || ServerSafety.isFeatureDisabled("better-movement")) return;

            GameOptions settings = MinecraftClient.getInstance().options;
            if (settings == null) return;

            Input self = (Input)(Object)this;
            PlayerInput playerInput = self.playerInput;
            boolean forward = playerInput.forward();
            boolean back = playerInput.backward();
            boolean left = playerInput.left();
            boolean right = playerInput.right();

            if (forward && settings.forwardKey.wasPressed()) sparrow_forwardPriority = true;
            if (back && settings.backKey.wasPressed()) sparrow_forwardPriority = false;

            if (forward && back) {
                if (sparrow_forwardPriority) back = false;
                else forward = false;
            }

            if (left && settings.leftKey.wasPressed()) sparrow_leftPriority = true;
            if (right && settings.rightKey.wasPressed()) sparrow_leftPriority = false;

            if (left && right) {
                if (sparrow_leftPriority) right = false;
                else left = false;
            }

            PlayerInput current = playerInput;
            if (forward != current.forward() || back != current.backward() || left != current.left() || right != current.right()) {
                self.playerInput = new PlayerInput(forward, back, left, right, current.jump(), current.sneak(), current.sprint());
            }
    }
}
