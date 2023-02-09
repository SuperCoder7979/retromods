package supercoder79.overworld_2.gen;

public class NoiseProvider {

    public static int index(int x, int y, int z) {
        return (y * 5 * 5) + (x * 5) + z;
    }
}
