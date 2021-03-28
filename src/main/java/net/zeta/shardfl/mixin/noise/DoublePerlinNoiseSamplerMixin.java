package net.zeta.shardfl.mixin.noise;

import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(DoublePerlinNoiseSampler.class)
public class DoublePerlinNoiseSamplerMixin {
    /**
     * @author Zeta
     */
    @Overwrite
    private static double createAmplitude(int octaves) {
        return 0.1D * (1.0D + 1.0D / (float)(octaves + 1));
    }
}
