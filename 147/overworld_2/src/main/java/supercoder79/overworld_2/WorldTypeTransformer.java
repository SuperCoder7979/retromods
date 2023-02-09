package supercoder79.overworld_2;

import net.minecraft.src.BiomeGenBase;
import net.minecraft.src.WorldType;
import nilloader.api.lib.mini.MiniTransformer;
import nilloader.api.lib.mini.PatchContext;
import nilloader.api.lib.mini.annotation.Patch;
import supercoder79.overworld_2.hacks.BiomeStealer;

import java.lang.reflect.Field;

@Patch.Class("net.minecraft.src.WorldType")
public class WorldTypeTransformer extends MiniTransformer {
	
	@Patch.Method("<clinit>()V")
	public void patchClinit(PatchContext ctx) {
		ctx.jumpToLastReturn(); // Equivalent to "TAIL" in Mixin
		
		ctx.add(
			INVOKESTATIC("supercoder79/overworld_2/WorldTypeTransformer$Hooks", "onClinit", "()V")
		);
	}

	// Only needed on forge
	@Patch.Method("getBiomesForWorldType()[Lnet/minecraft/src/BiomeGenBase;")
	@Patch.Method.AffectsControlFlow
	@Patch.Method.Optional
	public void patchGetBiomes(PatchContext ctx) {
		ctx.jumpToStart();

		ctx.add(
				ALOAD(0),
				ALOAD(0),
				GETFIELD("net/minecraft/src/WorldType", "biomesForWorldType", "[Lnet/minecraft/src/BiomeGenBase;"),
				INVOKESTATIC("supercoder79/overworld_2/WorldTypeTransformer$Hooks", "onGetBiomes", "(Lnet/minecraft/src/WorldType;[Lnet/minecraft/src/BiomeGenBase;)[Lnet/minecraft/src/BiomeGenBase;"),
				ARETURN()
		);
	}
	
	public static class Hooks {

		// This is needed to store a strong reference to the world type
		public static void onClinit() {
			new OW2WorldType();
		}

		// FIXME: in an ideal world none of this should exist at all
		// This hacks around EBXL not registering into our world type
		public static BiomeGenBase[] onGetBiomes(WorldType type, BiomeGenBase[] biomes) {
			if (!BiomeStealer.invocated) {
				if (type == OW2WorldType.INSTANCE) {
					try {
						// Can't reinvoke method, as that would recurse onto this hook
						Field biomesForWorldType = WorldType.DEFAULT.getClass().getDeclaredField("biomesForWorldType");
						biomesForWorldType.setAccessible(true);
						biomes = (BiomeGenBase[]) biomesForWorldType.get(WorldType.DEFAULT);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
				}
			}

			if (type == WorldType.DEFAULT && BiomeStealer.stolen == null && !BiomeStealer.isDefault(biomes)) {
				BiomeStealer.stolen = biomes.clone();
			}

			if (BiomeStealer.isDefault(biomes) && BiomeStealer.stolen != null && !BiomeStealer.isDefault(BiomeStealer.stolen)) {
				biomes = BiomeStealer.stolen.clone();
			}

			BiomeStealer.invocated = true;

			return biomes;
		}
		
	}
	
}
