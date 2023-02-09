package supercoder79.overworld_2;

import nilloader.api.lib.asm.tree.LabelNode;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;

// Needed for vanilla installs
@Patch.Class("net.minecraft.src.WorldProvider")
public class WorldProviderTransformer extends MiniTransformer {
	
	@Patch.Method("createChunkGenerator()Lnet/minecraft/src/IChunkProvider;")
	@Patch.Method.AffectsControlFlow
	public void patchClinit(PatchContext ctx) {
		ctx.jumpToStart();

		LabelNode nd = new LabelNode();
		ctx.add(
				GETSTATIC("supercoder79/overworld_2/OW2WorldType", "INSTANCE", "Lnet/minecraft/src/WorldType;"),
				ALOAD(0),
				GETFIELD("net/minecraft/src/WorldProvider", "terrainType", "Lnet/minecraft/src/WorldType;"),
				INVOKEVIRTUAL("net/minecraft/src/WorldType", "equals", "(Ljava/lang/Object;)Z"),
				IFEQ(nd),
				NEW("supercoder79/overworld_2/ChunkProviderOverworldTwo"),
				DUP(),
				ALOAD(0),
				GETFIELD("net/minecraft/src/WorldProvider", "worldObj", "Lnet/minecraft/src/World;"),
				INVOKESPECIAL("supercoder79/overworld_2/ChunkProviderOverworldTwo", "<init>", "(Lnet/minecraft/src/World;)V"),
				ARETURN(),
				nd
		);
	}
	
}
