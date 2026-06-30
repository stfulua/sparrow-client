package xyz.vprolabs.sparrow.mixin.Console;
import xyz.vprolabs.sparrow.console.SparrowConsoleScreen;
import net.minecraft.client.Keyboard;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.input.KeyInput;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Keyboard.class)
public class SparrowConsoleKeyMixin {

    @Unique
    private static Screen sparrow_previousScreen = null;

    @Inject(method = "onKey", at = @At("HEAD"), cancellable = true)
    private void onKey(long window, int action, KeyInput keyInput, CallbackInfo ci) {
        if (keyInput.getKeycode() == GLFW.GLFW_KEY_RIGHT_SHIFT && action == GLFW.GLFW_PRESS) {
            MinecraftClient client = MinecraftClient.getInstance();
            try {
                if (client.currentScreen instanceof SparrowConsoleScreen) {
                    // Close console, restore previous screen
                    Screen prev = sparrow_previousScreen;
                    sparrow_previousScreen = null;
                    client.setScreen(prev);
                    ci.cancel();
                } else if (client.currentScreen == null || client.currentScreen instanceof TitleScreen) {
                    // Open console (in-game or from main menu)
                    sparrow_previousScreen = client.currentScreen;
                    client.setScreen(new SparrowConsoleScreen());
                    ci.cancel();
                }
                // else: chat, settings, sign editing, typing screens — let Right Shift pass through
            } catch (Exception ignored) {
                // intentional — disconnection edge case, keypress not critical
            }
        }
    }
}
