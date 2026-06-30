package xyz.vprolabs.sparrow.mixin.Bugfix;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.network.ClientConnection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientConnection.class)
public class PacketErrorIgnoreMixin {

    @Inject(method = "exceptionCaught", at = @At("HEAD"), cancellable = true)
    private void sparrow_onExceptionCaught(ChannelHandlerContext ctx, Throwable cause, CallbackInfo ci) {
        if (cause instanceof DecoderException && cause.getMessage() != null
                && cause.getMessage().contains("unknown packet id")) {
            SparrowLogger.warn("Ignored unknown network packet: " + cause.getMessage());
            ci.cancel();
        }
    }
}
