package xyz.vprolabs.sparrow.mixin.Optimization.Chunk;

import xyz.vprolabs.sparrow.logging.SparrowLogger;
import net.minecraft.client.render.BufferBuilderStorage;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderManager;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.thread.NameableExecutor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

@Mixin(WorldRenderer.class)
public class ChunkExecutorPoolMixin {

    @Unique
    private static NameableExecutor sparrow_chunkExecutor = null;

    @Unique
    private static boolean sparrow_executorLogged = false;

    @ModifyArg(
        method = "reload()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/render/chunk/ChunkBuilder.<init>(Lnet/minecraft/client/world/ClientWorld;Lnet/minecraft/client/render/WorldRenderer;Lnet/minecraft/util/thread/NameableExecutor;Lnet/minecraft/client/render/BufferBuilderStorage;Lnet/minecraft/client/render/block/BlockRenderManager;Lnet/minecraft/client/render/block/entity/BlockEntityRenderManager;)V",
            ordinal = 0
        ),
        index = 2
    )
    private NameableExecutor sparrow_useDedicatedChunkExecutor(NameableExecutor original) {
        if (sparrow_chunkExecutor == null) {
            int threadCount = Math.max(2, Runtime.getRuntime().availableProcessors() / 2);
            sparrow_chunkExecutor = new NameableExecutor(
                Executors.newFixedThreadPool(threadCount, new ThreadFactory() {
                    private final AtomicInteger count = new AtomicInteger(1);

                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "Sparrow-Chunk-Builder-" + count.getAndIncrement());
                        t.setDaemon(true);
                        t.setPriority(Thread.NORM_PRIORITY);
                        return t;
                    }
                })
            );
            if (!sparrow_executorLogged) {
                sparrow_executorLogged = true;
                SparrowLogger.debug("ChunkExecutorPoolMixin: dedicated " + threadCount + "-thread pool for chunk building");
            }
        }
        return sparrow_chunkExecutor;
    }
}
