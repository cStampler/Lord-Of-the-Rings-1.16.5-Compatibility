package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.entity.npc.data.NPCEntitySettingsManager;
import lotr.common.entity.npc.data.NPCEntitySettingsMap;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNPCEntitySettings {
	private final NPCEntitySettingsMap npcEntitySettingsMap;

	public SPacketNPCEntitySettings(NPCEntitySettingsMap settings) {
		npcEntitySettingsMap = settings;
	}

	public static SPacketNPCEntitySettings decode(PacketBuffer buf) {
		NPCEntitySettingsMap npcEntitySettingsMap = NPCEntitySettingsMap.read(buf);
		return new SPacketNPCEntitySettings(npcEntitySettingsMap);
	}

	public static void encode(SPacketNPCEntitySettings packet, PacketBuffer buf) {
		packet.npcEntitySettingsMap.write(buf);
	}

	public static void handle(SPacketNPCEntitySettings packet, Supplier context) {
		NPCEntitySettingsManager.clientInstance().loadClientEntitySettingsFromServer(Minecraft.getInstance().getResourceManager(), packet.npcEntitySettingsMap);
		((Context) context.get()).setPacketHandled(true);
	}
}
