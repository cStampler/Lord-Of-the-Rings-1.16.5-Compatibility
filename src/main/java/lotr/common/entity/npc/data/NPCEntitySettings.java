package lotr.common.entity.npc.data;

import java.util.Optional;
import java.util.function.BiFunction;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import lotr.common.fac.*;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorldReader;

public class NPCEntitySettings {
	private final EntityType entityType;
	private final ResourceLocation factionName;
	private final float killAlignmentBonus;
	private final ResourceLocation speechbank;

	private NPCEntitySettings(EntityType entityType, ResourceLocation factionName, float killAlignmentBonus, ResourceLocation speechbank) {
		this.entityType = entityType;
		this.factionName = factionName;
		this.killAlignmentBonus = killAlignmentBonus;
		this.speechbank = speechbank;
	}

	public Faction getAssignedFaction(IWorldReader world) {
		FactionSettings facSettings = FactionSettingsManager.sidedInstance(world).getCurrentLoadedFactions();
		if (factionName != null) {
			Faction fac = facSettings.getFactionByName(factionName);
			if (fac != null) {
				return fac;
			}

			LOTRLog.error("Entity type %s has assigned faction %s - but no such faction is loaded!", entityType.getRegistryName(), factionName);
		}

		return facSettings.getFactionByPointer(FactionPointers.UNALIGNED);
	}

	public EntityType getEntityType() {
		return entityType;
	}

	public float getKillAlignmentBonus() {
		return killAlignmentBonus;
	}

	public Optional getSpeechbank() {
		return Optional.ofNullable(speechbank);
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(entityType.getRegistryName());
		DataUtil.writeNullableToBuffer(buf, factionName, (BiFunction) (hummel, hummel2) -> ((PacketBuffer) hummel).writeResourceLocation((ResourceLocation) hummel2));
		buf.writeFloat(killAlignmentBonus);
		DataUtil.writeNullableToBuffer(buf, speechbank, (BiFunction) (hummel, hummel2) -> ((PacketBuffer) hummel).writeResourceLocation((ResourceLocation) hummel2));
	}

	public static NPCEntitySettings createEmptyFallbackSettings(EntityType entityType) {
		return new NPCEntitySettings(entityType, FactionPointers.UNALIGNED.getName(), 0.0F, (ResourceLocation) null);
	}

	public static NPCEntitySettings read(EntityType entityType, JsonObject json) {
		ResourceLocation factionName = null;
		if (json.has("faction")) {
			String factionNameJson = json.get("faction").getAsString();
			if (!factionNameJson.isEmpty()) {
				factionName = new ResourceLocation(factionNameJson);
			}
		}

		float killAlignmentBonus = 0.0F;
		if (json.has("kill_bonus")) {
			killAlignmentBonus = json.get("kill_bonus").getAsFloat();
		}

		ResourceLocation speechbank = null;
		if (json.has("speechbank")) {
			speechbank = new ResourceLocation(json.get("speechbank").getAsString());
		}

		return new NPCEntitySettings(entityType, factionName, killAlignmentBonus, speechbank);
	}

	public static NPCEntitySettings read(PacketBuffer buf) {
		ResourceLocation entityTypeName = buf.readResourceLocation();
		buf.getClass();
		ResourceLocation factionName = (ResourceLocation) DataUtil.readNullableFromBuffer(buf, buf::readResourceLocation);
		float killAlignmentBonus = buf.readFloat();
		buf.getClass();
		ResourceLocation speechbank = (ResourceLocation) DataUtil.readNullableFromBuffer(buf, buf::readResourceLocation);
		EntityType entityType = NPCEntitySettingsManager.lookupEntityTypeByName(entityTypeName);
		if (entityType == null) {
			LOTRLog.warn("Received NPC entity type settings from server for a nonexistent entity type %s", entityTypeName);
			return null;
		}
		return new NPCEntitySettings(entityType, factionName, killAlignmentBonus, speechbank);
	}
}
