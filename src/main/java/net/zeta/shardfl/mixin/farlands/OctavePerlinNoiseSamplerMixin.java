package net.zeta.shardfl.mixin.farlands;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.noise.OctavePerlinNoiseSampler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(OctavePerlinNoiseSampler.class)
public class OctavePerlinNoiseSamplerMixin {

    /**
     * @author ZetaTheEliatrope
     */
    @Overwrite
    public static double maintainPrecision(double d) {
        return d
                //- (double) MathHelper.lfloor(d / 3.3554432E7D + 0.5D) * 3.3554432E7D
        ;
    }
}
