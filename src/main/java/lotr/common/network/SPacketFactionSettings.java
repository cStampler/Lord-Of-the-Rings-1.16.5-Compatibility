package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketFactionSettings {
	private final FactionSettings factionSettings;

	public SPacketFactionSettings(FactionSettings facs) {
		factionSettings = facs;
	}

	public static SPacketFactionSettings decode(PacketBuffer buf) {
		MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
		FactionSettings factionSettings = FactionSettings.read(mapSettings, buf);
		return new SPacketFactionSettings(factionSettings);
	}

	public static void encode(SPacketFactionSettings packet, PacketBuffer buf) {
		packet.factionSettings.write(buf);
	}

	public static void handle(SPacketFactionSettings packet, Supplier context) {
		FactionSettingsManager.clientInstance().loadClientFactionsFromServer(Minecraft.getInstance().getResourceManager(), packet.factionSettings);
		((Context) context.get()).setPacketHandled(true);
	}
}
