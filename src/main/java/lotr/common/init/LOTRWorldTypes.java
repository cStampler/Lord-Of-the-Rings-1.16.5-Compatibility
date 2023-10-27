package lotr.common.init;

import java.util.Optional;

import lotr.common.config.ClientsideCurrentServerConfigSettings;
import lotr.common.world.gen.MiddleEarthChunkGenerator;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.SimpleRegistry;
import net.minecraft.world.Dimension;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.world.ForgeWorldType;
import net.minecraftforge.common.world.ForgeWorldType.IChunkGeneratorFactory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRWorldTypes {
	public static final DeferredRegister WORLD_TYPES;
	public static final RegistryObject MIDDLE_EARTH;
	public static final RegistryObject MIDDLE_EARTH_CLASSIC;

	static {
		WORLD_TYPES = DeferredRegister.create(ForgeRegistries.WORLD_TYPES, "lotr");
		MIDDLE_EARTH = WORLD_TYPES.register("me", () -> new ForgeWorldType(createMiddleEarthWorldType(false)));
		MIDDLE_EARTH_CLASSIC = WORLD_TYPES.register("me_classic", () -> new ForgeWorldType(createMiddleEarthWorldType(true)));
	}

	private static ChunkGenerator createDefaultOverworldChunkGenerator(Registry biomeReg, Registry dimSettingsReg, long seed) {
		return DimensionGeneratorSettings.makeDefaultOverworld(biomeReg, dimSettingsReg, seed);
	}

	private static IChunkGeneratorFactory createMiddleEarthWorldType(final boolean classicBiomes) {
		return new IChunkGeneratorFactory() {
			@Override
			public ChunkGenerator createChunkGenerator(Registry biomeRegistry, Registry dimensionSettingsRegistry, long seed, String generatorSettings) {
				return LOTRWorldTypes.createDefaultOverworldChunkGenerator(biomeRegistry, dimensionSettingsRegistry, seed);
			}

			@Override
			public DimensionGeneratorSettings createSettings(DynamicRegistries dynamicRegistries, long seed, boolean generateStructures, boolean bonusChest, String generatorSettings) {
				IChunkGeneratorFactory defaultMethodProxy = this::createChunkGenerator;
				DimensionGeneratorSettings settings = defaultMethodProxy.createSettings(dynamicRegistries, seed, generateStructures, bonusChest, generatorSettings);
				MiddleEarthChunkGenerator meChunkgen = null;
				try {
					meChunkgen = (MiddleEarthChunkGenerator) LOTRWorldTypes.findMiddleEarthChunkGeneratorFromSettings(settings).orElseThrow(() -> new IllegalStateException("Expected to find a MiddleEarthChunkGenerator in new Middle-earth worldgen settings - this is a development error."));
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				meChunkgen.hackySetWorldTypeInstantMiddleEarth(true);
				meChunkgen.hackySetWorldTypeClassicBiomes(classicBiomes);
				return settings;
			}
		};
	}

	private static Optional findMiddleEarthChunkGeneratorFromSettings(DimensionGeneratorSettings dimGenSettings) {
		SimpleRegistry dimRegistry = dimGenSettings.dimensions();
		Dimension middleEarth = (Dimension) dimRegistry.get(LOTRDimensions.MIDDLE_EARTH_DIM_KEY);
		if (middleEarth != null) {
			ChunkGenerator chunkGen = middleEarth.generator();
			if (chunkGen instanceof MiddleEarthChunkGenerator) {
				return Optional.of((MiddleEarthChunkGenerator) chunkGen);
			}
		}

		return Optional.empty();
	}

	public static boolean hasMapFeatures(ServerWorld world) {
		DimensionGeneratorSettings dimGenSettings = world.getServer().getWorldData().worldGenSettings();
		Optional meChunkgen = findMiddleEarthChunkGeneratorFromSettings(dimGenSettings);
		return (Boolean) meChunkgen.map(cg -> !((MiddleEarthChunkGenerator) cg).isClassicBiomes()).orElse(true);
	}

	public static boolean hasMapFeaturesClientside() {
		return ClientsideCurrentServerConfigSettings.INSTANCE.hasMapFeatures;
	}

	public static boolean isInstantME(ServerWorld world) {
		DimensionGeneratorSettings dimGenSettings = world.getServer().getWorldData().worldGenSettings();
		Optional meChunkgen = findMiddleEarthChunkGeneratorFromSettings(dimGenSettings);
		return (Boolean) meChunkgen.map(hummel -> ((MiddleEarthChunkGenerator) hummel).isInstantMiddleEarth()).orElse(false);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		WORLD_TYPES.register(bus);
	}
}
