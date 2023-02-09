package supercoder79.overworld_2;

import net.minecraft.src.BiomeCache;
import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.ChunkProviderGenerate;
import net.minecraft.src.World;
import supercoder79.overworld_2.gen.NoiseProvider;
import supercoder79.overworld_2.math.MathHelper;
import supercoder79.overworld_2.noise.*;

import java.util.Random;

public class ChunkProviderOverworldTwo extends ChunkProviderGenerate {
    private final Noise[] surfaceNoise;
    private final Noise tearNoise;
    private final Noise extraDensityNoise;

    private final int noiseSizeY = 16;

    public ChunkProviderOverworldTwo(World world) {
        super(world, world.getSeed(), world.getWorldInfo().isMapFeaturesEnabled());

        Random random = new Random(world.getSeed());

        this.surfaceNoise = new Noise[] {
                surfaceNoise().create(random.nextLong()),
                surfaceNoise().create(random.nextLong())
        };

        this.tearNoise = tearNoise().create(random.nextLong());

        this.extraDensityNoise = extraDensityNoise().create(random.nextLong());

        this.parabolicField = new float[25];

        for(int var8 = -2; var8 <= 2; ++var8) {
            for(int var9 = -2; var9 <= 2; ++var9) {
                float var10 = 10.0F / net.minecraft.src.MathHelper.sqrt_float((float)(var8 * var8 + var9 * var9) + 0.2F);
                this.parabolicField[var8 + 2 + (var9 + 2) * 5] = var10;
            }
        }
    }

    @Override
    public void generateTerrain(int chunkX, int chunkZ, byte[] blocks) {
        Random random = new Random();
        double[] noise = new double[5 * 5 * 17];
        this.biomesForGeneration = this.worldObj.getWorldChunkManager().getBiomesForGeneration(this.biomesForGeneration, chunkX * 4 - 2, chunkZ * 4 - 2, 10, 10);

        for (int x = 0; x < 5; x++) {
            for (int z = 0; z < 5; z++) {
                fillNoiseColumn(noise, chunkX * 4 + x, chunkZ * 4 + z, x, z);
            }
        }

        for (int noiseX = 0; noiseX < 4; noiseX++) {
            for (int noiseZ = 0; noiseZ < 4; noiseZ++) {
                for (int noiseY = 15; noiseY >= 0; noiseY--) {

                    double x0y0z0 = noise[NoiseProvider.index(noiseX, noiseY, noiseZ)];
                    double x1y0z0 = noise[NoiseProvider.index(noiseX + 1, noiseY, noiseZ)];
                    double x0y0z1 = noise[NoiseProvider.index(noiseX, noiseY, noiseZ + 1)];
                    double x1y0z1 = noise[NoiseProvider.index(noiseX + 1, noiseY, noiseZ + 1)];

                    double x0y1z0 = noise[NoiseProvider.index(noiseX, noiseY + 1, noiseZ)];
                    double x1y1z0 = noise[NoiseProvider.index(noiseX + 1, noiseY + 1, noiseZ)];
                    double x0y1z1 = noise[NoiseProvider.index(noiseX, noiseY + 1, noiseZ + 1)];
                    double x1y1z1 = noise[NoiseProvider.index(noiseX + 1, noiseY + 1, noiseZ + 1)];

                    for (int pieceY = 0; pieceY < 8; pieceY++) {
                        double dy = pieceY / 8.0;
                        int ry = noiseY * 8 + pieceY;

                        double x0z0 = MathHelper.lerp(dy, x0y0z0, x0y1z0);
                        double x1z0 = MathHelper.lerp(dy, x1y0z0, x1y1z0);
                        double x0z1 = MathHelper.lerp(dy, x0y0z1, x0y1z1);
                        double x1z1 = MathHelper.lerp(dy, x1y0z1, x1y1z1);

                        for (int pieceX = 0; pieceX < 4; pieceX++) {
                            double dx = pieceX / 4.0;
                            int rx = noiseX * 4 + pieceX;

                            double z0 = MathHelper.lerp(dx, x0z0, x1z0);
                            double z1 = MathHelper.lerp(dx, x0z1, x1z1);

                            for (int pieceZ = 0; pieceZ < 4; pieceZ++) {
                                double dz = pieceZ / 4.0;
                                int rz = noiseZ * 4 + pieceZ;

                                double value = MathHelper.lerp(dz, z0, z1);

                                // 1.4 only supports gensize of 16
                                int index = rx << 11 | rz << 7 | ry;

                                if (value > 0.0) {
                                    blocks[index] = (byte)1;
                                } else if (ry < 63) {
                                    blocks[index] = (byte)9;
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private static NoiseFactory surfaceNoise() {
        OctaveNoise.Builder octaves = OctaveNoise.builder()
                .setHorizontalFrequency(1.0 / 24.0)
                .setVerticalFrequency(1.0 / 24.0)
                .setLacunarity(1.7)
                .setPersistence(1.0 / 1.8);

        octaves.add(PerlinNoise.create(), 6);

        return NormalizedNoise.of(octaves.build());
    }

    private static NoiseFactory tearNoise() {
        OctaveNoise.Builder octaves = OctaveNoise.builder()
                .setHorizontalFrequency(1.0 / 40.0)
                .setVerticalFrequency(1.0 / 18.0)
                .setLacunarity(1.35)
                .setPersistence(1.0 / 2.0);

        octaves.add(PerlinNoise.create(), 4);

        return NormalizedNoise.of(octaves.build());
    }

    private static NoiseFactory extraDensityNoise() {
        OctaveNoise.Builder octaves = OctaveNoise.builder()
                .setHorizontalFrequency(1.0 / 150.0)
                .setVerticalFrequency(1.0 / 150.0)
                .setLacunarity(1.5)
                .setPersistence(1.0 / 1.4);

        octaves.add(PerlinNoise.create(), 4);

        return NormalizedNoise.of(octaves.build());
    }

    private static float improveDepth(float depth) {
        return depth < 0.0 ? depth * 1.35f : depth;
    }

    private SurfaceParameters sampleSurfaceParameters(int x, int z) {
        float totalScale = 0.0F;
        float totalDepth = 0.0F;
        float totalWeight = 0.0F;

        float depthHere = improveDepth(biomesForGeneration[x + 2 + (z + 2) * (5 + 5)].minHeight);

        for (int oz = -2; oz <= 2; oz++) {
            for (int ox = -2; ox <= 2; ox++) {
                BiomeGenBase biome = biomesForGeneration[x + ox + 2 + (z + oz + 2) * (5 + 5)];
                float depth = improveDepth(biome.minHeight);
                float scale = biome.maxHeight;

                float weight =  this.parabolicField[ox + 2 + (oz + 2) * 5] / (depth + 2.0F);
                if (depth > depthHere) {
                    weight *= 0.5F;
                }

                totalScale += scale * weight;
                totalDepth += depth * weight;
                totalWeight += weight;
            }
        }

        float depth = totalDepth / totalWeight;
        float scale = totalScale / totalWeight;

        return new SurfaceParameters((depth * 0.5F) - 0.125F, (scale * 0.9F) + 0.1F);
    }

    private void fillNoiseColumn(double[] buffer, int x, int z, int ix, int iz) {
        SurfaceParameters params = sampleSurfaceParameters(ix, iz);
        double scaledDepth = params.depth * (0.265625D * 1.15);
        double scaledScale = 96.0D / params.scale;

        double topTarget = -10;
        double topSize = 3;
        double topOffset = 0;
//        double bottomTarget = noiseConfig.getBottomSlide().getTarget();
//        double bottomSize = noiseConfig.getBottomSlide().getSize();
//        double bottomOffset = noiseConfig.getBottomSlide().getOffset();
//        double randomDensityOffset = noiseConfig.hasRandomDensityOffset() ? this.extraDensityNoiseAt(x, z) : 0.0D;
        double randomDensityOffset = this.extraDensityNoiseAt(x, z);
        double densityFactor = 1.0;
        double densityOffset = (15.0 / (128.0 / 2.0));

        for(int y = 0; y <= this.noiseSizeY; ++y) {
            double noise = this.getNoiseAt(x, y, z);
            double yOffset = 1.0D - (double) y * 2.0D / (double)this.noiseSizeY + randomDensityOffset;
            double density = yOffset * densityFactor + (densityOffset * 0.2);
            double falloff = (density + scaledDepth) * scaledScale;

            if (falloff > 0.0D) {
                noise += falloff * 4.0D;
            } else {
                noise += falloff;
            }

//            double slide;
//            if (topSize > 0.0D) {
//                slide = ((double)(this.noiseSizeY - y) - topOffset) / topSize;
//                noise = clampedLerp(topTarget, noise, slide);
//            }

//            if (bottomSize > 0.0D) {
//                slide = ((double) y - bottomOffset) / bottomSize;
//                noise = clampedLerp(bottomTarget, noise, slide);
//            }

            buffer[NoiseProvider.index(ix, y, iz)] = noise;
        }
    }

    private double getNoiseAt(int x, int y, int z) {
        double tearNoise = this.tearNoise.get(x, y, z) * 15.0;
        double surfaceNoise;

        if (tearNoise <= 0.0) {
            surfaceNoise = this.surfaceNoise[0].get(x, y, z);
        } else if (tearNoise >= 1.0) {
            surfaceNoise = this.surfaceNoise[1].get(x, y, z);
        } else {
            double left = this.surfaceNoise[0].get(x, y, z);
            double right = this.surfaceNoise[1].get(x, y, z);
            surfaceNoise = MathHelper.lerp(tearNoise, left, right);
        }

        return surfaceNoise * 200;
    }

    protected double extraDensityNoiseAt(int x, int z) {
        double rawDensity = this.extraDensityNoise.get(x, 10.0D, z);
        double scaledDensity;
        if (rawDensity < 0.0D) {
            scaledDensity = -rawDensity * 0.3D;
        } else {
            scaledDensity = rawDensity;
        }

        double finalDensity = scaledDensity * 24.575625D - 2.0D;
        return finalDensity < 0.0D ? finalDensity * 0.009486607142857142D : Math.min(finalDensity, 1.0D) * 0.006640625D;
    }

    public static double clampedLerp(double start, double end, double value) {
        if (value < 0.0D) {
            return start;
        } else {
            return value > 1.0D ? end : PerlinNoise.lerp(value, start, end);
        }
    }

    private static class SurfaceParameters {
        private final float depth;
        private final float scale;

        public SurfaceParameters(float depth, float scale) {
            this.depth = depth;
            this.scale = scale;
        }
    }

    @Override
    public String makeString() {
        return "OverworldTwoLevelSource";
    }
}
