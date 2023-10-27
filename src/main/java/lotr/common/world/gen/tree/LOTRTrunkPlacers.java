package lotr.common.world.gen.tree;

import java.lang.reflect.Constructor;

import com.mojang.serialization.Codec;

import lotr.common.util.LOTRUtil;
import net.minecraft.block.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.IWorldGenerationReader;
import net.minecraft.world.gen.feature.TreeFeature;
import net.minecraft.world.gen.trunkplacer.AbstractTrunkPlacer;
import net.minecraft.world.gen.trunkplacer.TrunkPlacerType;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class LOTRTrunkPlacers {
	public static TrunkPlacerType DEAD_TRUNK_PLACER;
	public static TrunkPlacerType DESERT_TRUNK_PLACER;
	public static TrunkPlacerType BOUGHS_TRUNK_PLACER;
	public static TrunkPlacerType CEDAR_TRUNK_PLACER;
	public static TrunkPlacerType MIRK_OAK_TRUNK_PLACER;
	public static TrunkPlacerType FANGORN_TRUNK_PLACER;
	public static TrunkPlacerType PARTY_TRUNK_PLACER;

	public static void register() {
		DEAD_TRUNK_PLACER = registerModded("dead_trunk_placer", DeadTrunkPlacer.CODEC);
		DESERT_TRUNK_PLACER = registerModded("desert_trunk_placer", DesertTrunkPlacer.CODEC);
		BOUGHS_TRUNK_PLACER = registerModded("boughs_trunk_placer", BoughsTrunkPlacer.CODEC);
		CEDAR_TRUNK_PLACER = registerModded("cedar_trunk_placer", CedarTrunkPlacer.CODEC);
		MIRK_OAK_TRUNK_PLACER = registerModded("mirk_oak_trunk_placer", MirkOakTrunkPlacer.CODEC);
		FANGORN_TRUNK_PLACER = registerModded("fangorn_trunk_placer", FangornTrunkPlacer.CODEC);
		PARTY_TRUNK_PLACER = registerModded("party_trunk_placer", PartyTrunkPlacer.CODEC);
	}

	private static <P extends AbstractTrunkPlacer> TrunkPlacerType<P> registerModded(String name, Codec<P> codec) {
		TrunkPlacerType result = null;
		try {
			Constructor<TrunkPlacerType> met = ObfuscationReflectionHelper.findConstructor(TrunkPlacerType.class, Codec.class);
			LOTRUtil.unlockConstructor(met);
			result = Registry.register(Registry.TRUNK_PLACER_TYPES, new ResourceLocation("lotr", name), met.newInstance(codec));
		} catch (Exception var5) {
			var5.printStackTrace();
		}
		return result;
	}

	public static void setGrassToDirt(IWorldGenerationReader world, BlockPos groundPos) {
		if (world.isStateAtPosition(groundPos, state -> (state.is(Blocks.GRASS_BLOCK) || state.is(Blocks.MYCELIUM)))) {
			TreeFeature.setBlockKnownShape(world, groundPos, Blocks.DIRT.defaultBlockState());
		}

	}
}
