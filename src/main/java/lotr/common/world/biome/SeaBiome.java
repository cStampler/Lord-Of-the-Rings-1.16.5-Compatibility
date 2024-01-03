package lotr.common.world.biome;

import java.util.List;
import java.util.Random;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.biome.surface.SurfaceNoiseMixer;
import lotr.common.world.biome.surface.UnderwaterNoiseMixer;
import lotr.common.world.gen.feature.LatitudeBasedFeatureConfig;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.LightType;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.PerlinNoiseGenerator;

public class SeaBiome extends LOTRBiomeBase {
	private final PerlinNoiseGenerator iceNoiseGen;
	private SeaClimateWaterSpawns coldWaterSpawns;
	private SeaClimateWaterSpawns normalWaterSpawns;
	private SeaClimateWaterSpawns tropicalWaterSpawns;
	private final Random waterSpawningRand;

	public SeaBiome(boolean major) {
		this(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.OCEAN).depth(-1.5F).scale(0.1F).temperature(0.7F).downfall(0.9F), major);
	}

	protected SeaBiome(Builder builder, boolean major) {
		super(builder, major);
		iceNoiseGen = makeSingleLayerPerlinNoise(5231241491057810726L);
		coldWaterSpawns = new SeaClimateWaterSpawns();
		normalWaterSpawns = new SeaClimateWaterSpawns();
		tropicalWaterSpawns = new SeaClimateWaterSpawns();
		waterSpawningRand = new Random();
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		addAmbientCreatures(builder);
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.SHEEP, 6, 4, 4));
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.POLAR_BEAR, 2, 1, 2));
		coldWaterSpawns.add(new Spawners(EntityType.SQUID, 3, 1, 4));
		coldWaterSpawns.add(new Spawners(EntityType.COD, 15, 3, 6));
		coldWaterSpawns.add(new Spawners(EntityType.SALMON, 15, 1, 5));
		normalWaterSpawns.add(new Spawners(EntityType.SQUID, 1, 1, 4));
		normalWaterSpawns.add(new Spawners(EntityType.COD, 10, 3, 6));
		normalWaterSpawns.add(new Spawners(EntityType.DOLPHIN, 1, 1, 2));
		tropicalWaterSpawns.add(new Spawners(EntityType.SQUID, 10, 4, 4));
		tropicalWaterSpawns.add(new Spawners(EntityType.PUFFERFISH, 15, 1, 3));
		tropicalWaterSpawns.add(new Spawners(EntityType.TROPICAL_FISH, 25, 8, 8));
		tropicalWaterSpawns.add(new Spawners(EntityType.DOLPHIN, 2, 1, 2));
	}

	@Override
	protected void addCarvers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addSeaCarvers(builder);
	}

	@Override
	protected void addFeatures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addFeatures(builder);
		addIcebergs(builder);
	}

	protected void addIcebergs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addIcebergs(builder);
		LOTRBiomeFeatures.addBlueIcePatches(builder);
	}

	@Override
	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addOres(builder);
		LOTRBiomeFeatures.addExtraSalt(builder, 8, 4, 64);
		LOTRBiomeFeatures.addSaltInSand(builder, 8, 1, 56, 80);
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithLatitudeConfig(this, builder, LatitudeBasedFeatureConfig.LatitudeConfiguration.ofInverted(LatitudeBasedFeatureConfig.LatitudeValuesType.ICE).min(0.75F), 1, 0.1F, LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.beech(), 500, LOTRBiomeFeatures.beechFancy(), 50, LOTRBiomeFeatures.apple(), 30, LOTRBiomeFeatures.pear(), 30);
		LOTRBiomeFeatures.addTreesWithLatitudeConfig(this, builder, LatitudeBasedFeatureConfig.LatitudeConfiguration.of(LatitudeBasedFeatureConfig.LatitudeValuesType.ICE), 0, 0.25F, LOTRBiomeFeatures.spruce(), 600, LOTRBiomeFeatures.spruceThin(), 400, LOTRBiomeFeatures.spruceDead(), 2000, LOTRBiomeFeatures.fir(), 400);
		LOTRBiomeFeatures.addGrass(this, builder, 6, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 2);
		LOTRBiomeFeatures.addSeagrass(builder, 48, 0.4F);
		LOTRBiomeFeatures.addExtraUnderwaterSeagrass(builder);
		LOTRBiomeFeatures.addKelp(builder);
		LOTRBiomeFeatures.addCoral(builder);
		LOTRBiomeFeatures.addSeaPickles(builder);
		LOTRBiomeFeatures.addSponges(builder);
	}

	@Override
	public boolean doesSnowGenerate(boolean defaultDoesSnowGenerate, IWorldReader world, BlockPos pos) {
		if (isSeaFrozen(world, pos) && pos.getY() >= 0 && pos.getY() < world.dimensionType().logicalHeight() && world.getBrightness(LightType.BLOCK, pos) < 10) {
			BlockState state = world.getBlockState(pos);
			if (state.isAir() && Blocks.SNOW.defaultBlockState().canSurvive(world, pos)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public boolean doesWaterFreeze(boolean defaultDoesSnowGenerate, IWorldReader world, BlockPos pos, boolean mustBeAtEdge) {
		if (isSeaFrozen(world, pos) && pos.getY() >= 0 && pos.getY() < world.dimensionType().logicalHeight() && world.getBrightness(LightType.BLOCK, pos) < 10) {
			BlockState state = world.getBlockState(pos);
			FluidState fluid = world.getFluidState(pos);
			if (fluid.getType() == Fluids.WATER && state.getBlock() instanceof FlowingFluidBlock) {
				if (!mustBeAtEdge) {
					return true;
				}

				boolean surrounded = world.isWaterAt(pos.west()) && world.isWaterAt(pos.east()) && world.isWaterAt(pos.north()) && world.isWaterAt(pos.south());
				if (!surrounded) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}

	@Override
	public List<MobSpawnInfo.Spawners> getSpawnsAtLocation(EntityClassification creatureType, BlockPos pos) {
		if (creatureType != EntityClassification.WATER_CREATURE && creatureType != EntityClassification.WATER_AMBIENT) {
			return super.getSpawnsAtLocation(creatureType, pos);
		}
		int z = pos.getZ();
		double iceProgressF = MapSettingsManager.serverInstance().getCurrentLoadedMap().getWaterLatitudes().getIceCoverageForLatitude(z);
		double coralProgressF = MapSettingsManager.serverInstance().getCurrentLoadedMap().getWaterLatitudes().getCoralForLatitude(z);
		if (iceProgressF > 0.0D && waterSpawningRand.nextFloat() < iceProgressF) {
			return coldWaterSpawns.getSpawns(creatureType);
		}
		return coralProgressF > 0.0D && waterSpawningRand.nextFloat() < coralProgressF ? tropicalWaterSpawns.getSpawns(creatureType) : normalWaterSpawns.getSpawns(creatureType);
	}

	@Override
	public float getTemperatureForSnowWeatherRendering(IWorld world, BlockPos pos) {
		return getIceLatitudeLevel(world, pos.getZ()) > 0.25F ? 0.0F : super.getTemperatureForSnowWeatherRendering(world, pos);
	}

	public boolean isSeaFrozen(IWorldReader world, BlockPos pos) {
		int x = pos.getX();
		int z = pos.getZ();
		float iceProgressF = getIceLatitudeLevel(world, z);
		if (iceProgressF <= 0.0F) {
			return false;
		}
		if (iceProgressF >= 1.0F) {
			return true;
		}
		double noise1 = iceNoiseGen.getValue(x * 0.1D, z * 0.1D, false);
		double noise2 = iceNoiseGen.getValue(x * 0.03D, z * 0.03D, false);
		double noiseAvg = (noise1 + noise2) / 2.0D;
		double noiseNorm = (noiseAvg + 1.0D) / 2.0D;
		return noiseNorm < iceProgressF;
	}

	@Override
	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		super.setupBiomeAmbience(builder);
		builder.grassColorModifier(LOTRGrassColorModifiers.SEA);
	}

	@Override
	protected void setupSurface(MiddleEarthSurfaceConfig config) {
		config.setUnderwaterNoiseMixer(UnderwaterNoiseMixer.SEA_LATITUDE);
	}

	public static float getAdjustedTemperatureForGrassAndFoliage(IWorldReader world, Biome biome, int z) {
		float iceF = getIceLatitudeLevel(world, z);
		float adjustedTemp = MathHelper.lerp(iceF, biome.getBaseTemperature(), 0.0F);
		return MathHelper.clamp(adjustedTemp, 0.0F, 1.0F);
	}

	public static float getIceLatitudeLevel(IWorldReader world, int z) {
		return MapSettingsManager.sidedInstance(world).getCurrentLoadedMap().getWaterLatitudes().getIceCoverageForLatitude(z);
	}

	public static class Beach extends SeaBiome {
		public Beach(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.BEACH).depth(-0.1F).scale(0.03F).temperature(0.8F).downfall(0.7F), major);
		}

		@Override
		protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
			super.addAnimals(builder);
			builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.TURTLE, 10, 2, 5));
		}

		@Override
		protected void addIcebergs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			super.addVegetation(builder);
			LOTRBiomeFeatures.addDriftwood(builder, 12);
		}

		@Override
		public Biome getRiver(IWorld world) {
			return getNormalRiver(world);
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			config.setTop(Blocks.SAND.defaultBlockState());
			config.setFiller(Blocks.SAND.defaultBlockState());
			config.setUnderwater(Blocks.SAND.defaultBlockState());
		}
	}

	public static class Island extends SeaBiome {
		public Island(boolean major) {
			super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.OCEAN).depth(0.0F).scale(0.3F).temperature(0.7F).downfall(0.9F), major);
		}

		@Override
		protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 1, 2, 3);
		}

		@Override
		protected void addIcebergs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			super.setupSurface(config);
			config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.4D, 0.09D).threshold(0.7D).state(Blocks.GRAVEL).topOnly(), SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(2).scales(0.4D, 0.09D).threshold(0.3D).state(Blocks.STONE)));
		}
	}

	public static class WesternIsles extends SeaBiome.Island {
		public WesternIsles(boolean major) {
			super(major);
		}

		@Override
		protected void addIcebergs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		}

		@Override
		protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addAndesite(builder);
		}
	}

	public static class WhiteBeach extends SeaBiome.Beach {
		public WhiteBeach(boolean major) {
			super(major);
		}

		@Override
		protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
			LOTRBiomeFeatures.addWhiteSandSediments(builder);
		}

		@Override
		protected void setupSurface(MiddleEarthSurfaceConfig config) {
			config.setTop(((Block) LOTRBlocks.WHITE_SAND.get()).defaultBlockState());
			config.setFiller(((Block) LOTRBlocks.WHITE_SAND.get()).defaultBlockState());
			config.setUnderwater(((Block) LOTRBlocks.WHITE_SAND.get()).defaultBlockState());
		}
	}
}
