package xyz.vprolabs.sparrow.mixin.Optimization.Cull;

import xyz.vprolabs.sparrow.config.ConfigRegister;
import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.packet.s2c.play.ExplosionS2CPacket;
import net.minecraft.network.packet.s2c.play.ParticleS2CPacket;
import net.minecraft.util.math.Vec3d;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class PacketDistanceCullMixin {

    @Unique private static final float SPARROW_PARTICLE_CULL_DIST = 64.0f;
    @Unique private static final float SPARROW_PARTICLE_CULL_DIST_SQ =
        SPARROW_PARTICLE_CULL_DIST * SPARROW_PARTICLE_CULL_DIST;

    @Unique private static boolean sparrow_cullLogged = false;

    @Inject(method = "onParticle", at = @At("HEAD"), cancellable = true)
    private void sparrow_cullDistantParticle(ParticleS2CPacket packet, CallbackInfo ci) {
            String mode = ConfigRegister.particleMode.get();
            if ("off".equals(mode)) return;
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            double dx = packet.getX() - player.getX();
            double dy = packet.getY() - player.getY();
            double dz = packet.getZ() - player.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;

            if (distSq > SPARROW_PARTICLE_CULL_DIST_SQ) {
                if (!sparrow_cullLogged) {
                    sparrow_cullLogged = true;
                    SparrowLogger.debug("PacketDistanceCullMixin: culling particles > 64 blocks from player");
                }
                ci.cancel();
            }
    }

    @Inject(method = "onExplosion", at = @At("HEAD"), cancellable = true)
    private void sparrow_cullDistantExplosion(ExplosionS2CPacket packet, CallbackInfo ci) {
            PlayerEntity player = MinecraftClient.getInstance().player;
            if (player == null) return;

            Vec3d center = packet.center();
            if (center == null) return;
            double dx = center.x - player.getX();
            double dy = center.y - player.getY();
            double dz = center.z - player.getZ();
            double distSq = dx * dx + dy * dy + dz * dz;
            float radius = packet.radius();

            double cullDist = SPARROW_PARTICLE_CULL_DIST + radius;
            if (distSq > cullDist * cullDist) {
                ci.cancel();
            }
    }
}
