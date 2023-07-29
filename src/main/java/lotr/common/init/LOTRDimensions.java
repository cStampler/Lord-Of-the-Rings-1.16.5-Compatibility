package lotr.common.init;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableSet;
import com.mojang.serialization.*;

import lotr.common.LOTRLog;
import lotr.common.dim.*;
import lotr.common.fac.Faction;
import lotr.common.util.LOTRUtil;
import lotr.common.world.biome.provider.MiddleEarthBiomeProvider;
import lotr.common.world.gen.MiddleEarthChunkGenerator;
import net.minecraft.block.Blocks;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.*;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.settings.*;
import net.minecraft.world.server.ServerWorld;

public class LOTRDimensions {
	public static final ResourceLocation MIDDLE_EARTH_ID = new ResourceLocation("lotr", "middle_earth");
	public static final LOTRDimensionType MIDDLE_EARTH_DIMTYPE;
	public static final RegistryKey MIDDLE_EARTH_DIMTYPE_KEY;
	public static final RegistryKey MIDDLE_EARTH_DIM_KEY;
	public static final RegistryKey MIDDLE_EARTH_WORLD_KEY;
	private static Set addedDimensionKeys;
	public static final RegistryKey MIDDLE_EARTH_DIMSETTINGS_KEY;
	public static final DimensionSettings MIDDLE_EARTH_DIMSETTINGS;

	static {
		MIDDLE_EARTH_DIMTYPE = dispatchModDimensionType(MIDDLE_EARTH_ID);
		MIDDLE_EARTH_DIMTYPE_KEY = RegistryKey.create(Registry.DIMENSION_TYPE_REGISTRY, MIDDLE_EARTH_ID);
		MIDDLE_EARTH_DIM_KEY = createDimensionKey(MIDDLE_EARTH_ID);
		MIDDLE_EARTH_WORLD_KEY = RegistryKey.create(Registry.DIMENSION_REGISTRY, MIDDLE_EARTH_DIM_KEY.location());
		MIDDLE_EARTH_DIMSETTINGS_KEY = RegistryKey.create(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY, new ResourceLocation("lotr", "middle_earth"));
		MIDDLE_EARTH_DIMSETTINGS = createMiddleEarthDimensionSettings();
	}

	public static void addSpecificDimensionToWorldRegistry(RegistryKey dimKey, SimpleRegistry dimReg, Registry dimTypeReg, Registry biomeReg, Registry dimSettingsReg, long seed) {
		if (!dimKey.equals(MIDDLE_EARTH_DIM_KEY)) {
			throw new IllegalArgumentException("Coding error! LOTR mod somehow tried to add an unknown dimension (" + dimKey.location() + ") to the world registry - it isn't one of ours!");
		}
		dimReg.register(dimKey, new Dimension(() -> ((DimensionType) dimTypeReg.getOrThrow(MIDDLE_EARTH_DIMTYPE_KEY)), createMiddleEarthChunkGenerator(biomeReg, dimSettingsReg, seed)), Lifecycle.stable());
	}

	private static RegistryKey createDimensionKey(ResourceLocation res) {
		RegistryKey key = RegistryKey.create(Registry.LEVEL_STEM_REGISTRY, res);
		if (addedDimensionKeys == null) {
			addedDimensionKeys = new HashSet();
		}

		addedDimensionKeys.add(key);
		return key;
	}

	private static ChunkGenerator createMiddleEarthChunkGenerator(Registry biomeReg, Registry dimSettingsReg, long seed) {
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

	public static RegistryKey getCurrentLOTRDimensionOrFallback(World world) {
		DimensionType dimension = world.dimensionType();
		return dimension instanceof LOTRDimensionType ? world.dimension() : MIDDLE_EARTH_WORLD_KEY;
	}

	public static BlockPos getDimensionSpawnPoint(ServerWorld world) {
		DimensionType dimension = world.dimensionType();
		return dimension instanceof MiddleEarthDimensionType ? ((MiddleEarthDimensionType) dimension).getSpawnCoordinate(world) : world.getSharedSpawnPos();
	}

	public static ITextComponent getDisplayName(RegistryKey dimensionWorldKey) {
		ResourceLocation dimensionName = dimensionWorldKey.location();
		String key = String.format("dimension.%s.%s", dimensionName.getNamespace(), dimensionName.getPath());
		return new TranslationTextComponent(key);
	}

	public static boolean isAddedDimension(RegistryKey key) {
		return addedDimensionKeys.contains(key);
	}

	public static boolean isDimension(Faction fac, RegistryKey dimension) {
		return fac.getDimension().equals(dimension);
	}

	public static boolean isDimension(World world, RegistryKey dimension) {
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

	public static void registerDimensionTypes(MutableRegistry dimTypeReg) {
		dimTypeReg.register(MIDDLE_EARTH_DIMTYPE_KEY, MIDDLE_EARTH_DIMTYPE, Lifecycle.stable());
	}

	public static void registerWorldDimensions(SimpleRegistry dimReg, Registry dimTypeReg, Registry biomeReg, Registry dimSettingsReg, long seed) {
		Iterator var6 = viewAddedDimensions().iterator();

		while (var6.hasNext()) {
			RegistryKey modDimension = (RegistryKey) var6.next();
			addSpecificDimensionToWorldRegistry(modDimension, dimReg, dimTypeReg, biomeReg, dimSettingsReg, seed);
		}

	}

	public static void replaceDimensionCodecToForceStability() {
		try {
			final Codec codec = DimensionGeneratorSettings.CODEC;
			Codec stableCodec = new Codec() {
				@Override
				public DataResult decode(DynamicOps ops, Object input) {
					DataResult result = codec.decode(ops, input);
					try {
						return DataResult.success(result.result().orElseThrow(() -> new IllegalStateException("Failed to change lifecycle to stable")), Lifecycle.stable());
					} catch (Throwable e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					return result;
				}

				@Override
				public DataResult encode(Object input, DynamicOps ops, Object prefix) {
					return codec.encode(input, ops, prefix);
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

	public static Set viewAddedDimensions() {
		return ImmutableSet.copyOf(addedDimensionKeys);
	}
}
