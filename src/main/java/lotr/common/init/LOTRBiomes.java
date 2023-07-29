package lotr.common.init;

import java.util.*;
import java.util.stream.Collectors;

import lotr.common.world.biome.*;
import lotr.common.world.biome.surface.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import net.minecraft.util.text.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.NonNullSupplier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import net.minecraftforge.registries.*;

public class LOTRBiomes {
	public static final DeferredRegister BIOMES;
	public static final DeferredRegister SURFACE_BUILDERS;
	private static final List PRE_REGISTRY;
	private static final List BIOME_WRAPPER_OBJECTS;
	private static final Map BIOME_WRAPPER_OBJECTS_BY_NAME;
	public static final PreRegisteredLOTRBiome SHIRE;
	public static final PreRegisteredLOTRBiome MORDOR;
	public static final PreRegisteredLOTRBiome ANORIEN;
	public static final PreRegisteredLOTRBiome ROHAN;
	public static final PreRegisteredLOTRBiome MISTY_MOUNTAINS;
	public static final PreRegisteredLOTRBiome SHIRE_WOODLANDS;
	public static final PreRegisteredLOTRBiome RIVER;
	public static final PreRegisteredLOTRBiome TROLLSHAWS;
	public static final PreRegisteredLOTRBiome BLUE_MOUNTAINS;
	public static final PreRegisteredLOTRBiome ERIADOR;
	public static final PreRegisteredLOTRBiome LONE_LANDS;
	public static final PreRegisteredLOTRBiome ITHILIEN;
	public static final PreRegisteredLOTRBiome BROWN_LANDS;
	public static final PreRegisteredLOTRBiome LOTHLORIEN;
	public static final PreRegisteredLOTRBiome IRON_HILLS;
	public static final PreRegisteredLOTRBiome DUNLAND;
	public static final PreRegisteredLOTRBiome EMYN_MUIL;
	public static final PreRegisteredLOTRBiome LINDON;
	public static final PreRegisteredLOTRBiome SOUTHRON_COASTS;
	public static final PreRegisteredLOTRBiome NAN_CURUNIR;
	public static final PreRegisteredLOTRBiome FORODWAITH;
	public static final PreRegisteredLOTRBiome EREGION;
	public static final PreRegisteredLOTRBiome MIRKWOOD;
	public static final PreRegisteredLOTRBiome GREY_MOUNTAINS;
	public static final PreRegisteredLOTRBiome WHITE_MOUNTAINS;
	public static final PreRegisteredLOTRBiome FANGORN;
	public static final PreRegisteredLOTRBiome WOODLAND_REALM;
	public static final PreRegisteredLOTRBiome DALE;
	public static final PreRegisteredLOTRBiome ANGMAR;
	public static final PreRegisteredLOTRBiome HARONDOR;
	public static final PreRegisteredLOTRBiome ENEDWAITH;
	public static final PreRegisteredLOTRBiome VALES_OF_ANDUIN;
	public static final PreRegisteredLOTRBiome ANDUIN_HILLS;
	public static final PreRegisteredLOTRBiome WILDERLAND;
	public static final PreRegisteredLOTRBiome OLD_FOREST;
	public static final PreRegisteredLOTRBiome BREELAND;
	public static final PreRegisteredLOTRBiome CHETWOOD;
	public static final PreRegisteredLOTRBiome MISTY_MOUNTAINS_FOOTHILLS;
	public static final PreRegisteredLOTRBiome BLUE_MOUNTAINS_FOOTHILLS;
	public static final PreRegisteredLOTRBiome GREY_MOUNTAINS_FOOTHILLS;
	public static final PreRegisteredLOTRBiome WHITE_MOUNTAINS_FOOTHILLS;
	public static final PreRegisteredLOTRBiome MORDOR_MOUNTAINS;
	public static final PreRegisteredLOTRBiome FORODWAITH_MOUNTAINS;
	public static final PreRegisteredLOTRBiome ANGMAR_MOUNTAINS;
	public static final PreRegisteredLOTRBiome NURN;
	public static final PreRegisteredLOTRBiome UMBAR;
	public static final PreRegisteredLOTRBiome HARAD_DESERT;
	public static final PreRegisteredLOTRBiome LINDON_WOODLANDS;
	public static final PreRegisteredLOTRBiome ERIADOR_DOWNS;
	public static final PreRegisteredLOTRBiome LONE_LANDS_HILLS;
	public static final PreRegisteredLOTRBiome NORTHLANDS;
	public static final PreRegisteredLOTRBiome NORTHLANDS_FOREST;
	public static final PreRegisteredLOTRBiome SEA;
	public static final PreRegisteredLOTRBiome ISLAND;
	public static final PreRegisteredLOTRBiome BEACH;
	public static final PreRegisteredLOTRBiome TOLFALAS;
	public static final PreRegisteredLOTRBiome LAKE;
	public static final PreRegisteredLOTRBiome NURNEN;
	public static final PreRegisteredLOTRBiome DOR_EN_ERNIL;
	public static final PreRegisteredLOTRBiome EMYN_EN_ERNIL;
	public static final PreRegisteredLOTRBiome WESTERN_ISLES;
	public static final PreRegisteredLOTRBiome COLDFELLS;
	public static final PreRegisteredLOTRBiome ETTENMOORS;
	public static final PreRegisteredLOTRBiome HARNENNOR;
	public static final PreRegisteredLOTRBiome DAGORLAD;
	public static final PreRegisteredLOTRBiome WHITE_BEACH;
	public static final PreRegisteredLOTRBiome DORWINION;
	public static final PreRegisteredLOTRBiome EMYN_WINION;
	public static final PreRegisteredLOTRBiome WOLD;
	public static final PreRegisteredLOTRBiome MINHIRIATH;
	public static final PreRegisteredLOTRBiome ERYN_VORN;
	public static final PreRegisteredLOTRBiome DRUWAITH_IAUR;
	public static final PreRegisteredLOTRBiome ANDRAST;
	public static final PreRegisteredLOTRBiome LOSSARNACH;
	public static final PreRegisteredLOTRBiome LEBENNIN;
	public static final PreRegisteredLOTRBiome PELARGIR;
	public static final PreRegisteredLOTRBiome LAMEDON;
	public static final PreRegisteredLOTRBiome LAMEDON_HILLS;
	public static final PreRegisteredLOTRBiome BLACKROOT_VALE;
	public static final PreRegisteredLOTRBiome PINNATH_GELIN;
	public static final PreRegisteredLOTRBiome ANFALAS;
	public static final PreRegisteredLOTRBiome NORTHERN_WILDERLAND;
	public static final PreRegisteredLOTRBiome NORTHERN_DALE;
	public static final PreRegisteredLOTRBiome RIVENDELL;
	public static final PreRegisteredLOTRBiome RIVENDELL_HILLS;
	public static final PreRegisteredLOTRBiome FURTHER_GONDOR;
	public static final PreRegisteredLOTRBiome SHIRE_MOORS;
	public static final PreRegisteredLOTRBiome WHITE_DOWNS;
	public static final PreRegisteredLOTRBiome MIDGEWATER;
	public static final PreRegisteredLOTRBiome SWANFLEET;
	public static final PreRegisteredLOTRBiome GLADDEN_FIELDS;
	public static final PreRegisteredLOTRBiome LONG_MARSHES;
	public static final PreRegisteredLOTRBiome NINDALF;
	public static final PreRegisteredLOTRBiome DEAD_MARSHES;
	public static final PreRegisteredLOTRBiome MOUTHS_OF_ENTWASH;
	public static final PreRegisteredLOTRBiome ETHIR_ANDUIN;
	public static final PreRegisteredLOTRBiome SHIRE_MARSHES;
	public static final PreRegisteredLOTRBiome NURN_MARSHES;
	public static final PreRegisteredLOTRBiome GORGOROTH;
	public static final PreRegisteredLOTRBiome FOROCHEL;
	public static final PreRegisteredLOTRBiome LOTHLORIEN_EAVES;
	public static final PreRegisteredLOTRBiome HARAD_HALF_DESERT;
	public static final PreRegisteredLOTRBiome HARAD_DESERT_HILLS;
	public static final PreRegisteredLOTRBiome LOSTLADEN;
	public static final PreRegisteredLOTRBiome EASTERN_DESOLATION;
	public static final PreRegisteredLOTRBiome NORTHERN_MIRKWOOD;
	public static final PreRegisteredLOTRBiome MIRKWOOD_MOUNTAINS;
	public static final PreRegisteredLOTRBiome MORGUL_VALE;
	public static final PreRegisteredLOTRBiome DENSE_NORTHLANDS_FOREST;
	public static final PreRegisteredLOTRBiome SNOWY_NORTHLANDS;
	public static final PreRegisteredLOTRBiome SNOWY_NORTHLANDS_FOREST;
	public static final PreRegisteredLOTRBiome DENSE_SNOWY_NORTHLANDS_FOREST;
	public static final PreRegisteredLOTRBiome TOWER_HILLS;
	public static final PreRegisteredLOTRBiome SOUTHRON_COASTS_FOREST;
	public static final PreRegisteredLOTRBiome UMBAR_FOREST;
	public static final PreRegisteredLOTRBiome UMBAR_HILLS;
	public static final PreRegisteredLOTRBiome FIELD_OF_CORMALLEN;
	public static final PreRegisteredLOTRBiome HILLS_OF_EVENDIM;
	public static final SurfaceBuilder MIDDLE_EARTH_SURFACE;

	static {
		BIOMES = DeferredRegister.create(ForgeRegistries.BIOMES, "lotr");
		SURFACE_BUILDERS = DeferredRegister.create(ForgeRegistries.SURFACE_BUILDERS, "lotr");
		PRE_REGISTRY = new ArrayList();
		BIOME_WRAPPER_OBJECTS = new ArrayList();
		BIOME_WRAPPER_OBJECTS_BY_NAME = new HashMap();
		SHIRE = prepare("shire", () -> new ShireBiome(true));
		MORDOR = prepare("mordor", () -> new MordorBiome(true));
		ANORIEN = prepare("anorien", () -> new AnorienBiome(true));
		ROHAN = prepare("rohan", () -> new RohanBiome(true));
		MISTY_MOUNTAINS = prepare("misty_mountains", () -> new MistyMountainsBiome(true));
		SHIRE_WOODLANDS = prepare("shire_woodlands", () -> new ShireWoodlandsBiome(false));
		RIVER = prepare("river", () -> new MERiverBiome(false));
		TROLLSHAWS = prepare("trollshaws", () -> new TrollshawsBiome(true));
		BLUE_MOUNTAINS = prepare("blue_mountains", () -> new BlueMountainsBiome(true));
		ERIADOR = prepare("eriador", () -> new EriadorBiome(true));
		LONE_LANDS = prepare("lone_lands", () -> new LoneLandsBiome(true));
		ITHILIEN = prepare("ithilien", () -> new IthilienBiome(true));
		BROWN_LANDS = prepare("brown_lands", () -> new BrownLandsBiome(true));
		LOTHLORIEN = prepare("lothlorien", () -> new LothlorienBiome(true));
		IRON_HILLS = prepare("iron_hills", () -> new IronHillsBiome(true));
		DUNLAND = prepare("dunland", () -> new DunlandBiome(true));
		EMYN_MUIL = prepare("emyn_muil", () -> new EmynMuilBiome(true));
		LINDON = prepare("lindon", () -> new LindonBiome(true));
		SOUTHRON_COASTS = prepare("southron_coasts", () -> new SouthronCoastsBiome(true));
		NAN_CURUNIR = prepare("nan_curunir", () -> new NanCurunirBiome(true));
		FORODWAITH = prepare("forodwaith", () -> new ForodwaithBiome(true));
		EREGION = prepare("eregion", () -> new EregionBiome(true));
		MIRKWOOD = prepare("mirkwood", () -> new MirkwoodBiome(true));
		GREY_MOUNTAINS = prepare("grey_mountains", () -> new GreyMountainsBiome(true));
		WHITE_MOUNTAINS = prepare("white_mountains", () -> new WhiteMountainsBiome(true));
		FANGORN = prepare("fangorn", () -> new FangornBiome(true));
		WOODLAND_REALM = prepare("woodland_realm", () -> new WoodlandRealmBiome(true));
		DALE = prepare("dale", () -> new DaleBiome(true));
		ANGMAR = prepare("angmar", () -> new AngmarBiome(true));
		HARONDOR = prepare("harondor", () -> new HarondorBiome(true));
		ENEDWAITH = prepare("enedwaith", () -> new EnedwaithBiome(true));
		VALES_OF_ANDUIN = prepare("vales_of_anduin", () -> new AnduinValeBiome(true));
		ANDUIN_HILLS = prepare("anduin_hills", () -> new AnduinHillsBiome(true));
		WILDERLAND = prepare("wilderland", () -> new WilderlandBiome(true));
		OLD_FOREST = prepare("old_forest", () -> new OldForestBiome(true));
		BREELAND = prepare("breeland", () -> new BreelandBiome(true));
		CHETWOOD = prepare("chetwood", () -> new ChetwoodBiome(true));
		MISTY_MOUNTAINS_FOOTHILLS = prepare("misty_mountains_foothills", () -> new MistyMountainsBiome.Foothills(true));
		BLUE_MOUNTAINS_FOOTHILLS = prepare("blue_mountains_foothills", () -> new BlueMountainsBiome.Foothills(true));
		GREY_MOUNTAINS_FOOTHILLS = prepare("grey_mountains_foothills", () -> new GreyMountainsBiome.Foothills(true));
		WHITE_MOUNTAINS_FOOTHILLS = prepare("white_mountains_foothills", () -> new WhiteMountainsBiome.Foothills(true));
		MORDOR_MOUNTAINS = prepare("mordor_mountains", () -> new MordorBiome.Mountains(true));
		FORODWAITH_MOUNTAINS = prepare("forodwaith_mountains", () -> new ForodwaithBiome.Mountains(true));
		ANGMAR_MOUNTAINS = prepare("angmar_mountains", () -> new AngmarBiome.Mountains(true));
		NURN = prepare("nurn", () -> new NurnBiome(true));
		UMBAR = prepare("umbar", () -> new UmbarBiome(true));
		HARAD_DESERT = prepare("harad_desert", () -> new HaradDesertBiome(true));
		LINDON_WOODLANDS = prepare("lindon_woodlands", () -> new LindonBiome.Woodlands(true));
		ERIADOR_DOWNS = prepare("eriador_downs", () -> new EriadorBiome.Downs(true));
		LONE_LANDS_HILLS = prepare("lone_lands_hills", () -> new LoneLandsBiome.Hills(true));
		NORTHLANDS = prepare("northlands", () -> new NorthlandsBiome(true));
		NORTHLANDS_FOREST = prepare("northlands_forest", () -> new NorthlandsBiome.Forest(true));
		SEA = prepare("sea", () -> new SeaBiome(false));
		ISLAND = prepare("island", () -> new SeaBiome.Island(false));
		BEACH = prepare("beach", () -> new SeaBiome.Beach(false));
		TOLFALAS = prepare("tolfalas", () -> new TolfalasBiome(false));
		LAKE = prepare("lake", () -> new LakeBiome(false));
		NURNEN = prepare("nurnen", () -> new NurnBiome.Sea(false));
		DOR_EN_ERNIL = prepare("dor_en_ernil", () -> new DorEnErnilBiome(true));
		EMYN_EN_ERNIL = prepare("emyn_en_ernil", () -> new DorEnErnilBiome.Hills(true));
		WESTERN_ISLES = prepare("western_isles", () -> new SeaBiome.WesternIsles(false));
		COLDFELLS = prepare("coldfells", () -> new ColdfellsBiome(true));
		ETTENMOORS = prepare("ettenmoors", () -> new EttenmoorsBiome(true));
		HARNENNOR = prepare("harnennor", () -> new HarnennorBiome(true));
		DAGORLAD = prepare("dagorlad", () -> new DagorladBiome(true));
		WHITE_BEACH = prepare("white_beach", () -> new SeaBiome.WhiteBeach(false));
		DORWINION = prepare("dorwinion", () -> new DorwinionBiome(true));
		EMYN_WINION = prepare("emyn_winion", () -> new DorwinionBiome.Hills(true));
		WOLD = prepare("wold", () -> new RohanBiome.Wold(true));
		MINHIRIATH = prepare("minhiriath", () -> new EriadorBiome.Minhiriath(true));
		ERYN_VORN = prepare("eryn_vorn", () -> new EriadorBiome.ErynVorn(true));
		DRUWAITH_IAUR = prepare("druwaith_iaur", () -> new DruwaithIaurBiome(true));
		ANDRAST = prepare("andrast", () -> new AndrastBiome(true));
		LOSSARNACH = prepare("lossarnach", () -> new LossarnachBiome(true));
		LEBENNIN = prepare("lebennin", () -> new LebenninBiome(true));
		PELARGIR = prepare("pelargir", () -> new PelargirBiome(true));
		LAMEDON = prepare("lamedon", () -> new LamedonBiome(true));
		LAMEDON_HILLS = prepare("lamedon_hills", () -> new LamedonBiome.Hills(true));
		BLACKROOT_VALE = prepare("blackroot_vale", () -> new BlackrootValeBiome(true));
		PINNATH_GELIN = prepare("pinnath_gelin", () -> new PinnathGelinBiome(true));
		ANFALAS = prepare("anfalas", () -> new AnfalasBiome(true));
		NORTHERN_WILDERLAND = prepare("northern_wilderland", () -> new WilderlandBiome.Northern(true));
		NORTHERN_DALE = prepare("northern_dale", () -> new DaleBiome.Northern(true));
		RIVENDELL = prepare("rivendell", () -> new RivendellBiome(true));
		RIVENDELL_HILLS = prepare("rivendell_hills", () -> new RivendellBiome.Hills(true));
		FURTHER_GONDOR = prepare("further_gondor", () -> new FurtherGondorBiome(true));
		SHIRE_MOORS = prepare("shire_moors", () -> new ShireBiome.Moors(true));
		WHITE_DOWNS = prepare("white_downs", () -> new ShireBiome.WhiteDowns(true));
		MIDGEWATER = prepare("midgewater", () -> new MidgewaterBiome(true));
		SWANFLEET = prepare("swanfleet", () -> new SwanfleetBiome(true));
		GLADDEN_FIELDS = prepare("gladden_fields", () -> new GladdenFieldsBiome(true));
		LONG_MARSHES = prepare("long_marshes", () -> new LongMarshesBiome(true));
		NINDALF = prepare("nindalf", () -> new NindalfBiome(true));
		DEAD_MARSHES = prepare("dead_marshes", () -> new DeadMarshesBiome(true));
		MOUTHS_OF_ENTWASH = prepare("mouths_of_entwash", () -> new MouthsOfEntwashBiome(true));
		ETHIR_ANDUIN = prepare("ethir_anduin", () -> new EthirAnduinBiome(true));
		SHIRE_MARSHES = prepare("shire_marshes", () -> new ShireBiome.Marshes(true));
		NURN_MARSHES = prepare("nurn_marshes", () -> new NurnBiome.Marshes(true));
		GORGOROTH = prepare("gorgoroth", () -> new GorgorothBiome(true));
		FOROCHEL = prepare("forochel", () -> new ForochelBiome(true));
		LOTHLORIEN_EAVES = prepare("lothlorien_eaves", () -> new LothlorienBiome.Eaves(true));
		HARAD_HALF_DESERT = prepare("harad_half_desert", () -> new HaradDesertBiome.HalfDesert(true));
		HARAD_DESERT_HILLS = prepare("harad_desert_hills", () -> new HaradDesertBiome.Hills(true));
		LOSTLADEN = prepare("lostladen", () -> new LostladenBiome(true));
		EASTERN_DESOLATION = prepare("eastern_desolation", () -> new EasternDesolationBiome(true));
		NORTHERN_MIRKWOOD = prepare("northern_mirkwood", () -> new MirkwoodBiome.Northern(true));
		MIRKWOOD_MOUNTAINS = prepare("mirkwood_mountains", () -> new MirkwoodBiome.Mountains(true));
		MORGUL_VALE = prepare("morgul_vale", () -> new MorgulValeBiome(true));
		DENSE_NORTHLANDS_FOREST = prepare("dense_northlands_forest", () -> new NorthlandsBiome.DenseForest(false));
		SNOWY_NORTHLANDS = prepare("snowy_northlands", () -> new NorthlandsBiome.SnowyNorthlands(true));
		SNOWY_NORTHLANDS_FOREST = prepare("snowy_northlands_forest", () -> new NorthlandsBiome.SnowyForest(true));
		DENSE_SNOWY_NORTHLANDS_FOREST = prepare("dense_snowy_northlands_forest", () -> new NorthlandsBiome.DenseSnowyForest(false));
		TOWER_HILLS = prepare("tower_hills", () -> new TowerHillsBiome(true));
		SOUTHRON_COASTS_FOREST = prepare("southron_coasts_forest", () -> new SouthronCoastsBiome.Forest(true));
		UMBAR_FOREST = prepare("umbar_forest", () -> new UmbarBiome.Forest(true));
		UMBAR_HILLS = prepare("umbar_hills", () -> new UmbarBiome.Hills(true));
		FIELD_OF_CORMALLEN = prepare("field_of_cormallen", () -> new IthilienBiome.Cormallen(true));
		HILLS_OF_EVENDIM = prepare("hills_of_evendim", () -> new EriadorBiome.EvendimHills(true));
		MIDDLE_EARTH_SURFACE = preRegSurfaceBuilder("middle_earth", new MiddleEarthSurfaceBuilder(MiddleEarthSurfaceConfig.CODEC));
	}

	public static boolean areBiomesEqual(Biome biome1, Biome biome2, IWorld world) {
		return getBiomeRegistryName(biome1, world).equals(getBiomeRegistryName(biome2, world));
	}

	public static Biome getBiomeByID(int biomeID, IWorld world) {
		return (Biome) getBiomeRegistry(world).byId(biomeID);
	}

	public static Biome getBiomeByRegistryName(ResourceLocation biomeName, IWorld world) {
		return (Biome) getBiomeRegistry(world).get(biomeName);
	}

	public static ITextComponent getBiomeDisplayName(Biome biome, IWorld world) {
		ResourceLocation key = getBiomeRegistryName(biome, world);
		return new TranslationTextComponent(String.format("biome.%s.%s", key.getNamespace(), key.getPath()));
	}

	public static int getBiomeID(Biome biome, IWorld world) {
		return getBiomeRegistry(world).getId(biome);
	}

	public static int getBiomeID(LOTRBiomeWrapper biomeWrapper, IWorld world) {
		return getBiomeIDByRegistryName(biomeWrapper.getBiomeRegistryName(), world);
	}

	public static int getBiomeID(PreRegisteredLOTRBiome preparedBiome, IWorld world) {
		return getBiomeIDByRegistryName(preparedBiome.getRegistryName(), world);
	}

	public static int getBiomeIDByRegistryName(ResourceLocation biomeName, IWorld world) {
		MutableRegistry reg = getBiomeRegistry(world);
		return reg.getId(reg.get(biomeName));
	}

	private static MutableRegistry getBiomeRegistry(IWorld world) {
		return world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
	}

	public static ResourceLocation getBiomeRegistryName(Biome biome, IWorld world) {
		return biome.getRegistryName() != null ? biome.getRegistryName() : getBiomeRegistry(world).getKey(biome);
	}

	public static ServerWorld getServerBiomeContextWorld() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server != null ? server.overworld() : null;
	}

	public static LOTRBiomeWrapper getWrapperFor(Biome biome, IWorld world) {
		ResourceLocation key = getBiomeRegistryName(biome, world);
		return BIOME_WRAPPER_OBJECTS_BY_NAME.containsKey(key) ? (LOTRBiomeWrapper) BIOME_WRAPPER_OBJECTS_BY_NAME.get(key) : VanillaPlaceholderLOTRBiome.makeWrapperFor(world, biome);
	}

	public static boolean isDefaultLOTRBiome(ResourceLocation biomeName) {
		return BIOME_WRAPPER_OBJECTS_BY_NAME.containsKey(biomeName);
	}

	public static List listAllBiomesForProvider(Registry lookupRegistry) {
		return (List) BIOME_WRAPPER_OBJECTS.stream().map(wrapper -> ((Biome) lookupRegistry.get(RegistryKey.create(Registry.BIOME_REGISTRY, ((LOTRBiomeWrapper) wrapper).getBiomeRegistryName())))).collect(Collectors.toList());
	}

	public static List listBiomeNamesForClassicGen() {
		new ArrayList();
		return (List) BIOME_WRAPPER_OBJECTS.stream().filter(hummel -> ((LOTRBiomeBase) hummel).isMajorBiome()).map(hummel -> ((LOTRBiomeBase) hummel).getBiomeRegistryName()).collect(Collectors.toList());
	}

	private static PreRegisteredLOTRBiome prepare(String name, NonNullSupplier wrapperSupplier) {
		PreRegisteredLOTRBiome preparedBiome = new PreRegisteredLOTRBiome(name, wrapperSupplier);
		PRE_REGISTRY.add(preparedBiome);
		return preparedBiome;
	}

	private static SurfaceBuilder preRegSurfaceBuilder(String name, SurfaceBuilder surfaceBuilder) {
		return (SurfaceBuilder) RegistryOrderHelper.preRegObject(SURFACE_BUILDERS, name, surfaceBuilder);
	}

	public static void register() {
		IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
		PRE_REGISTRY.forEach(preparedBiome -> {
			String name = ((PreRegisteredLOTRBiome) preparedBiome).getName();
			ResourceLocation fullResName = ((PreRegisteredLOTRBiome) preparedBiome).getRegistryName();
			LOTRBiomeBase lotrBiome = ((PreRegisteredLOTRBiome) preparedBiome).initialiseAndReturnBiomeWrapper(fullResName);
			BIOME_WRAPPER_OBJECTS.add(lotrBiome);
			BIOME_WRAPPER_OBJECTS_BY_NAME.put(fullResName, lotrBiome);
			BIOMES.register(name, ((PreRegisteredLOTRBiome) preparedBiome).supplyBiomeInitialiser());
		});
		BIOMES.register(bus);
		SURFACE_BUILDERS.register(bus);
	}
}
