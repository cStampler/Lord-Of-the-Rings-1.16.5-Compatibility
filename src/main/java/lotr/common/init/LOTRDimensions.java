package lotr.common.init;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.Lifecycle;

import lotr.common.LOTRLog;
import lotr.common.dim.LOTRDimensionType;
import lotr.common.dim.MiddleEarthDimensionType;
import lotr.common.fac.Faction;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.provider.MiddleEarthBiomeProvider;
import lotr.common.world.gen.MiddleEarthChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.MutableRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Dimension;
import net.minecraft.world.DimensionType;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.gen.settings.ScalingSettings;
import net.minecraft.world.gen.settings.SlideSettings;
import net.minecraft.world.server.ServerWorld;

public class LOTRDimensions {
	public static final ResourceLocation MIDDLE_EARTH_ID = new ResourceLocation("lotr", "middle_earth");
	public static final LOTRDimensionType MIDDLE_EARTH_DIMTYPE;
	public static final RegistryKey<DimensionType> MIDDLE_EARTH_DIMTYPE_KEY;
	public static final RegistryKey<Dimension> MIDDLE_EARTH_DIM_KEY;
	public static final RegistryKey<World> MIDDLE_EARTH_WORLD_KEY;
	private static Set<RegistryKey<Dimension>> addedDimensionKeys;
	public static final RegistryKey<DimensionSettings> MIDDLE_EARTH_DIMSETTINGS_KEY;
	public static final DimensionSettings MIDDLE_EARTH_DIMSETTINGS;

	static {
		MIDDLE_EARTH_DIMTYPE = dispatchModDimensionType(MIDDLE_EARTH_ID);
		MIDDLE_EARTH_DIMTYPE_KEY = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, MIDDLE_EARTH_ID);
		MIDDLE_EARTH_DIM_KEY = createDimensionKey(MIDDLE_EARTH_ID);
		MIDDLE_EARTH_WORLD_KEY = RegistryKey.create(Registry.DIMENSION_REGISTRY, MIDDLE_EARTH_DIM_KEY.location());
		MIDDLE_EARTH_DIMSETTINGS_KEY = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("lotr", "middle_earth"));
		MIDDLE_EARTH_DIMSETTINGS = createMiddleEarthDimensionSettings();
	}

	public static void addSpecificDimensionToWorldRegistry(RegistryKey<Dimension> dimKey, SimpleRegistry<Dimension> dimReg, Registry<DimensionType> dimTypeReg, Registry<Biome> biomeReg, Registry<DimensionSettings> dimSettingsReg, long seed) {
		if (!dimKey.equals(MIDDLE_EARTH_DIM_KEY)) {
			throw new IllegalArgumentException("Coding error! LOTR mod somehow tried to add an unknown dimension (" + dimKey.location() + ") to the world registry - it isn't one of ours!");
		}
		dimReg.register(dimKey, new Dimension(() -> ((DimensionType) dimTypeReg.getOrThrow(MIDDLE_EARTH_DIMTYPE_KEY)), createMiddleEarthChunkGenerator(biomeReg, dimSettingsReg, seed)), Lifecycle.stable());
	}

	private static RegistryKey<Dimension> createDimensionKey(ResourceLocation res) {
		RegistryKey<Dimension> key = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, res);
		if (addedDimensionKeys == null) {
			addedDimensionKeys = new HashSet<RegistryKey<Dimension>>();
		}

		addedDimensionKeys.add(key);
		return key;
	}

	private static ChunkGenerator createMiddleEarthChunkGenerator(Registry<Biome> biomeReg, Registry<DimensionSettings> dimSettingsReg, long seed) {
		boolean classicBiomes = false;
		return new MiddleEarthChunkGenerator(new MiddleEarthBiomeProvider(seed, classicBiomes, biomeReg), seed, () -> (DimensionSettings) dimSettingsReg.getOrThrow(MIDDLE_EARTH_DIMSETTINGS_KEY), Optional.empty());
	}


	private static DimensionSettings createMiddleEarthDimensionSettings() {
		DimensionStructuresSettings dimStrSettings = new DimensionStructuresSettings(false);
		boolean isAmplified = false;
		double d0 = 0.9999999814507745D;
		return new DimensionSettings(dimStrSettings, new NoiseSettings(256, new ScalingSettings(d0, d0, 80.0D, 160.0D), new SlideSettings(-10, 3, 0), new SlideSettings(-30, 0, 0), 1, 2, 1.0D, -0.46875D, true, true, false, isAmplified), Blocks.STONE.defaultBlockState(), Blocks.WATER.defaultBlockState(), -10, 0, 63, false);
	}

	public static LOTRDimensionType dispatchModDimensionType(ResourceLocation dimensionId) {
		if (dimensionId.equals(MIDDLE_EARTH_ID)) {
			return new MiddleEarthDimensionType(MIDDLE_EARTH_ID);
		}
		throw new IllegalArgumentException("Dimension ID " + dimensionId + " is not a known LOTR mod dimension and cannot be dispatched to a LOTRDimensionType!");
	}

	public static RegistryKey<World> getCurrentLOTRDimensionOrFallback(World world) {
		DimensionType dimension = world.dimensionType();
		return dimension instanceof LOTRDimensionType ? world.dimension() : MIDDLE_EARTH_WORLD_KEY;
	}

	public static BlockPos getDimensionSpawnPoint(ServerWorld world) {
		DimensionType dimension = world.dimensionType();
		return dimension instanceof MiddleEarthDimensionType ? ((MiddleEarthDimensionType) dimension).getSpawnCoordinate(world) : world.getSharedSpawnPos();
	}

	public static ITextComponent getDisplayName(RegistryKey<World> dimensionWorldKey) {
		ResourceLocation dimensionName = dimensionWorldKey.location();
		String key = String.format("dimension.%s.%s", dimensionName.getNamespace(), dimensionName.getPath());
		return new TranslationTextComponent(key);
	}

	public static boolean isAddedDimension(RegistryKey<Dimension> key) {
		return addedDimensionKeys.contains(key);
	}

	public static boolean isDimension(Faction fac, RegistryKey<World> dimension) {
		return fac.getDimension().equals(dimension);
	}

	public static boolean isDimension(World world, RegistryKey<World> dimension) {
		return world.dimension().equals(dimension);
	}

	public static boolean isModDimension(World world) {
		return world.dimensionType() instanceof LOTRDimensionType;
	}

	public static void registerAssociated() {
		replaceDimensionCodecToForceStability();
		Registry.register(Registry.CHUNK_GENERATOR, new ResourceLocation("lotr", "middle_earth"), MiddleEarthChunkGenerator.CODEC);
		Registry.register(Registry.BIOME_SOURCE, new ResourceLocation("lotr", "middle_earth"), MiddleEarthBiomeProvider.CODEC);
		WorldGenRegistries.register(WorldGenRegistries.NOISE_GENERATOR_SETTINGS, MIDDLE_EARTH_DIMSETTINGS_KEY.location(), MIDDLE_EARTH_DIMSETTINGS);
	}

	public static void registerDimensionTypes(MutableRegistry<DimensionType> dimTypeReg) {
		dimTypeReg.register(MIDDLE_EARTH_DIMTYPE_KEY, MIDDLE_EARTH_DIMTYPE, Lifecycle.stable());
	}

	public static void registerWorldDimensions(SimpleRegistry<Dimension> dimReg, Registry<DimensionType> dimTypeReg, Registry<Biome> biomeReg, Registry<DimensionSettings> dimSettingsReg, long seed) {
		Iterator<RegistryKey<Dimension>> var6 = viewAddedDimensions().iterator();

		while (var6.hasNext()) {
			RegistryKey<Dimension> modDimension = var6.next();
			addSpecificDimensionToWorldRegistry(modDimension, dimReg, dimTypeReg, biomeReg, dimSettingsReg, seed);
		}

	}

	public static void replaceDimensionCodecToForceStability() {
		try {
			final Codec<DimensionGeneratorSettings> codec = DimensionGeneratorSettings.CODEC;
		      Codec<DimensionGeneratorSettings> stableCodec = new Codec<DimensionGeneratorSettings>() {
		          public <T> DataResult<T> encode(DimensionGeneratorSettings input, DynamicOps<T> ops, T prefix) {
		            return codec.encode(input, ops, prefix);
		          }
		          
		          public <T> DataResult<Pair<DimensionGeneratorSettings, T>> decode(DynamicOps<T> ops, T input) {
		            DataResult<Pair<DimensionGeneratorSettings, T>> result = codec.decode(ops, input);
		            return DataResult.success(result.result().orElseThrow(() -> new IllegalStateException("Failed to change lifecycle to stable")), Lifecycle.stable());
		          }
		        };
			Field f_codec = Stream.of(DimensionGeneratorSettings.class.getDeclaredFields()).filter(field -> ((field.getModifiers() & 8) != 0)).filter(field -> (field.getType() == Codec.class)).findFirst().orElseThrow(() -> new IllegalStateException("Failed to find codec field in DimensionGeneratorSettings"));
			LOTRUtil.unlockFinalField(f_codec);
			f_codec.set((Object) null, stableCodec);
		} catch (Exception var3) {
			var3.printStackTrace();
			LOTRLog.error("Failed to set dimension generator settings codec to stable");
		}

	}

	public static Set<RegistryKey<Dimension>> viewAddedDimensions() {
		return ImmutableSet.copyOf(addedDimensionKeys);
	}
}
