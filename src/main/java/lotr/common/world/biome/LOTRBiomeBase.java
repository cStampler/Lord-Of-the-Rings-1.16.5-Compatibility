package lotr.common.world.biome;

import java.awt.Color;
import java.lang.reflect.*;
import java.util.*;

import com.google.common.collect.ImmutableList;

import lotr.common.*;
import lotr.common.init.*;
import lotr.common.util.*;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.gen.feature.WeightedRandomFeatureConfig;
import net.minecraft.block.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.*;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.feature.*;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class LOTRBiomeBase implements LOTRBiomeWrapper {
	protected static final int STANDARD_FOG_COLOR = 12638463;
	protected static final int STANDARD_WATER_FOG_COLOR = 329011;
	protected static final PerlinNoiseGenerator SNOW_VARIETY_NOISE = makeSingleLayerPerlinNoise(2490309256000602L);
	private ResourceLocation biomeName;
	private Biome actualBiome;
	private Builder unbuiltBiome;
	private net.minecraft.world.biome.BiomeAmbience.Builder unbuiltBiomeAmbience;
	private final boolean isMajorBiome;
	protected LOTRBiomeBase.CustomBiomeColors biomeColors;
	private float treeDensityForPodzol;
	private int maxPodzolHeight;
	private WeightedRandomFeatureConfig grassBonemealGenerator;

	protected LOTRBiomeBase(Builder builder, boolean major) {
		this(builder, 329011, major);
	}

	protected LOTRBiomeBase(Builder builder, int waterFogColor, boolean major) {
		maxPodzolHeight = Integer.MAX_VALUE;
		unbuiltBiome = builder;
		unbuiltBiomeAmbience = new net.minecraft.world.biome.BiomeAmbience.Builder().waterColor(16777215).waterFogColor(waterFogColor);
		isMajorBiome = major;
		biomeColors = new LOTRBiomeBase.CustomBiomeColors(builder);
	}

	protected void addAmbientCreatures(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		builder.addSpawn(EntityClassification.AMBIENT, new Spawners(EntityType.BAT, 10, 8, 8));
	}

	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		addLandCreatures(builder);
		addAmbientCreatures(builder);
	}

	protected void addBears(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addBears(builder, 1);
	}

	protected void addBears(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
	}

	protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addSandSediments(builder);
	}

	protected void addBoars(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addBoars(builder, 1);
	}

	protected void addBoars(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
	}

	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	protected void addCaracals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addCaracals(builder, 1);
	}

	protected void addCaracals(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners((EntityType) LOTREntities.CARACAL.get(), 12 * mul, 1, 4));
	}

	protected void addCarvers(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCarvers(builder);
	}

	protected void addCobwebs(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCobwebs(builder);
	}

	protected void addDeer(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addDeer(builder, 1);
	}

	protected void addDeer(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
	}

	protected void addDirtGravel(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addDirtGravel(builder);
	}

	protected void addDripstones(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addDripstones(builder);
	}

	protected void addElk(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addElk(builder, 1);
	}

	protected void addElk(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
	}

	protected void addExtraSheep(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.SHEEP, 24 * mul, 4, 4));
	}

	protected void addFeatures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addCarvers(builder);
		addLakes(builder);
		addDirtGravel(builder);
		addStoneVariants(builder);
		addOres(builder);
		addDripstones(builder);
		addCobwebs(builder);
		addSedimentDisks(builder);
		addBoulders(builder);
		addVegetation(builder);
		LOTRBiomeFeatures.addMushrooms(builder);
		addReeds(builder);
		addPumpkins(builder);
		addLiquidSprings(builder);
		LOTRBiomeFeatures.addFreezeTopLayer(builder);
		addStructures(builder);
	}

	protected void addFoxes(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addFoxes(builder, 1);
	}

	protected void addFoxes(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.FOX, 16 * mul, 2, 4));
	}

	protected void addHorsesDonkeys(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addHorsesDonkeys(builder, 1);
	}

	protected void addHorsesDonkeys(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.HORSE, 10 * mul, 2, 6));
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.DONKEY, 1 * mul, 1, 3));
	}

	protected void addLakes(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addLakes(builder);
	}

	protected void addLandCreatures(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.SHEEP, 24, 4, 4));
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.PIG, 20, 4, 4));
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.CHICKEN, 20, 4, 4));
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.COW, 16, 4, 4));
		this.addDeer(builder);
	}

	protected void addLiquidSprings(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addWaterLavaSprings(builder);
	}

	protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addOres(builder);
	}

	protected void addPumpkins(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addPumpkins(builder);
	}

	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addReeds(builder);
	}

	protected void addSedimentDisks(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		addBiomeSandSediments(builder);
		LOTRBiomeFeatures.addClayGravelSediments(builder);
	}

	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addGranite(builder);
	}

	protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
	}

	protected void addWolves(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		this.addWolves(builder, 1);
	}

	protected void addWolves(net.minecraft.world.biome.MobSpawnInfo.Builder builder, int mul) {
		builder.addSpawn(EntityClassification.CREATURE, new Spawners(EntityType.WOLF, 10 * mul, 4, 8));
	}

	@Override
	public final Vector3d alterCloudColor(Vector3d clouds) {
		if (biomeColors.hasClouds()) {
			float[] colors = biomeColors.getCloudsRGB();
			clouds = clouds.multiply(colors[0], colors[1], colors[2]);
		}

		return clouds;
	}

	@Override
	public final Biome getActualBiome() {
		if (actualBiome != null) {
			return actualBiome;
		}
		throw new IllegalStateException("Cannot fetch Biome object for LOTRBiome " + biomeName + " - has not yet been initialised!");
	}

	protected ExtendedWeatherType getBiomeExtendedWeather() {
		return ExtendedWeatherType.NONE;
	}

	@Override
	public final ResourceLocation getBiomeRegistryName() {
		return biomeName;
	}

	@Override
	public final float getCloudCoverage() {
		return biomeColors.getCloudCoverage();
	}

	public final int getCustomWaterColor() {
		return biomeColors.getWater();
	}

	@Override
	public final ExtendedWeatherType getExtendedWeatherVisually() {
		return isChristmasSnowInVisualContext() ? ExtendedWeatherType.NONE : getBiomeExtendedWeather();
	}

	@Override
	public BlockState getGrassForBonemeal(Random rand, BlockPos pos) {
		if (grassBonemealGenerator != null) {
			ConfiguredFeature feature = grassBonemealGenerator.getRandomFeature(rand);
			if (feature.config instanceof BlockClusterFeatureConfig) {
				return ((BlockClusterFeatureConfig) feature.config).stateProvider.getState(rand, pos);
			}

			LOTRLog.warn("DEVELOPMENT ERROR: Biome (%s) grass bonemeal generator contains a FeatureConfig of invalid type - should be BlockClusterFeatureConfig, but is %s", biomeName, feature.config.getClass().getName());
		}

		return Blocks.GRASS.defaultBlockState();
	}

	protected final Biome getNormalRiver(IWorld world) {
		return LOTRBiomes.getBiomeByRegistryName(LOTRBiomes.RIVER.getRegistryName(), world);
	}

	@Override
	public final RainType getPrecipitationVisually() {
		return isChristmasSnowInVisualContext() ? RainType.SNOW : actualBiome.getPrecipitation();
	}

	@Override
	public Biome getRiver(IWorld world) {
		return getNormalRiver(world);
	}

	@Override
	public LOTRBiomeBase getShore() {
		return actualBiome.getDepth() < 0.0F ? this : LOTRBiomes.BEACH.getInitialisedBiomeWrapper();
	}

	@Override
	public List getSpawnsAtLocation(EntityClassification creatureType, BlockPos pos) {
		return actualBiome.getMobSettings().getMobs(creatureType);
	}

	@Override
	public float getTemperatureForSnowWeatherRendering(IWorld world, BlockPos pos) {
		return actualBiome.getTemperature(pos);
	}

	public final boolean hasCustomWaterColor() {
		return biomeColors.hasWater();
	}

	public Biome initialiseActualBiome() {
		if (actualBiome != null) {
			throw new IllegalStateException("LOTRBiome object for " + biomeName + " is already initialised!");
		}
		setupBiomeAmbience(unbuiltBiomeAmbience);
		unbuiltBiome.specialEffects(unbuiltBiomeAmbience.build());
		unbuiltBiomeAmbience = null;
		net.minecraft.world.biome.BiomeGenerationSettings.Builder generationBuilder = new net.minecraft.world.biome.BiomeGenerationSettings.Builder();
		MiddleEarthSurfaceConfig surfaceBuilderConfig = MiddleEarthSurfaceConfig.createDefault();
		setupSurface(surfaceBuilderConfig);
		addFeatures(generationBuilder);
		surfaceBuilderConfig.setTreeDensityForPodzol(treeDensityForPodzol).setMaxPodzolHeight(maxPodzolHeight);
		generationBuilder.surfaceBuilder(LOTRBiomes.MIDDLE_EARTH_SURFACE.configured(surfaceBuilderConfig));
		unbuiltBiome.generationSettings(generationBuilder.build());
		net.minecraft.world.biome.MobSpawnInfo.Builder entitySpawnBuilder = new net.minecraft.world.biome.MobSpawnInfo.Builder();
		addAnimals(entitySpawnBuilder);
		unbuiltBiome.mobSpawnSettings(entitySpawnBuilder.build());
		actualBiome = unbuiltBiome.build();
		unbuiltBiome = null;
		return actualBiome;
	}

	@Override
	public final boolean isFoggy() {
		return biomeColors.isFoggy();
	}

	public final boolean isMajorBiome() {
		return isMajorBiome;
	}

	@Override
	public boolean isRiver() {
		return false;
	}

	@Override
	public boolean isSurfaceBlockForNPCSpawn(BlockState state) {
		MiddleEarthSurfaceConfig sc = (MiddleEarthSurfaceConfig) getActualBiome().getGenerationSettings().getSurfaceBuilderConfig();
		return sc.isSurfaceBlockForNPCSpawning(state);
	}

	@Override
	public void onGeographicalWaterColorUpdate(int waterColor, Biome biomeObjectInClientRegistry) {
		if (!hasCustomWaterColor()) {
			try {
				Field waterColorP = ObfuscationReflectionHelper.findField(BiomeAmbience.class, "waterColor");
				LOTRUtil.unlockFinalField(waterColorP);
				waterColorP.set(biomeObjectInClientRegistry.getSpecialEffects(), waterColor);
			} catch (Exception var5) {
				var5.printStackTrace();
			}
		}

	}

	public LOTRBiomeBase setBiomeName(ResourceLocation name) {
		if (biomeName != null) {
			throw new IllegalStateException("Cannot set biomeName for LOTRBiome " + biomeName + " - already set!");
		}
		biomeName = name;
		return this;
	}

	public void setGrassBonemealGenerator(WeightedRandomFeatureConfig config) {
		grassBonemealGenerator = config;
	}

	protected void setupBiomeAmbience(net.minecraft.world.biome.BiomeAmbience.Builder builder) {
		if (biomeColors.hasGrass()) {
			unbuiltBiomeAmbience.grassColorOverride(biomeColors.getGrass());
		}

		if (biomeColors.hasFoliage()) {
			unbuiltBiomeAmbience.foliageColorOverride(biomeColors.getFoliage());
		}

		unbuiltBiomeAmbience.skyColor(biomeColors.getSky());
		unbuiltBiomeAmbience.fogColor(biomeColors.getFog());
		if (hasCustomWaterColor()) {
			unbuiltBiomeAmbience.waterColor(getCustomWaterColor());
		}

		builder.ambientMoodSound(MoodSoundAmbience.LEGACY_CAVE_SETTINGS);
	}

	protected void setupSurface(MiddleEarthSurfaceConfig config) {
	}

	public final void updateBiomePodzolVariables(float treeDensity, int maxHeight) {
		treeDensityForPodzol = Math.max(treeDensityForPodzol, treeDensity);
		maxPodzolHeight = Math.min(maxPodzolHeight, maxHeight);
	}

	private static boolean isChristmasSnowInVisualContext() {
		return isChristmasSnowOverride() && LOTRMod.PROXY.isClient();
	}

	private static boolean isChristmasSnowOverride() {
		return CalendarUtil.isChristmas();
	}

	public static boolean isSnowingVisually(LOTRBiomeWrapper biomeWrapper, IWorld world, BlockPos pos) {
		float temp = biomeWrapper.getTemperatureForSnowWeatherRendering(world, pos);
		boolean isChristmas = isChristmasSnowOverride();
		return isChristmas || isTemperatureSuitableForSnow(temp);
	}

	public static boolean isTemperatureSuitableForSnow(float temp) {
		return temp < 0.15F;
	}

	public static PerlinNoiseGenerator makeSingleLayerPerlinNoise(long seed) {
		return new PerlinNoiseGenerator(new SharedSeedRandom(seed), ImmutableList.of(0));
	}

	static void setFinal(Field field, Object newValue) throws Exception {
		field.setAccessible(true);
		Field modifiersField = Field.class.getDeclaredField("modifiers");
		modifiersField.setAccessible(true);
		modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
		field.set(null, newValue);
	}

	public static class CustomBiomeColors {
		private int grass = -1;
		private int foliage = -1;
		private int sky;
		private final int defaultBiomeSky;
		private int clouds = -1;
		private float[] cloudsRGB = new float[3];
		private float cloudCoverage = 1.0F;
		private int fog;
		private float[] fogRGB = new float[3];
		private boolean foggy = false;
		private int water = -1;

		public CustomBiomeColors(Builder builder) {
			float biomeTemp = (Float) ObfuscationReflectionHelper.getPrivateValue(Builder.class, builder, "temperature");
			defaultBiomeSky = calculateSkyColor(biomeTemp);
			setSky(defaultBiomeSky);
			setFog(12638463);
		}

		public float getCloudCoverage() {
			return cloudCoverage;
		}

		public int getClouds() {
			return clouds;
		}

		public float[] getCloudsRGB() {
			return cloudsRGB;
		}

		public int getFog() {
			return fog;
		}

		public float[] getFogRGB() {
			return fogRGB;
		}

		public int getFoliage() {
			return foliage;
		}

		public int getGrass() {
			return grass;
		}

		public int getSky() {
			return sky;
		}

		public int getWater() {
			return water;
		}

		public boolean hasClouds() {
			return clouds >= 0;
		}

		public boolean hasFoliage() {
			return foliage >= 0;
		}

		public boolean hasGrass() {
			return grass >= 0;
		}

		public boolean hasWater() {
			return water >= 0;
		}

		public boolean isFoggy() {
			return foggy;
		}

		public LOTRBiomeBase.CustomBiomeColors resetClouds() {
			clouds = -1;
			cloudsRGB = null;
			cloudCoverage = 1.0F;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors resetFog() {
			setFog(12638463);
			fogRGB = null;
			foggy = false;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors resetFoliage() {
			return setFoliage(-1);
		}

		public LOTRBiomeBase.CustomBiomeColors resetGrass() {
			return setGrass(-1);
		}

		public LOTRBiomeBase.CustomBiomeColors resetSky() {
			return setSky(defaultBiomeSky);
		}

		public LOTRBiomeBase.CustomBiomeColors resetWater(int i) {
			return setWater(-1);
		}

		public LOTRBiomeBase.CustomBiomeColors setCloudCoverage(float f) {
			cloudCoverage = f;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setClouds(int i) {
			clouds = i;
			cloudsRGB = new Color(clouds).getColorComponents(cloudsRGB);
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setFog(int i) {
			fog = i;
			fogRGB = new Color(fog).getColorComponents(fogRGB);
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setFoggy(boolean flag) {
			foggy = flag;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setFoliage(int i) {
			foliage = i;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setGrass(int i) {
			grass = i;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setSky(int i) {
			sky = i;
			return this;
		}

		public LOTRBiomeBase.CustomBiomeColors setWater(int i) {
			water = i;
			return this;
		}

		private static int calculateSkyColor(float p_244206_0_) {
			float lvt_1_1_ = p_244206_0_ / 3.0F;
			lvt_1_1_ = MathHelper.clamp(lvt_1_1_, -1.0F, 1.0F);
			return MathHelper.hsvToRgb(0.62222224F - lvt_1_1_ * 0.05F, 0.5F + lvt_1_1_ * 0.1F, 1.0F);
		}
	}
}
