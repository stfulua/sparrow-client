package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelemetryManager.class)
public class TelemetryMixin {

    @Inject(method = "computeSender", at = @At("HEAD"), cancellable = true)
    private void killTelemetry(CallbackInfoReturnable<TelemetrySender> cir) {
        SparrowLogger.debug("TelemetryMixin: setting NOOP sender");
        cir.setReturnValue(TelemetrySender.NOOP);
    }
}
