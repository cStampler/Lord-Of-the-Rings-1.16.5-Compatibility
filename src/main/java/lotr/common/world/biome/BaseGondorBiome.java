package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public abstract class BaseGondorBiome extends LOTRBiomeBase {
	protected BaseGondorBiome(Builder builder, boolean major) {
		super(builder, major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addHorsesDonkeys(builder);
		this.addBears(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, ((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState(), 1, 3, 200, 3);
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 200, 3);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addGranite(builder);
		LOTRBiomeFeatures.addGondorRockPatches(builder);
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.GONDOR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GONDOR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.STONE_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.GONDOR;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.addSubSoilLayer(((Block) LOTRBlocks.GONDOR_ROCK.get()).defaultBlockState(), 8, 10);
	}
}
