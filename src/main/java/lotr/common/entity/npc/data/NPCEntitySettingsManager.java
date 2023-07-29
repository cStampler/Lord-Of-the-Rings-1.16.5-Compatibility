package lotr.common.entity.npc.data;

import java.util.*;
import java.util.Map.Entry;

import com.google.gson.*;

import lotr.common.LOTRLog;
import lotr.common.fac.Faction;
import lotr.common.network.*;
import lotr.common.resources.InstancedJsonReloadListener;
import net.minecraft.entity.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldReader;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.registries.ForgeRegistries;

public class NPCEntitySettingsManager extends InstancedJsonReloadListener {
	private static final NPCEntitySettingsManager CLIENT_INSTANCE;
	private static final NPCEntitySettingsManager SERVER_INSTANCE;
	static {
		CLIENT_INSTANCE = new NPCEntitySettingsManager(LogicalSide.CLIENT);
		SERVER_INSTANCE = new NPCEntitySettingsManager(LogicalSide.SERVER);
	}

	private NPCEntitySettingsMap currentLoadedEntitySettings;

	private NPCEntitySettingsManager(LogicalSide side) {
		super("npcs/entity_settings", "NPCEntitySettings", side);
	}

	@Override
	protected void apply(Map jsons, IResourceManager serverResMgr, IProfiler profiler) {
		currentLoadedEntitySettings = loadEntitySettingsFromJsons(jsons);
		logEntitySettingsLoad("Loaded serverside NPC entity settings", currentLoadedEntitySettings);
	}

	public NPCEntitySettingsMap getCurrentLoadedEntitySettings() {
		return currentLoadedEntitySettings;
	}

	public void loadClientEntitySettingsFromServer(IResourceManager resMgr, NPCEntitySettingsMap settings) {
		currentLoadedEntitySettings = settings;
		logEntitySettingsLoad("Loaded clientside NPC entity settings from server", currentLoadedEntitySettings);
	}

	private NPCEntitySettingsMap loadEntitySettingsFromJsons(Map entityTypeJsons) {
		Map entityTypeMap = new HashMap();
		Iterator var4 = entityTypeJsons.entrySet().iterator();

		while (var4.hasNext()) {
			Entry entry = (Entry) var4.next();
			ResourceLocation res = (ResourceLocation) entry.getKey();
			JsonObject entityTypeJson = ((JsonElement) entry.getValue()).getAsJsonObject();

			try {
				EntityType entityType = lookupEntityTypeByName(res);
				if (entityType == null) {
					LOTRLog.error("Failed to load NPC entity settings for %s - no such entity type exists in the game!", res);
				} else {
					NPCEntitySettings entityTypeSettings = NPCEntitySettings.read(entityType, entityTypeJson);
					if (entityTypeSettings != null) {
						entityTypeMap.put(entityType, entityTypeSettings);
					}
				}
			} catch (Exception var10) {
				LOTRLog.warn("Failed to load NPC entity settings for %s from file", res);
				var10.printStackTrace();
			}
		}

		return new NPCEntitySettingsMap(entityTypeMap);
	}

	private void logEntitySettingsLoad(String prefix, NPCEntitySettingsMap settings) {
		LOTRLog.info("%s - %d entity type settings", prefix, settings.getSize());
	}

	public void sendEntitySettingsToPlayer(ServerPlayerEntity player) {
		SPacketNPCEntitySettings packet = new SPacketNPCEntitySettings(currentLoadedEntitySettings);
		LOTRPacketHandler.sendTo(packet, player);
	}

	public static NPCEntitySettingsManager clientInstance() {
		return CLIENT_INSTANCE;
	}

	public static Faction getEntityTypeFaction(Entity entity) {
		return getEntityTypeSettings(entity).getAssignedFaction(entity.level);
	}

	public static NPCEntitySettings getEntityTypeSettings(Entity entity) {
		return sidedInstance(entity.level).getCurrentLoadedEntitySettings().getEntityTypeSettings(entity.getType());
	}

	public static EntityType lookupEntityTypeByName(ResourceLocation name) {
		return ForgeRegistries.ENTITIES.getValue(name);
	}

	public static NPCEntitySettingsManager serverInstance() {
		return SERVER_INSTANCE;
	}

	public static NPCEntitySettingsManager sidedInstance(IWorldReader world) {
		return !world.isClientSide() ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}

	public static NPCEntitySettingsManager sidedInstance(LogicalSide side) {
		return side == LogicalSide.SERVER ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}
}
