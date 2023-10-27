package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.FactionStats;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketFactionStats extends ByteArrayPacket {
	private final Faction faction;

	private SPacketFactionStats(Faction faction, byte[] data) {
		super(data);
		this.faction = faction;
	}

	public SPacketFactionStats(Faction faction, FactionStats stats) {
		super(hummel -> stats.write((PacketBuffer) hummel));
		this.faction = faction;
	}

	public static SPacketFactionStats decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		int factionId = buf.readInt();
		Faction faction = facSettings.getFactionByID(factionId);
		if (faction == null) {
			LOTRLog.warn("Received faction stats update for nonexistent faction ID %d from server", factionId);
		}

		return (SPacketFactionStats) decodeByteData(buf, data -> new SPacketFactionStats(faction, (byte[]) data));
	}

	public static void encode(SPacketFactionStats packet, PacketBuffer buf) {
		buf.writeInt(packet.faction.getAssignedId());
		encodeByteData(packet, buf);
	}

	public static void handle(SPacketFactionStats packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		Faction faction = packet.faction;
		if (faction != null) {
			pd.getFactionStatsData().getFactionStats(faction).read(packet.getBufferedByteData());
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
