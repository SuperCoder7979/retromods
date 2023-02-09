package supercoder79.overworld_2.noise;

import java.util.Random;

public final class PerlinNoise implements Noise {
    public static final NoiseRange RANGE = NoiseRange.NORMAL;

    protected final double originX;
    protected final double originY;
    protected final double originZ;
    protected final int[] permutations;

    protected PerlinNoise(Random random) {
        this.originX = random.nextDouble() * 256.0;
        this.originY = random.nextDouble() * 256.0;
        this.originZ = random.nextDouble() * 256.0;

        this.permutations = Perlin.initPermutationTable(random);
    }

    public static NoiseFactory create() {
        return (seed) -> new PerlinNoise(new Random(seed));
    }

    @Override
    public double get(double x, double y, double z) {
        x = Noise.maintainPrecision(x + this.originX);
        y = Noise.maintainPrecision(y + this.originY);
        z = Noise.maintainPrecision(z + this.originZ);

        int ox = floor(x);
        int oy = floor(y);
        int oz = floor(z);

        double ix = x - ox;
        double iy = y - oy;
        double iz = z - oz;

        int prf = this.permute(ox) + oy;
        int prtf = this.permute(prf) + oz;
        int prbf = this.permute(prf + 1) + oz;
        int plf = this.permute(ox + 1) + oy;
        int pltf = this.permute(plf) + oz;
        int plbf = this.permute(plf + 1) + oz;

        double rtf = Perlin.grad(this.permute(prtf), ix, iy, iz);
        double ltf = Perlin.grad(this.permute(pltf), ix - 1.0, iy, iz);
        double rbf = Perlin.grad(this.permute(prbf), ix, iy - 1.0, iz);
        double lbf = Perlin.grad(this.permute(plbf), ix - 1.0, iy - 1.0, iz);
        double rtb = Perlin.grad(this.permute(prtf + 1), ix, iy, iz - 1.0);
        double ltb = Perlin.grad(this.permute(pltf + 1), ix - 1.0, iy, iz - 1.0);
        double rbb = Perlin.grad(this.permute(prbf + 1), ix, iy - 1.0, iz - 1.0);
        double lbb = Perlin.grad(this.permute(plbf + 1), ix - 1.0, iy - 1.0, iz - 1.0);

        return lerp3(
                perlinFade(ix),
                perlinFade(iy),
                perlinFade(iz),
                rtf, ltf, rbf, lbf,
                rtb, ltb, rbb, lbb
        );
    }

    private int permute(int x) {
        return this.permutations[x & 255] & 255;
    }

    @Override
    public NoiseRange getRange() {
        return RANGE;
    }

    public static int floor(double d) {
        int i = (int)d;
        return d < (double)i ? i - 1 : i;
    }

    public static double perlinFade(double d) {
        return d * d * d * (d * (d * 6.0 - 15.0) + 10.0);
    }

    public static double lerp(double d, double e, double f) {
        return e + d * (f - e);
    }

    public static double lerp2(double d, double e, double f, double g, double h, double i) {
        return lerp(e, lerp(d, f, g), lerp(d, h, i));
    }

    public static double lerp3(double d, double e, double f, double g, double h, double i, double j, double k, double l, double m, double n) {
        return lerp(f, lerp2(d, e, g, h, i, j), lerp2(d, e, k, l, m, n));
    }
}