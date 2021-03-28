package net.zeta.shardfl.mixin.noise;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.InterpolatedNoiseSampler;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import net.minecraft.util.math.noise.PerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(InterpolatedNoiseSampler.class)
public class InterpolatedNoiseSamplerMixin {
    @Shadow
    private OctavePerlinNoiseSampler lowerInterpolatedNoise;
    private OctavePerlinNoiseSampler upperInterpolatedNoise;
    private OctavePerlinNoiseSampler interpolationNoise;
    /**
     * @author Zeta
     */
    @Overwrite
    public double sample(int x, int y, int z, double horizontalScale, double verticalScale, double horizontalStretch, double verticalStretch) {
        double d = 0.0D;
        double e = 0.0D;
        double f = 0.0D;
        boolean bl = true;
        double g = 1.0D;

        for(int i = 0; i < 16; ++i) {
            double h = OctavePerlinNoiseSampler.maintainPrecision((float)x * horizontalScale * g);
            double j = OctavePerlinNoiseSampler.maintainPrecision((float)y * verticalScale * g);
            double k = OctavePerlinNoiseSampler.maintainPrecision((float)z * horizontalScale * g);
            double l = verticalScale * g;
            PerlinNoiseSampler perlinNoiseSampler = this.lowerInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler != null) {
                d += perlinNoiseSampler.sample(h, j, k, l, (float)y * l) / g;
            }

            PerlinNoiseSampler perlinNoiseSampler2 = this.upperInterpolatedNoise.getOctave(i);
            if (perlinNoiseSampler2 != null) {
                e += perlinNoiseSampler2.sample(h, j, k, l, (float)y * l) / g;
            }

            if (i < 8) {
                PerlinNoiseSampler perlinNoiseSampler3 = this.interpolationNoise.getOctave(i);
                if (perlinNoiseSampler3 != null) {
                    f += perlinNoiseSampler3.sample(OctavePerlinNoiseSampler.maintainPrecision((float)x * horizontalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((float)y * verticalStretch * g), OctavePerlinNoiseSampler.maintainPrecision((float)z * horizontalStretch * g), verticalStretch * g, (float)y * verticalStretch * g) / g;
                }
            }

            g /= 2.0D;
        }

        return MathHelper.clampedLerp(d / 512.0D, e / 512.0D, (f / 10.0D + 1.0D) / 2.0D);
    }
}
