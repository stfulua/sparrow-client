package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.config.ConfigCache;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.particle.ParticleRenderer;
import net.minecraft.client.particle.ParticleTextureSheet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.IdentityHashMap;
import java.util.Map;

@Mixin(ParticleManager.class)
public class ParticleLimiterMixin {

    @Unique private static final int MAX_TOTAL = 100;
    @Unique private static final int MAX_PER_TYPE = 30;

    // ── O(1) live-particle counters ──
    // Incremented at addParticle(Particle) HEAD after passing the cancel check.
    // Reconciled to ground truth at tick() TAIL so dead-particle removals are
    // reflected. Per-spawn limit check is O(1) — replaces the old O(n) scan.
    @Unique private static int sparrow_liveCount = 0;
    @Unique private static final Map<Class<?>, Integer> sparrow_liveByType = new IdentityHashMap<>();
    // Reused across tick() TAIL reconciliations to avoid 20/sec IdentityHashMap allocation.
    @Unique private static final Map<Class<?>, Integer> sparrow_reconcileScratch = new IdentityHashMap<>();

    // ── 1-arg: filter addition + increment live counter on accept ──
    @Inject(method = "addParticle(Lnet/minecraft/client/particle/Particle;)V", at = @At("HEAD"), cancellable = true)
    private void sparrow_filterParticle(Particle particle, CallbackInfo ci) {
        String mode = ConfigCache.particleMode;
        if (mode == null || mode.equals("on")) {
            sparrow_liveCount++;
            sparrow_liveByType.merge(particle.getClass(), 1, Integer::sum);
            return;
        }

        if (mode.equals("off")) {
            ci.cancel();
            return;
        }

        if (!mode.equals("minimal")) return;

        // minimal — O(1) cap check via cached counters
        if (sparrow_liveCount >= MAX_TOTAL) {
            ci.cancel();
            return;
        }
        Class<?> cls = particle.getClass();
        Integer tc = sparrow_liveByType.get(cls);
        if (tc != null && tc >= MAX_PER_TYPE) {
            ci.cancel();
            return;
        }
        sparrow_liveCount++;
        sparrow_liveByType.merge(cls, 1, Integer::sum);
    }

    // ── tick TAIL: reconcile counters to actual queue state ──
    // O(n) once per game tick (not per spawn). Covers particles that died
    // during tick. No-op in off mode (tick is cancelled at HEAD).
    @Inject(method = "tick", at = @At("TAIL"))
    private void sparrow_reconcileCounters(CallbackInfo ci) {
        int total = 0;
        sparrow_reconcileScratch.clear();
        for (ParticleRenderer<?> renderer : ((ParticleManagerAccessor)(Object)this).getParticles().values()) {
            for (Particle p : renderer.getParticles()) {
                total++;
                sparrow_reconcileScratch.merge(p.getClass(), 1, Integer::sum);
            }
        }
        sparrow_liveCount = total;
        sparrow_liveByType.clear();
        sparrow_liveByType.putAll(sparrow_reconcileScratch);
    }

    // ── tick: skip entirely when off ──
    @Inject(method = "tick", at = @At("HEAD"), cancellable = true)
    private void sparrow_skipTick(CallbackInfo ci) {
        if ("off".equals(ConfigCache.particleMode)) {
            ci.cancel();
        }
    }

    // ── show mode in F3 debug ──
    @Inject(method = "getDebugString", at = @At("RETURN"), cancellable = true)
    private void sparrow_debugMode(CallbackInfoReturnable<String> cir) {
        String mode = ConfigCache.particleMode;
        if (mode != null && !mode.equals("on")) {
            String existing = cir.getReturnValue();
            cir.setReturnValue((existing == null ? "" : existing) + " [" + mode + "]");
        }
    }

}
