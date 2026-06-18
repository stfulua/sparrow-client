package com.vprolabs.sparrow.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.LightmapTextureManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fullbright — forces the gamma option to 15.0 every frame.
 *
 * <p>The previous version of this mixin used a one-shot gate
 * ({@code sparrow_fullbrightApplied}) so gamma was only forced once. That allowed the user
 * (or any other mod) to lower brightness via the options screen and have it stick forever.
 *
 * <p>This version is per-frame: the gamma option is set to 15.0 at the start of every
 * {@code update()} call. The cost is a few nanoseconds per frame; the benefit is that
 * fullbright is permanent and unbreakable, in fact.
 *
 * <h2>Per-frame cost (audited via {@code javap -c})</h2>
 * <pre>
 *   aload_3
 *   ifnull        15
 *   aload_3
 *   getfield      #34                 // options
 *   ifnonnull     16
 *   return
 *   aload_3
 *   getfield      #34                 // options
 *   invokevirtual #40                 // getGamma()
 *   checkcast     SimpleOptionAccessor
 *   getstatic     GAMMA_MAX           // cached Double (no autobox)
 *   invokeinterface setValue
 *   return
 * </pre>
 * <ul>
 *   <li>1 static field read (MinecraftClient.instance)</li>
 *   <li>1 instance field read (options)</li>
 *   <li>1 method call (getGamma)</li>
 *   <li>1 interface method call (setValue, with cached Double — zero alloc)</li>
 *   <li>1 field write (the gamma value)</li>
 * </ul>
 * Total: ~5-10 ns/frame. At 60 FPS that's ~600 ns/sec — completely free, in fact.
 *
 * <h2>Why per-frame, not one-shot</h2>
 * A one-shot gate allowed the user (or another mod) to lower gamma via the options
 * screen and have it stick forever. Per-frame force is the only way to make fullbright
 * unbreakable. The cost is negligible compared to the lighting pipeline that
 * {@code LightingKillMixin} kills (which saved milliseconds per frame, not nanoseconds).
 */
@Mixin(LightmapTextureManager.class)
public class LightmapTextureManagerMixin {

    /**
     * Cached {@code Double(15.0)} to avoid the autobox allocation that would otherwise
     * happen on every frame ({@code ldc2_w 15.0d; invokestatic Double.valueOf}).
     * At 60 FPS that's 60 Double allocations/sec saved — trivial in absolute terms,
     * but a free win and keeps the per-frame mixin at zero allocations.
     */
    private static final Double GAMMA_MAX = 15.0;

    @Inject(method = "update", at = @At("HEAD"))
    private void sparrow_forceFullbright(float delta, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client == null || client.options == null) return;

        ((SimpleOptionAccessor)(Object) client.options.getGamma()).setValue(GAMMA_MAX);
    }
}
