package lotr.common.config;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.util.LOTRUtil;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.*;
import net.minecraftforge.eventbus.api.*;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig.*;

public class LOTRConfig {
	public static final LOTRConfig.ClientConfig CLIENT;
	public static final LOTRConfig.CommonConfig COMMON;
	public static final LOTRConfig.ServerConfig SERVER;
	public static final ForgeConfigSpec CLIENT_SPEC;
	public static final ForgeConfigSpec COMMON_SPEC;
	public static final ForgeConfigSpec SERVER_SPEC;

	static {
		Pair clientPair = new Builder().configure(LOTRConfig.ClientConfig::new);
		Pair commonPair = new Builder().configure(LOTRConfig.CommonConfig::new);
		Pair serverPair = new Builder().configure(LOTRConfig.ServerConfig::new);
		CLIENT = (LOTRConfig.ClientConfig) clientPair.getLeft();
		CLIENT_SPEC = (ForgeConfigSpec) clientPair.getRight();
		COMMON = (LOTRConfig.CommonConfig) commonPair.getLeft();
		COMMON_SPEC = (ForgeConfigSpec) commonPair.getRight();
		SERVER = (LOTRConfig.ServerConfig) serverPair.getLeft();
		SERVER_SPEC = (ForgeConfigSpec) serverPair.getRight();
	}

	private static final LOTRConfig.BoolHolder makeBool(List collection, Builder builder, String key, boolean def, boolean worldRestart, String comment) {
		builder.comment(comment).translation(String.format("config.%s.%s", "lotr", key));
		if (worldRestart) {
			builder.worldRestart();
		}

		BooleanValue boolVal = builder.define(key, def);
		LOTRConfig.BoolHolder holder = new LOTRConfig.BoolHolder(boolVal);
		collection.add(holder);
		return holder;
	}

	private static final LOTRConfig.BoolHolder makeBool(List collection, Builder builder, String key, boolean def, String comment) {
		return makeBool(collection, builder, key, def, false, comment);
	}

	private static final LOTRConfig.BoolHolder makeBoolRestart(List collection, Builder builder, String key, boolean def, String comment) {
		return makeBool(collection, builder, key, def, true, comment);
	}

	private static final LOTRConfig.IntHolder makeInt(List collection, Builder builder, String key, int def, int min, int max, boolean worldRestart, String comment) {
		builder.comment(comment).translation(String.format("config.%s.%s", "lotr", key));
		if (worldRestart) {
			builder.worldRestart();
		}

		IntValue intVal = builder.defineInRange(key, def, min, max);
		LOTRConfig.IntHolder holder = new LOTRConfig.IntHolder(intVal);
		collection.add(holder);
		return holder;
	}

	private static final LOTRConfig.IntHolder makeInt(List collection, Builder builder, String key, int def, String comment) {
		return makeIntBounded(collection, builder, key, def, Integer.MIN_VALUE, Integer.MAX_VALUE, comment);
	}

	private static final LOTRConfig.IntHolder makeIntBounded(List collection, Builder builder, String key, int def, int min, int max, String comment) {
		return makeInt(collection, builder, key, def, min, max, false, comment);
	}

	@SubscribeEvent
	public static void onModConfig(ModConfigEvent event) {
		ForgeConfigSpec spec = event.getConfig().getSpec();
		if (spec == CLIENT_SPEC) {
			CLIENT.bakeFields();
		} else if (spec == COMMON_SPEC) {
			COMMON.bakeFields();
		} else if (spec == SERVER_SPEC) {
			SERVER.bakeFields();
		}

	}

	public static void register(IEventBus fmlBus) {
		fmlBus.register(LOTRConfig.class);
		ModLoadingContext.get().registerConfig(Type.CLIENT, CLIENT_SPEC);
		ModLoadingContext.get().registerConfig(Type.COMMON, COMMON_SPEC);
		ModLoadingContext.get().registerConfig(Type.SERVER, SERVER_SPEC);
	}

	public static class BoolHolder extends LOTRConfig.ConfigValueHolder {
		public BoolHolder(ConfigValue cfgVal) {
			super(cfgVal);
		}

		public void toggleAndSave() {
			setAndSave(!(Boolean) get());
		}
	}

	public static class ClientConfig {
		private final List clientFields = new ArrayList();
		public final LOTRConfig.BoolHolder modSky;
		public final LOTRConfig.BoolHolder modClouds;
		public final LOTRConfig.IntHolder cloudRange;
		public final LOTRConfig.BoolHolder northernLights;
		public final LOTRConfig.BoolHolder sunGlare;
		public final LOTRConfig.BoolHolder rainMist;
		public final LOTRConfig.BoolHolder mistyMountainsMist;
		public final LOTRConfig.BoolHolder newWeatherRendering;
		public final LOTRConfig.BoolHolder newRainGroundParticles;
		public final LOTRConfig.BoolHolder modMainMenu;
		public final LOTRConfig.BoolHolder sepiaMap;
		public final LOTRConfig.BoolHolder mapLabels;
		public final LOTRConfig.BoolHolder showWorldTypeHelp;
		public final LOTRConfig.BoolHolder fogOfWar;
		public final LOTRConfig.BoolHolder imperialWaypointDistances;
		public final LOTRConfig.BoolHolder compass;
		public final LOTRConfig.BoolHolder compassInfo;
		public final LOTRConfig.BoolHolder showAlignmentEverywhere;
		public final LOTRConfig.IntHolder alignmentXOffset;
		public final LOTRConfig.IntHolder alignmentYOffset;
		public final LOTRConfig.BoolHolder immersiveSpeech;
		public final LOTRConfig.BoolHolder immersiveSpeechChatLog;
		public final LOTRConfig.IntHolder immersiveSpeechDuration;
		public final LOTRConfig.BoolHolder displayAlignmentAboveHead;
		public final LOTRConfig.IntHolder dolAmrothChestplateWings;
		public final LOTRConfig.BoolHolder mannishWomenUseAlexModelStyle;
		public final LOTRConfig.BoolHolder elfWomenUseAlexModelStyle;
		public final LOTRConfig.BoolHolder dwarfWomenUseAlexModelStyle;
		public final LOTRConfig.BoolHolder hobbitWomenUseAlexModelStyle;
		public final LOTRConfig.BoolHolder orcWomenUseAlexModelStyle;
		public final LOTRConfig.BoolHolder windAmbience;
		public final LOTRConfig.BoolHolder newRainSounds;
		public final LOTRConfig.BoolHolder newThunderSounds;

		public ClientConfig(Builder builder) {
			builder.push("environment");
			modSky = LOTRConfig.makeBoolRestart(clientFields, builder, "modSky", true, "Toggle the Middle-earth sky renderer");
			modClouds = LOTRConfig.makeBoolRestart(clientFields, builder, "modClouds", true, "Toggle the Middle-earth cloud renderer");
			cloudRange = LOTRConfig.makeIntBounded(clientFields, builder, "cloudRange", 1024, 0, Integer.MAX_VALUE, "");
			northernLights = LOTRConfig.makeBool(clientFields, builder, "northernLights", true, "The Aurora, or Northern Lights! May be a slightly performance-intensive feature for some users");
			sunGlare = LOTRConfig.makeBool(clientFields, builder, "sunGlare", true, "");
			rainMist = LOTRConfig.makeBool(clientFields, builder, "rainMist", true, "");
			mistyMountainsMist = LOTRConfig.makeBool(clientFields, builder, "mistyMountainsMist", true, "");
			newWeatherRendering = LOTRConfig.makeBoolRestart(clientFields, builder, "newWeatherRendering", true, "New rain and snow textures (in Middle-earth), ash, and sandstorms");
			newRainGroundParticles = LOTRConfig.makeBoolRestart(clientFields, builder, "newRainGroundParticles", true, "Replace rain ground particles in Middle-earth, to match the new rain textures");
			builder.pop();
			builder.push("gui");
			modMainMenu = LOTRConfig.makeBool(clientFields, builder, "modMainMenu", true, "Display the mod's custom main menu screen");
			sepiaMap = LOTRConfig.makeBool(clientFields, builder, "sepiaMap", false, "");
			mapLabels = LOTRConfig.makeBool(clientFields, builder, "mapLabels", true, "");
			showWorldTypeHelp = LOTRConfig.makeBool(clientFields, builder, "showWorldTypeHelp", true, "Will be automatically set to false after the world type help display has been shown once");
			fogOfWar = LOTRConfig.makeBool(clientFields, builder, "fogOfWar", false, "Toggle the map exploration fog. If a server has set forceFogOfWar to either force enable or disable the fog, that server setting will override this one.");
			imperialWaypointDistances = LOTRConfig.makeBool(clientFields, builder, "imperialWaypointDistances", false, "Display waypoint distances in leagues, miles, and yards instead of metric (blocks)");
			builder.pop();
			builder.push("hud");
			compass = LOTRConfig.makeBool(clientFields, builder, "compass", true, "Middle-earth on-screen compass");
			compassInfo = LOTRConfig.makeBool(clientFields, builder, "compassInfo", true, "On-screen compass: coordinates and biome name");
			showAlignmentEverywhere = LOTRConfig.makeBool(clientFields, builder, "showAlignmentEverywhere", false, "Display the alignment meter even in non-mod dimensions");
			alignmentXOffset = LOTRConfig.makeInt(clientFields, builder, "alignmentXOffset", 0, "Configure the x-position of the alignment meter on-screen. Negative values move it left, positive values right");
			alignmentYOffset = LOTRConfig.makeInt(clientFields, builder, "alignmentYOffset", 0, "Configure the y-position of the alignment meter on-screen. Negative values move it up, positive values down");
			immersiveSpeech = LOTRConfig.makeBool(clientFields, builder, "immersiveSpeech", true, "If set to true, NPC speech will appear on-screen with the NPC. If set to false, it will be sent to the chat box");
			immersiveSpeechChatLog = LOTRConfig.makeBool(clientFields, builder, "immersiveSpeechChatLog", false, "Toggle whether speech still shows in the chat box when Immersive Speech is enabled");
			immersiveSpeechDuration = LOTRConfig.makeIntBounded(clientFields, builder, "immersiveSpeechDuration", 10, 0, 60, "The duration (in seconds) of Immersive Speech displays");
			displayAlignmentAboveHead = LOTRConfig.makeBool(clientFields, builder, "displayAlignmentAboveHead", true, "Render other players' alignment values above their heads");
			builder.pop();
			builder.push("model");
			dolAmrothChestplateWings = LOTRConfig.makeIntBounded(clientFields, builder, "dolAmrothChestplateWings", 12, 0, 24, "The number of wings on the swan knights' chestplates");
			mannishWomenUseAlexModelStyle = LOTRConfig.makeBool(clientFields, builder, "mannishWomenUseAlexModelStyle", true, "If true, Mannish women NPCs will use the 'Alex' model style, with slimmer arms");
			elfWomenUseAlexModelStyle = LOTRConfig.makeBool(clientFields, builder, "elfWomenUseAlexModelStyle", true, "If true, Elf-women NPCs will use the 'Alex' model style, with slimmer arms");
			dwarfWomenUseAlexModelStyle = LOTRConfig.makeBool(clientFields, builder, "dwarfWomenUseAlexModelStyle", false, "If true, Dwarf-women NPCs will use the 'Alex' model style, with slimmer arms");
			hobbitWomenUseAlexModelStyle = LOTRConfig.makeBool(clientFields, builder, "hobbitWomenUseAlexModelStyle", false, "If true, Hobbit-women NPCs will use the 'Alex' model style, with slimmer arms");
			orcWomenUseAlexModelStyle = LOTRConfig.makeBool(clientFields, builder, "orcWomenUseAlexModelStyle", false, "If true, Orc-women NPCs will use the 'Alex' model style, with slimmer arms");
			builder.pop();
			builder.push("sound");
			windAmbience = LOTRConfig.makeBool(clientFields, builder, "windAmbience", true, "");
			newRainSounds = LOTRConfig.makeBool(clientFields, builder, "newRainSounds", true, "");
			newThunderSounds = LOTRConfig.makeBool(clientFields, builder, "newThunderSounds", true, "");
			builder.pop();
		}

		public void bakeFields() {
			clientFields.forEach(hummel -> ((LOTRConfig.ConfigValueHolder) hummel).bake());
		}

		public void toggleMapLabels() {
			mapLabels.toggleAndSave();
		}

		public void toggleSepia() {
			sepiaMap.toggleAndSave();
		}
	}

	public static class CommonConfig {
		private final List commonFields = new ArrayList();
		public final LOTRConfig.BoolHolder drunkSpeech;
		public final LOTRConfig.IntHolder npcTalkToPlayerMinDuration;
		public final LOTRConfig.IntHolder npcTalkToPlayerMaxDuration;
		public final LOTRConfig.BoolHolder areasOfInfluence;
		public final LOTRConfig.BoolHolder alignmentDraining;
		public final LOTRConfig.BoolHolder smallerBees;
		public final LOTRConfig.BoolHolder generateBiomeJsons;

		public CommonConfig(Builder builder) {
			builder.push("gameplay");
			drunkSpeech = LOTRConfig.makeBool(commonFields, builder, "drunkSpeech", true, "");
			npcTalkToPlayerMinDuration = LOTRConfig.makeIntBounded(commonFields, builder, "npcTalkToPlayerMinDuration", 5, 3, 60, "The minimum possible time (in seconds) for which an NPC will stand still and display talking animations when spoken to by a player");
			npcTalkToPlayerMaxDuration = LOTRConfig.makeIntBounded(commonFields, builder, "npcTalkToPlayerMaxDuration", 10, 3, 60, "The maximum possible time (in seconds) for which an NPC will stand still and display talking animations when spoken to by a player");
			areasOfInfluence = LOTRConfig.makeBool(commonFields, builder, "areasOfInfluence", true, "Alignment gains depend on factions' areas of influence");
			alignmentDraining = LOTRConfig.makeBool(commonFields, builder, "alignmentDraining", true, "Factions dislike if a player has + alignment with enemy factions");
			builder.pop();
			builder.push("mobs");
			smallerBees = LOTRConfig.makeBool(commonFields, builder, "smallerBees", true, "They're simply too big!");
			builder.pop();
			builder.push("datapack-utility");
			generateBiomeJsons = LOTRConfig.makeBool(commonFields, builder, "generateBiomeJsons", false, "If enabled, the mod will generate up-to-date biome JSON files for all its biomes and output them in the game directory during loading. These biome templates are provided to help datapack creators.");
			builder.pop();
		}

		public void bakeFields() {
			commonFields.forEach(hummel -> ((LOTRConfig.ConfigValueHolder) hummel).bake());
		}

		public int getRandomNPCTalkToPlayerDuration(Random rand) {
			int min = (Integer) npcTalkToPlayerMinDuration.get();
			int max = (Integer) npcTalkToPlayerMaxDuration.get();
			if (min > max) {
				min = (Integer) npcTalkToPlayerMaxDuration.get();
				max = (Integer) npcTalkToPlayerMinDuration.get();
			}

			return MathHelper.nextInt(rand, min, max);
		}
	}

	public abstract static class ConfigValueHolder {
		public final ConfigValue configValue;
		private Object value;

		public ConfigValueHolder(ConfigValue cfgVal) {
			configValue = cfgVal;
		}

		public void bake() {
			value = configValue.get();
		}

		public Object get() {
			return value;
		}

		public void setAndSave(Object newValue) {
			configValue.set(value = newValue);
			configValue.save();
		}
	}

	public static class IntHolder extends LOTRConfig.ConfigValueHolder {
		public IntHolder(ConfigValue cfgVal) {
			super(cfgVal);
		}
	}

	public static class ServerConfig {
		private final List serverFields = new ArrayList();
		public final LOTRConfig.IntHolder forceMapLocations;
		public final LOTRConfig.IntHolder forceFogOfWar;
		public final LOTRConfig.IntHolder playerDataClearingInterval;

		public ServerConfig(Builder builder) {
			builder.push("admin");
			forceMapLocations = LOTRConfig.makeIntBounded(serverFields, builder, "forceMapLocations", 0, 0, 2, "Force hide or show all players' map locations. 0 = per-player (default), 1 = force hide, 2 = force show");
			forceFogOfWar = LOTRConfig.makeIntBounded(serverFields, builder, "forceFogOfWar", 0, 0, 2, "Force enable or disable the map exploration fog for all players. 0 = set by players in their own configs (default), 1 = force fog, 2 = force no fog");
			playerDataClearingInterval = LOTRConfig.makeIntBounded(serverFields, builder, "playerDataClearingInterval", LOTRUtil.minutesToTicks(1), LOTRUtil.secondsToTicks(30), LOTRUtil.minutesToTicks(60), "Tick interval between clearing offline LOTR-playerdata from the cache. Offline players' data is typically loaded to serve features like fellowships and their shared custom waypoints. Higher values may reduce server lag, as data will have to be reloaded from disk less often, but will result in higher RAM usage to some extent");
			builder.pop();
		}

		public void bakeFields() {
			serverFields.forEach(hummel -> ((LOTRConfig.ConfigValueHolder) hummel).bake());
		}
	}
}
