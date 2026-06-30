package xyz.vprolabs.sparrow.mixin.Brand;

import net.minecraft.client.session.telemetry.TelemetryManager;
import net.minecraft.client.session.telemetry.TelemetrySender;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TelemetryManager.class)
public class TelemetryKillerMixin {

    @Inject(method = "getSender", at = @At("HEAD"), cancellable = true)
    private void killTelemetrySender(CallbackInfoReturnable<TelemetrySender> cir) {
            cir.setReturnValue(TelemetrySender.NOOP);
    }
}
