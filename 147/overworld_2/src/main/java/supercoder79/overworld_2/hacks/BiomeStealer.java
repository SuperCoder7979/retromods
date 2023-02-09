package supercoder79.overworld_2.hacks;

import net.minecraft.src.BiomeGenBase;

public class BiomeStealer {
    public static boolean invocated = false;
    public static BiomeGenBase[] stolen = null;

    public static boolean isDefault(BiomeGenBase[] values) {
        return values.length == 7 &&
                values[0] == BiomeGenBase.desert &&
                values[1] == BiomeGenBase.forest &&
                values[2] == BiomeGenBase.extremeHills &&
                values[3] == BiomeGenBase.swampland &&
                values[4] == BiomeGenBase.plains &&
                values[5] == BiomeGenBase.taiga &&
                values[6] == BiomeGenBase.jungle;
    }
}
