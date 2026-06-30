package xyz.vprolabs.sparrow.mixin.Bugfix;

import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.state.PlayerEntityRenderState;
import net.minecraft.entity.PlayerLikeEntity;
import net.minecraft.item.Items;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public class ShieldVisibilityMixin {

    @Inject(method = "updateRenderState", at = @At("TAIL"))
    private void sparrow_forceShieldPose(PlayerLikeEntity entity, PlayerEntityRenderState state, float tickDelta, CallbackInfo ci) {
            if (!state.isUsingItem) return;
            if (!entity.getActiveItem().isOf(Items.SHIELD)) return;

            Hand hand = entity.getActiveHand();
            Arm mainArm = entity.getMainArm();
            BipedEntityModel.ArmPose blocking = BipedEntityModel.ArmPose.BLOCK;

            if (hand == Hand.MAIN_HAND) {
                if (mainArm == Arm.RIGHT) {
                    state.rightArmPose = blocking;
                } else {
                    state.leftArmPose = blocking;
                }
            } else {
                if (mainArm == Arm.RIGHT) {
                    state.leftArmPose = blocking;
                } else {
                    state.rightArmPose = blocking;
                }
            }
    }
}
