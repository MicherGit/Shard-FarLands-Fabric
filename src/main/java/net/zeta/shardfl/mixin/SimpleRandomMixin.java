package net.zeta.shardfl.mixin;

import net.minecraft.util.thread.AtomicStack;
import net.minecraft.util.thread.LockHelper;
import net.minecraft.world.gen.SimpleRandom;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.concurrent.atomic.AtomicLong;

@Mixin(SimpleRandom.class)
public class SimpleRandomMixin {
    @Shadow
    private final AtomicLong seed = new AtomicLong();

    public SimpleRandomMixin(long seed) {
        this.setSeed(seed);
    }
@Overwrite
    public void setSeed(long seed) {
        if (!this.seed.compareAndSet(this.seed.get(), (seed ^ 25214903917L) & 281474976710655L)) {
            throw LockHelper.crash("SimpleRandomSource", (AtomicStack)null);
        }
    }
@Overwrite
    private int next(int bits) {
        long l = this.seed.get();
        long m = l * 25214903917L + 11L & 281474976710655L;
        if (!this.seed.compareAndSet(l, m)) {
            throw LockHelper.crash("SimpleRandomSource", (AtomicStack)null);
        } else {
            return (int)(m >> 48 - bits);
        }
    }
@Overwrite
    public int nextInt() {
        return this.next(32);
    }
@Overwrite
    public int nextInt(int i) {
        if (i <= 0) {
            throw new IllegalArgumentException("Bound must be positive");
        } else if ((i & i - 1) == 0) {
            return (int)((long)i * (long)this.next(31) >> 31);
        } else {
            int j;
            int k;
            do {
                j = this.next(31);
                k = j % i;
            } while(j - k + (i - 1) < 0);

            return k;
        }
    }
@Overwrite
    public long nextLong() {
        int i = this.next(32);
        int j = this.next(32);
        long l = (long)i << 32;
        return l + (long)j;
    }
@Overwrite
    public double nextDouble() {
        int i = this.next(26);
        int j = this.next(27);
        long l = ((long)i << 27) + (long)j;
        return (float)l * 1.1102230246251565E-16D;
    }
}
