package supercoder79.overworld_2.noise;

import java.util.Random;

public final class Perlin {
    protected static final int[][] GRADIENT = new int[][]{
            {1, 1, 0},
            {-1, 1, 0},
            {1, -1, 0},
            {-1, -1, 0},
            {1, 0, 1},
            {-1, 0, 1},
            {1, 0, -1},
            {-1, 0, -1},
            {0, 1, 1},
            {0, -1, 1},
            {0, 1, -1},
            {0, -1, -1},
            {1, 1, 0},
            {0, -1, 1},
            {-1, 1, 0},
            {0, -1, -1}
    };
    
    private static final int[] GRADIENTS = new int[GRADIENT.length * 3];

    static {
        for (int i = 0; i < GRADIENT.length; i++) {
            int[] grad = GRADIENT[i];
            GRADIENTS[(i * 3)] = grad[0];
            GRADIENTS[(i * 3) + 1] = grad[1];
            GRADIENTS[(i * 3) + 2] = grad[2];
        }
    }

    public static int[] initPermutationTable(Random random) {
        int[] permutations = new int[256];
        for (int i = 0; i < 256; ++i) {
            permutations[i] = i;
        }

        for (int i = 0; i < 256; ++i) {
            int offset = random.nextInt(256 - i);
            int swap = permutations[i];
            permutations[i] = permutations[i + offset];
            permutations[i + offset] = swap;
        }

        return permutations;
    }

    public static double grad(int hash, double x, double y, double z) {
        int grad = hash & 15;
        return GRADIENTS[(grad * 3)] * x + GRADIENTS[(grad * 3) + 1] * y + GRADIENTS[(grad * 3) + 2] * z;
    }
}