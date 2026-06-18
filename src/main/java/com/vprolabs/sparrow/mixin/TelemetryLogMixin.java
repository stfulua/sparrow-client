package com.vprolabs.sparrow.mixin;

import com.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.session.telemetry.TelemetryLogManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Mixin(TelemetryLogManager.class)
public class TelemetryLogMixin {

    @Inject(method = "create", at = @At("HEAD"), cancellable = true)
    private static void disableTelemetryLogs(Path path, CallbackInfoReturnable<CompletableFuture<Optional<TelemetryLogManager>>> cir) {
        SparrowLogger.debug("TelemetryLogMixin: disabling telemetry log files");
        cir.setReturnValue(CompletableFuture.completedFuture(Optional.empty()));
    }
}
