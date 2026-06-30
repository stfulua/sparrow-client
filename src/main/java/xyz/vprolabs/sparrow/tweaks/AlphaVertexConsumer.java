package xyz.vprolabs.sparrow.tweaks;

import net.minecraft.client.render.VertexConsumer;

/**
 * Wraps a VertexConsumer and applies a fixed alpha multiplier to all color calls.
 * Used to make fluids (water/lava) 50% translucent.
 */
public class AlphaVertexConsumer implements VertexConsumer {

    private final VertexConsumer delegate;
    private final float alpha;

    public AlphaVertexConsumer(VertexConsumer delegate, float alpha) {
        this.delegate = delegate;
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer vertex(float x, float y, float z) {
        delegate.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int r, int g, int b, int a) {
        delegate.color(r, g, b, (int) (a * alpha));
        return this;
    }

    @Override
    public VertexConsumer color(int argb) {
        int a = (argb >> 24) & 0xFF;
        int newA = (int) (a * alpha);
        return delegate.color((argb & 0x00FFFFFF) | (newA << 24));
    }

    @Override
    public VertexConsumer color(float r, float g, float b, float a) {
        delegate.color(r, g, b, a * alpha);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        delegate.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        delegate.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v) {
        delegate.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        delegate.normal(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer lineWidth(float width) {
        delegate.lineWidth(width);
        return this;
    }
}
