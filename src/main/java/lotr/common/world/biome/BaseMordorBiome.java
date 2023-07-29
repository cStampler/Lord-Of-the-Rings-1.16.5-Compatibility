package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public abstract class BaseMordorBiome extends LOTRBiomeBase {
	protected BaseMordorBiome(Builder builder, int water, boolean major) {
		super(builder, water, major);
		biomeColors.setWater(water);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addDirtGravel(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addMordorDirtGravel(builder);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addMordorOres(builder);
	}

	@Override
	protected void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addReedsWithDriedChance(builder, 1.0F);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.MORDOR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.MORDOR_BRICK_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.ORC_TORCH.get()).defaultBlockState(), 1));
	}

	@Override
	protected ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.ASHFALL;
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.MORDOR_PATH;
	}

	@Override
	public boolean hasSkyFeatures() {
		return false;
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setFillerDepth(2.0D);
		config.addSubSoilLayer(((Block) LOTRBlocks.MORDOR_DIRT.get()).defaultBlockState(), 3);
		config.setUnderwater(((Block) LOTRBlocks.MORDOR_GRAVEL.get()).defaultBlockState());
		config.addSubSoilLayer(((Block) LOTRBlocks.MORDOR_ROCK.get()).defaultBlockState(), 1000);
	}
}
