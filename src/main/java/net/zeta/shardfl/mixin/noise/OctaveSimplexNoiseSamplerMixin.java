package net.zeta.shardfl.mixin.noise;

import it.unimi.dsi.fastutil.ints.IntSortedSet;
import net.minecraft.util.math.noise.OctaveSimplexNoiseSampler;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.world.gen.ChunkRandom;
import net.minecraft.world.gen.WorldGenRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(OctaveSimplexNoiseSampler.class)
public class OctaveSimplexNoiseSamplerMixin {
    @Shadow
    private final SimplexNoiseSampler[] octaveSamplers;
    private final double persistence;
    private final double lacunarity;

    private OctaveSimplexNoiseSamplerMixin(WorldGenRandom random, IntSortedSet octaves) {
        if (octaves.isEmpty()) {
            throw new IllegalArgumentException("Need some octaves!");
        } else {
            int i = -octaves.firstInt();
            int j = octaves.lastInt();
            int k = i + j + 1;
            if (k < 1) {
                throw new IllegalArgumentException("Total number of octaves needs to be >= 1");
            } else {
                SimplexNoiseSampler simplexNoiseSampler = new SimplexNoiseSampler(random);
                int l = j;
                this.octaveSamplers = new SimplexNoiseSampler[k];
                if (j >= 0 && j < k && octaves.contains(0)) {
                    this.octaveSamplers[j] = simplexNoiseSampler;
                }

                for(int m = j + 1; m < k; ++m) {
                    if (m >= 0 && octaves.contains(l - m)) {
                        this.octaveSamplers[m] = new SimplexNoiseSampler(random);
                    } else {
                        random.skip(262);
                    }
                }

                if (j > 0) {
                    long n = (long)(simplexNoiseSampler.sample(simplexNoiseSampler.originX, simplexNoiseSampler.originY, simplexNoiseSampler.originZ) * 9.223372036854776E18D);
                    WorldGenRandom worldGenRandom = new ChunkRandom(n);

                    for(int o = l - 1; o >= 0; --o) {
                        if (o < k && octaves.contains(l - o)) {
                            this.octaveSamplers[o] = new SimplexNoiseSampler(worldGenRandom);
                        } else {
                            worldGenRandom.skip(262);
                        }
                    }
                }

                this.lacunarity = Math.pow(2.0D, (float)j);
                this.persistence = 1.0D / (Math.pow(2.0D, (float)k) - 1.0D);
            }
        }
    }
}
