package lotr.common.world.gen.tree;

import com.mojang.serialization.Codec;

import net.minecraft.world.gen.foliageplacer.FoliagePlacerType;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRFoliagePlacers {
	public static FoliagePlacerType SHIRE_PINE_FOLIAGE;
	public static FoliagePlacerType ASPEN_FOLIAGE;
	public static FoliagePlacerType LAIRELOSSE_FOLIAGE;
	public static FoliagePlacerType PINE_FOLIAGE;
	public static FoliagePlacerType FIR_FOLIAGE;
	public static FoliagePlacerType HOLLY_FOLIAGE;
	public static FoliagePlacerType CYPRESS_FOLIAGE;
	public static FoliagePlacerType EMPTY_FOLIAGE;
	public static FoliagePlacerType DESERT_FOLIAGE;
	public static FoliagePlacerType BOUGHS_FOLIAGE;
	public static FoliagePlacerType CEDAR_FOLIAGE;
	public static FoliagePlacerType MIRK_OAK_FOLIAGE;
	public static FoliagePlacerType CLUSTER_FOLIAGE;
	public static FoliagePlacerType CULUMALDA_FOLIAGE;
	public static final int MAX_LEAF_DISTANCE = 7;

	public static void register() {
		SHIRE_PINE_FOLIAGE = registerModded("shire_pine_foliage_placer", ShirePineFoliagePlacer.CODEC);
		ASPEN_FOLIAGE = registerModded("aspen_foliage_placer", AspenFoliagePlacer.CODEC);
		LAIRELOSSE_FOLIAGE = registerModded("lairelosse_foliage_placer", LairelosseFoliagePlacer.CODEC);
		PINE_FOLIAGE = registerModded("pine_foliage_placer", LOTRPineFoliagePlacer.CODEC);
		FIR_FOLIAGE = registerModded("fir_foliage_placer", FirFoliagePlacer.CODEC);
		HOLLY_FOLIAGE = registerModded("holly_foliage_placer", HollyFoliagePlacer.CODEC);
		CYPRESS_FOLIAGE = registerModded("cypress_foliage_placer", CypressFoliagePlacer.CODEC);
		EMPTY_FOLIAGE = registerModded("empty_foliage_placer", EmptyFoliagePlacer.CODEC);
		DESERT_FOLIAGE = registerModded("desert_foliage_placer", DesertFoliagePlacer.CODEC);
		BOUGHS_FOLIAGE = registerModded("boughs_foliage_placer", BoughsFoliagePlacer.CODEC);
		CEDAR_FOLIAGE = registerModded("cedar_foliage_placer", CedarFoliagePlacer.CODEC);
		MIRK_OAK_FOLIAGE = registerModded("mirk_oak_foliage_placer", MirkOakFoliagePlacer.CODEC);
		CLUSTER_FOLIAGE = registerModded("cluster_foliage_placer", ClusterFoliagePlacer.CODEC);
		CULUMALDA_FOLIAGE = registerModded("culumalda_foliage_placer", CulumaldaFoliagePlacer.CODEC);
	}

	private static FoliagePlacerType registerModded(String name, Codec codec) {
		FoliagePlacerType type = new FoliagePlacerType(codec);
		type.setRegistryName("lotr", name);
		ForgeRegistries.FOLIAGE_PLACER_TYPES.register(type);
		return type;
	}
}
