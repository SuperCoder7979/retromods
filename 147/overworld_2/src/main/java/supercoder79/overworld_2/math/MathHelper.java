package supercoder79.overworld_2.math;

public class MathHelper {
    public static double lerp(double delta, double start, double end) {
        return start + delta * (end - start);
    }
}
