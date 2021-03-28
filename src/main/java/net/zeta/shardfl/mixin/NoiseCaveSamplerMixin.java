package net.zeta.shardfl.mixin;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.NoiseCaveSampler;
import net.minecraft.world.gen.NoiseHelper;
import net.minecraft.world.gen.SimpleRandom;
import net.minecraft.world.gen.WorldGenRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(NoiseCaveSampler.class)
public class NoiseCaveSamplerMixin {
    @Shadow
    private final int minY;
    private final DoublePerlinNoiseSampler terrainAdditionNoise;
    private final DoublePerlinNoiseSampler pillarNoise;
    private final DoublePerlinNoiseSampler pillarFalloffNoise;
    private final DoublePerlinNoiseSampler pillarScaleNoise;
    private final DoublePerlinNoiseSampler scaledCaveScaleNoise;
    private final DoublePerlinNoiseSampler horizontalCaveNoise;
    private final DoublePerlinNoiseSampler caveScaleNoise;
    private final DoublePerlinNoiseSampler caveFalloffNoise;
    private final DoublePerlinNoiseSampler tunnelNoise1;
    private final DoublePerlinNoiseSampler tunnelNoise2;
    private final DoublePerlinNoiseSampler tunnelScaleNoise;
    private final DoublePerlinNoiseSampler tunnelFalloffNoise;
    private final DoublePerlinNoiseSampler offsetNoise;
    private final DoublePerlinNoiseSampler offsetScaleNoise;
    private final DoublePerlinNoiseSampler field_28842;
    private final DoublePerlinNoiseSampler caveDensityNoise;

    public NoiseCaveSamplerMixin(WorldGenRandom random, int minY) {
        this.minY = minY;
        this.pillarNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, new double[]{1.0D, 1.0D});
        this.pillarFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.pillarScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.scaledCaveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, new double[]{1.0D});
        this.horizontalCaveNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.caveScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, new double[]{1.0D});
        this.caveFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, new double[]{1.0D});
        this.tunnelNoise1 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, new double[]{1.0D});
        this.tunnelNoise2 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -7, new double[]{1.0D});
        this.tunnelScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -11, new double[]{1.0D});
        this.tunnelFalloffNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.offsetNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -5, new double[]{1.0D});
        this.offsetScaleNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.field_28842 = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D, 1.0D, 1.0D});
        this.terrainAdditionNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -8, new double[]{1.0D});
        this.caveDensityNoise = DoublePerlinNoiseSampler.create(new SimpleRandom(random.nextLong()), -6, new double[]{1.0D, 1.0D, 0.0D, 1.0D, 0.0D, 1.0D, 1.0D});
    }

    public double sample(int x, int y, int z, double offset) {
        boolean bl = offset < 170.0D;
        double d = this.getTunnelOffsetNoise(x, y, z);
        double e = this.getTunnelNoise(x, y, z);
        if (bl) {
            return Math.min(offset, (e + d) * 128.0D * 5.0D);
        } else {
            double f = this.caveDensityNoise.sample((float)x, (float)y, (float)z);
            double g = MathHelper.clamp(f + 0.25D, -1.0D, 1.0D);
            double h = (offset - 170.0D) / 100.0D;
            double i = g + MathHelper.clampedLerp(0.5D, 0.0D, h);
            double j = this.getTerrainAdditionNoise(x, y, z);
            double k = this.getCaveNoise(x, y, z);
            double l = i + j;
            double m = Math.min(l, Math.min(e, k) + d);
            double n = Math.max(m, this.getPillarNoise(x, y, z));
            return 128.0D * MathHelper.clamp(n, -1.0D, 1.0D);
        }
    }

    private double getPillarNoise(int x, int y, int z) {
        double d = 0.0D;
        double e = 2.0D;
        double f = NoiseHelper.lerpFromProgress(this.pillarFalloffNoise, (float)x, (float)y, (float)z, 0.0D, 2.0D);
        boolean i = false;
        boolean j = true;
        double g = NoiseHelper.lerpFromProgress(this.pillarScaleNoise, (float)x, (float)y, (float)z, 0.0D, 1.0D);
        g = Math.pow(g, 3.0D);
        double h = 25.0D;
        double k = 0.3D;
        double l = this.pillarNoise.sample((float)x * 25.0D, (float)y * 0.3D, (float)z * 25.0D);
        l = g * (l * 2.0D - f);
        return l > 0.02D ? l : -1.0D / 0.0;
    }

    private double getTerrainAdditionNoise(int x, int y, int z) {
        double d = this.terrainAdditionNoise.sample((float)x, (float)(y * 8), (float)z);
        return MathHelper.square(d) * 4.0D;
    }

    private double getTunnelNoise(int x, int y, int z) {
        double d = this.tunnelScaleNoise.sample((float)(x * 2), (float)y, (float)(z * 2));
        double e = NoiseCaveSamplerMixin.CaveScaler.scaleTunnels(d);
        double f = 0.065D;
        double g = 0.088D;
        double h = NoiseHelper.lerpFromProgress(this.tunnelFalloffNoise, (float)x, (float)y, (float)z, 0.065D, 0.088D);
        double i = sample(this.tunnelNoise1, (float)x, (float)y, (float)z, e);
        double j = Math.abs(e * i) - h;
        double k = sample(this.tunnelNoise2, (float)x, (float)y, (float)z, e);
        double l = Math.abs(e * k) - h;
        return clamp(Math.max(j, l));
    }

    private double getCaveNoise(int x, int y, int z) {
        double d = this.caveScaleNoise.sample((float)(x * 2), (float)y, (float)(z * 2));
        double e = NoiseCaveSamplerMixin.CaveScaler.scaleCaves(d);
        double f = 0.6D;
        double g = 1.3D;
        double h = NoiseHelper.lerpFromProgress(this.caveFalloffNoise, (float)(x * 2), (float)y, (float)(z * 2), 0.6D, 1.3D);
        double i = sample(this.scaledCaveScaleNoise, (float)x, (float)y, (float)z, e);
        double j = 0.083D;
        double k = Math.abs(e * i) - 0.083D * h;
        int l = this.minY;
        boolean m = true;
        double n = NoiseHelper.lerpFromProgress(this.horizontalCaveNoise, (float)x, 0.0D, (float)z, (float)l, 8.0D);
        double o = Math.abs(n - (float)y / 8.0D) - 1.0D * h;
        o = o * o * o;
        return clamp(Math.max(o, k));
    }

    private double getTunnelOffsetNoise(int x, int y, int z) {
        double d = NoiseHelper.lerpFromProgress(this.offsetScaleNoise, (float)x, (float)y, (float)z, 0.0D, 0.1D);
        return (0.4D - Math.abs(this.offsetNoise.sample((float)x, (float)y, (float)z))) * d;
    }

    private static double clamp(double value) {
        return MathHelper.clamp(value, -1.0D, 1.0D);
    }

    private static double sample(DoublePerlinNoiseSampler sampler, double x, double y, double z, double scale) {
        return sampler.sample(x / scale, y / scale, z / scale);
    }
    @Mixin(CaveScaler.class)
    static final class CaveScaler {
        @Overwrite
        private static double scaleCaves(double value) {
            if (value < -0.75D) {
                return 0.5D;
            } else if (value < -0.5D) {
                return 0.75D;
            } else if (value < 0.5D) {
                return 1.0D;
            } else {
                return value < 0.75D ? 2.0D : 3.0D;
            }
        }
        @Overwrite
        private static double scaleTunnels(double value) {
            if (value < -0.5D) {
                return 0.75D;
            } else if (value < 0.0D) {
                return 1.0D;
            } else {
                return value < 0.5D ? 1.5D : 2.0D;
            }
        }
    }
}
