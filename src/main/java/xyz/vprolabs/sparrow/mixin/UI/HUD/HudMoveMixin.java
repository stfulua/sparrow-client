package xyz.vprolabs.sparrow.mixin.UI.HUD;

import xyz.vprolabs.sparrow.config.ConfigReader;
import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.state.HudMoveState;
import xyz.vprolabs.sparrow.state.HudPositions;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashMap;
import java.util.Map;

@Mixin(MinecraftClient.class)
public class HudMoveMixin {
    @Unique private static final Map<String, int[]> sparrow_savedOffsets = new HashMap<>();
    @Unique private static final String[] ELEMENT_KEYS = {"coords", "ping", "desync", "fire-timer", "ghost-block", "knockback", "shield"};

    public static void activateMoveHud() {
        sparrow_savedOffsets.clear();
        for (String key : ELEMENT_KEYS) {
            int[] off = HudPositions.getOffset(key);
            sparrow_savedOffsets.put(key, new int[]{off[0], off[1]});
        }
        HudMoveState.active = true;
    }

    @Unique private static boolean sparrow_moveKeyWasDown = false;

    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        long handle = client.getWindow().getHandle();

        int moveKey = ConfigRegister.movehudKey.get();
        boolean moveKeyDown = GLFW.glfwGetKey(handle, moveKey) == GLFW.GLFW_PRESS;

        if (!HudMoveState.active && moveKeyDown && !sparrow_moveKeyWasDown) {
            activateMoveHud();
            sparrow_moveKeyWasDown = true;
            return;
        }
        sparrow_moveKeyWasDown = moveKeyDown;

        if (!HudMoveState.active) return;

        if (GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_ENTER) == GLFW.GLFW_PRESS) {
            ConfigReader.saveFromCache();
            HudMoveState.reset();
            return;
        }

        if (GLFW.glfwGetKey(handle, GLFW.GLFW_KEY_ESCAPE) == GLFW.GLFW_PRESS) {
            for (Map.Entry<String, int[]> e : sparrow_savedOffsets.entrySet()) {
                HudPositions.setOffset(e.getKey(), e.getValue()[0], e.getValue()[1]);
            }
            HudMoveState.reset();
            return;
        }

        boolean mouseDown = GLFW.glfwGetMouseButton(handle, GLFW.GLFW_MOUSE_BUTTON_LEFT) == GLFW.GLFW_PRESS;

        if (mouseDown && !HudMoveState.wasMouseDown) {
            double mx = client.mouse.getX();
            double my = client.mouse.getY();
            int scaledW = client.getWindow().getScaledWidth();
            int scaledH = client.getWindow().getScaledHeight();
            int guiX = (int) (mx * scaledW / client.getWindow().getWidth());
            int guiY = (int) (my * scaledH / client.getWindow().getHeight());

            for (Map.Entry<String, int[]> entry : HudMoveState.elementBounds.entrySet()) {
                int[] b = entry.getValue();
                if (guiX >= b[0] && guiX <= b[0] + b[2] && guiY >= b[1] && guiY <= b[1] + b[3]) {
                    HudMoveState.selectedElement = entry.getKey();
                    HudMoveState.dragStartX = guiX;
                    HudMoveState.dragStartY = guiY;
                    int[] off = HudPositions.getOffset(entry.getKey());
                    HudMoveState.origOffsetX = off[0];
                    HudMoveState.origOffsetY = off[1];
                    HudMoveState.isDragging = true;
                    break;
                }
            }
        }

        if (mouseDown && HudMoveState.isDragging && HudMoveState.selectedElement != null) {
            double mx = client.mouse.getX();
            double my = client.mouse.getY();
            int scaledW = client.getWindow().getScaledWidth();
            int scaledH = client.getWindow().getScaledHeight();
            int guiX = (int) (mx * scaledW / client.getWindow().getWidth());
            int guiY = (int) (my * scaledH / client.getWindow().getHeight());

            int deltaX = guiX - HudMoveState.dragStartX;
            int deltaY = guiY - HudMoveState.dragStartY;
            HudPositions.setOffset(HudMoveState.selectedElement,
                HudMoveState.origOffsetX + deltaX,
                HudMoveState.origOffsetY + deltaY);
        }

        if (!mouseDown && HudMoveState.wasMouseDown) {
            HudMoveState.isDragging = false;
            HudMoveState.selectedElement = null;
        }

        HudMoveState.wasMouseDown = mouseDown;
    }
}
