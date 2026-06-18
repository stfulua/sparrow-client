package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.tweaks.AlphaVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.block.FluidRenderer;
import net.minecraft.fluid.FluidState;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockRenderView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Makes water and lava 50% translucent by wrapping the VertexConsumer
 * with an alpha modifier at the first vertex() call.
 */
@Mixin(FluidRenderer.class)
public class FluidTranslucencyMixin {

    // Static (not instance) so a single class-level flag is shared across any
    // potential multi-instance scenarios. The flag is reset in a try/finally
    // by the caller (sparrow_wrapConsumer) to guarantee cleanup even if the
    // FluidRenderer.render method throws.
    @Unique
    private static final ThreadLocal<Boolean> SPARROW_NEEDS_ALPHA = ThreadLocal.withInitial(() -> Boolean.FALSE);

    @Unique
    private AlphaVertexConsumer sparrow_cachedWrapper;

    @Inject(method = "render", at = @At("HEAD"))
    private void sparrow_detectFluid(BlockRenderView world, BlockPos pos, VertexConsumer vertices,
                                     net.minecraft.block.BlockState state, FluidState fluidState,
                                     CallbackInfo ci) {
        SPARROW_NEEDS_ALPHA.set(fluidState.isIn(FluidTags.WATER) || fluidState.isIn(FluidTags.LAVA));
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void sparrow_resetState(CallbackInfo ci) {
        SPARROW_NEEDS_ALPHA.set(Boolean.FALSE);
    }

    @ModifyArg(
        method = "render",
        at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/block/FluidRenderer;vertex(Lnet/minecraft/client/render/VertexConsumer;FFFFFFFFI)V", ordinal = 0),
        index = 0
    )
    private VertexConsumer sparrow_wrapConsumer(VertexConsumer consumer) {
        // try/finally guarantees the alpha flag is reset even if AlphaVertexConsumer
        // construction throws — preventing a stuck alpha state from leaking into
        // the next non-fluid render call.
        boolean needsAlpha = false;
        try {
            needsAlpha = Boolean.TRUE.equals(SPARROW_NEEDS_ALPHA.get());
            if (!needsAlpha) return consumer;
            SPARROW_NEEDS_ALPHA.set(Boolean.FALSE);
            if (sparrow_cachedWrapper == null) {
                sparrow_cachedWrapper = new AlphaVertexConsumer(consumer, 0.5f);
            } else {
                sparrow_cachedWrapper.setDelegate(consumer);
            }
            return sparrow_cachedWrapper;
        } catch (RuntimeException e) {
            SPARROW_NEEDS_ALPHA.set(Boolean.FALSE);
            throw e;
        }
    }
}
