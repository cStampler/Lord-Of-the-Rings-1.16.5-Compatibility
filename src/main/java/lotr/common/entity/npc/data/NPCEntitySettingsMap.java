package lotr.common.entity.npc.data;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.data.DataUtil;
import net.minecraft.entity.EntityType;
import net.minecraft.network.PacketBuffer;

public class NPCEntitySettingsMap {
	private final Map entityTypeSettingsMap;

	public NPCEntitySettingsMap(Map map) {
		entityTypeSettingsMap = map;
	}

	public NPCEntitySettings getEntityTypeSettings(EntityType type) {
		return (NPCEntitySettings) entityTypeSettingsMap.getOrDefault(type, NPCEntitySettings.createEmptyFallbackSettings(type));
	}

	public int getSize() {
		return entityTypeSettingsMap.size();
	}

	public void write(PacketBuffer buf) {
		DataUtil.writeMapToBuffer(buf, entityTypeSettingsMap, (entityType, settings) -> {
			((NPCEntitySettings) settings).write(buf);
		});
	}

	public static NPCEntitySettingsMap read(PacketBuffer buf) {
		Map entityTypeSettingsMap = DataUtil.readNewMapFromBuffer(buf, HashMap::new, () -> {
			NPCEntitySettings settings = NPCEntitySettings.read(buf);
			return Pair.of(settings.getEntityType(), settings);
		});
		return new NPCEntitySettingsMap(entityTypeSettingsMap);
	}
}
