package net.zeta.shardfl.mixin.noise.farlands;

import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import net.minecraft.world.gen.WorldGenRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.function.LongFunction;

@Mixin(OctavePerlinNoiseSampler.class)
public class OctavePerlinNoiseSamplerMixin {
    @Shadow
    private final PerlinNoiseSampler[] octaveSamplers;
    private final DoubleList amplitudes;
    private final double persistence;
    private final double lacunarity;

    protected OctavePerlinNoiseSamplerMixin(WorldGenRandom worldGenRandom, Pair<Integer, DoubleList> pair, LongFunction<WorldGenRandom> longFunction) {
        int i = (Integer)pair.getFirst();
        this.amplitudes = (DoubleList)pair.getSecond();
        PerlinNoiseSampler perlinNoiseSampler = new PerlinNoiseSampler(worldGenRandom);
        int j = this.amplitudes.size();
        int k = -i;
        this.octaveSamplers = new PerlinNoiseSampler[j];
        if (k >= 0 && k < j) {
            double d = this.amplitudes.getDouble(k);
            if (d != 0.0D) {
                this.octaveSamplers[k] = perlinNoiseSampler;
            }
        }

        for(int l = k - 1; l >= 0; --l) {
            if (l < j) {
                double e = this.amplitudes.getDouble(l);
                if (e != 0.0D) {
                    this.octaveSamplers[l] = new PerlinNoiseSampler(worldGenRandom);
                } else {
                    method_34401(worldGenRandom);
                }
            } else {
                method_34401(worldGenRandom);
            }
        }

        if (k < j - 1) {
            throw new IllegalArgumentException("Positive octaves are temporarily disabled");
        } else {
            this.lacunarity = Math.pow(2.0D, (float)(-k));
            this.persistence = Math.pow(2.0D, (float)(j - 1)) / (Math.pow(2.0D, (float)j) - 1.0D);
        }
    }
    /**
     * @author Mojank fjnk
     */
    @Overwrite
    private static void method_34401(WorldGenRandom worldGenRandom) {
        worldGenRandom.skip(262);
    }

    /**
     * @author ZetaTheEliatrope
     */
    @Overwrite
    public static double maintainPrecision(double d) {
        return d
                //- (float) MathHelper.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D
        ;
    }
}
