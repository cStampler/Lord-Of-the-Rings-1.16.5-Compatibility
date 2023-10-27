package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketMapSettings {
	private final MapSettings mapSettings;

	public SPacketMapSettings(MapSettings map) {
		mapSettings = map;
	}

	public static SPacketMapSettings decode(PacketBuffer buf) {
		MapSettings mapSettings = MapSettings.read(MapSettingsManager.clientInstance(), buf);
		return new SPacketMapSettings(mapSettings);
	}

	public static void encode(SPacketMapSettings packet, PacketBuffer buf) {
		packet.mapSettings.write(buf);
	}

	public static void handle(SPacketMapSettings packet, Supplier context) {
		MapSettingsManager.clientInstance().loadClientMapFromServer(Minecraft.getInstance().getResourceManager(), packet.mapSettings);
		((Context) context.get()).setPacketHandled(true);
	}
}
