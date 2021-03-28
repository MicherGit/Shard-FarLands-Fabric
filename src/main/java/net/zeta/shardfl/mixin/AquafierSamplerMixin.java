package net.zeta.shardfl.mixin;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.world.gen.AquiferSampler;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.NoiseColumnSampler;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Arrays;

@Mixin(AquiferSampler.class)
public class AquafierSamplerMixin {
    @Shadow
    private final DoublePerlinNoiseSampler edgeDensityNoise;
    private final DoublePerlinNoiseSampler waterLevelNoise;
    private final ChunkGeneratorSettings settings;
    private final int[] waterLevels;
    private final long[] blockPositions;
    private double densityAddition;
    private int waterLevel;
    private boolean needsFluidTick;
    private final NoiseColumnSampler columnSampler;
    private final int startX;
    private final int startY;
    private final int startZ;
    private final int sizeX;
    private final int sizeZ;


    public AquafierSamplerMixin(int x, int z, DoublePerlinNoiseSampler edgeDensityNoise, DoublePerlinNoiseSampler waterLevelNoise, ChunkGeneratorSettings settings, NoiseColumnSampler columnSampler, int height) {
        this.edgeDensityNoise = edgeDensityNoise;
        this.waterLevelNoise = waterLevelNoise;
        this.settings = settings;
        this.columnSampler = columnSampler;
        ChunkPos chunkPos = new ChunkPos(x, z);
        this.startX = this.getLocalX(chunkPos.getStartX()) - 1;
        int i = this.getLocalX(chunkPos.getEndX()) + 1;
        this.sizeX = i - this.startX + 1;
        int j = settings.getGenerationShapeConfig().getMinimumY();
        this.startY = this.getLocalY(j) - 1;
        int k = this.getLocalY(j + height) + 1;
        int l = k - this.startY + 1;
        this.startZ = this.getLocalZ(chunkPos.getStartZ()) - 1;
        int m = this.getLocalZ(chunkPos.getEndZ()) + 1;
        this.sizeZ = m - this.startZ + 1;
        int n = this.sizeX * l * this.sizeZ;
        this.waterLevels = new int[n];
        Arrays.fill(this.waterLevels, 2147483647);
        this.blockPositions = new long[n];
        Arrays.fill(this.blockPositions, 9223372036854775807L);
    }

    @Overwrite
    private int index(int x, int y, int z) {
        int i = x - this.startX;
        int j = y - this.startY;
        int k = z - this.startZ;
        return (j * this.sizeZ + k) * this.sizeX + i;
    }
    @Overwrite
    protected void apply(int x, int y, int z) {
        int i = Math.floorDiv(x - 5, 16);
        int j = Math.floorDiv(y + 1, 12);
        int k = Math.floorDiv(z - 5, 16);
        int l = 2147483647;
        int m = 2147483647;
        int n = 2147483647;
        long o = 0L;
        long p = 0L;
        long q = 0L;

        int ai;
        int aj;
        int ak;
        for(ai = 0; ai <= 1; ++ai) {
            for(aj = -1; aj <= 1; ++aj) {
                for(ak = 0; ak <= 1; ++ak) {
                    int u = i + ai;
                    int v = j + aj;
                    int w = k + ak;
                    int aa = this.index(u, v, w);
                    long ab = this.blockPositions[aa];
                    long ad;
                    if (ab != 9223372036854775807L) {
                        ad = ab;
                    } else {
                        ChunkRandom chunkRandom = new ChunkRandom(MathHelper.hashCode(u, v * 3, w) + 1L);
                        ad = BlockPos.asLong(u * 16 + chunkRandom.nextInt(10), v * 12 + chunkRandom.nextInt(9), w * 16 + chunkRandom.nextInt(10));
                        this.blockPositions[aa] = ad;
                    }

                    int ae = BlockPos.unpackLongX(ad) - x;
                    int af = BlockPos.unpackLongY(ad) - y;
                    int ag = BlockPos.unpackLongZ(ad) - z;
                    int ah = ae * ae + af * af + ag * ag;
                    if (l >= ah) {
                        q = p;
                        p = o;
                        o = ad;
                        n = m;
                        m = l;
                        l = ah;
                    } else if (m >= ah) {
                        q = p;
                        p = ad;
                        n = m;
                        m = ah;
                    } else if (n >= ah) {
                        q = ad;
                        n = ah;
                    }
                }
            }
        }

        ai = this.getWaterLevel(o);
        aj = this.getWaterLevel(p);
        ak = this.getWaterLevel(q);
        double d = this.maxDistance(l, m);
        double e = this.maxDistance(l, n);
        double f = this.maxDistance(m, n);
        this.waterLevel = ai;
        this.needsFluidTick = d > 0.0D;
        if (this.waterLevel >= y && y - this.settings.getGenerationShapeConfig().getMinimumY() <= 9) {
            this.densityAddition = 1.0D;
        } else if (d > -1.0D) {
            double g = 1.0D + (this.edgeDensityNoise.sample((float)x, (float)y, (float)z) + 0.1D) / 4.0D;
            double h = this.calculateDensity(y, g, ai, aj);
            double al = this.calculateDensity(y, g, ai, ak);
            double am = this.calculateDensity(y, g, aj, ak);
            double an = Math.max(0.0D, d);
            double ao = Math.max(0.0D, e);
            double ap = Math.max(0.0D, f);
            double aq = 2.0D * an * Math.max(h, Math.max(al * ao, am * ap));
            this.densityAddition = Math.max(0.0D, aq);
        } else {
            this.densityAddition = 0.0D;
        }

    }
    @Overwrite
    private double maxDistance(int a, int b) {
        double d = 25.0D;
        return 1.0D - (float)Math.abs(b - a) / 25.0D;
    }
    @Overwrite
    private double calculateDensity(int y, double noise, int a, int b) {
        return 0.5D * (float)Math.abs(a - b) * noise - Math.abs(0.5D * (float)(a + b) - (float)y - 0.5D);
    }
@Overwrite
    private int getLocalX(int x) {
        return Math.floorDiv(x, 16);
    }
@Overwrite
    private int getLocalY(int y) {
        return Math.floorDiv(y, 12);
    }
@Overwrite
    private int getLocalZ(int z) {
        return Math.floorDiv(z, 16);
    }
@Overwrite
    private int getWaterLevel(long pos) {
        int i = BlockPos.unpackLongX(pos);
        int j = BlockPos.unpackLongY(pos);
        int k = BlockPos.unpackLongZ(pos);
        int l = this.getLocalX(i);
        int m = this.getLocalY(j);
        int n = this.getLocalZ(k);
        int o = this.index(l, m, n);
        int p = this.waterLevels[o];
        if (p != 2147483647) {
            return p;
        } else {
            int q = this.getWaterLevel(i, j, k);
            this.waterLevels[o] = q;
            return q;
        }
    }
@Overwrite
    private int getWaterLevel(int x, int y, int z) {
        int i = this.settings.getSeaLevel();
        if (y > 30) {
            return i;
        } else {
            boolean j = true;
            boolean k = true;
            boolean l = true;
            double d = this.waterLevelNoise.sample((float)Math.floorDiv(x, 64), (float)Math.floorDiv(y, 40) / 1.4D, (float)Math.floorDiv(z, 64)) * 30.0D + -10.0D;
            if (Math.abs(d) > 8.0D) {
                d *= 4.0D;
            }

            int m = Math.floorDiv(y, 40) * 40 + 20;
            int n = m + MathHelper.floor(d);
            return Math.min(56, n);
        }
    }
@Overwrite
    public int getWaterLevel() {
        return this.waterLevel;
    }
@Overwrite
    public double getDensityAddition() {
        return this.densityAddition;
    }
@Overwrite
    public boolean needsFluidTick() {
        return this.needsFluidTick;
    }
}
