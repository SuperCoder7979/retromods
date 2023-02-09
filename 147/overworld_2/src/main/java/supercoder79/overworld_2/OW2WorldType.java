package supercoder79.overworld_2;

import net.minecraft.src.IChunkProvider;
import net.minecraft.src.World;
import net.minecraft.src.WorldType;

public class OW2WorldType extends WorldType {
    public static WorldType INSTANCE = null;

    OW2WorldType() {
        super(13, "overworld_2");

        // :see_no_evil:
//        if (INSTANCE != null) {
//            throw new RuntimeException("How did you manage to make two of me?");
//        }

        INSTANCE = this;
    }

    // @Override when forge is installed!
    public IChunkProvider getChunkGenerator(World world, String generatorOptions) {
        return new ChunkProviderOverworldTwo(world);
    }

    @Override
    public String getTranslateName() {
        return "Overworld -2";
    }
}
