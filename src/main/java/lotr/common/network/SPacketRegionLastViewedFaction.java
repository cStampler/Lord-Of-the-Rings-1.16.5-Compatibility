package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.fac.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketRegionLastViewedFaction {
	private final FactionRegion region;
	private final Faction faction;

	public SPacketRegionLastViewedFaction(FactionRegion region, Faction faction) {
		this.region = region;
		this.faction = faction;
	}

	public static SPacketRegionLastViewedFaction decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		int regionId = buf.readInt();
		int factionId = buf.readInt();
		FactionRegion region = facSettings.getRegionByID(regionId);
		Faction faction = facSettings.getFactionByID(factionId);
		if (region == null) {
			LOTRLog.warn("Received nonexistent faction region ID %d from server", regionId);
		}

		if (faction == null) {
			LOTRLog.warn("Received nonexistent faction ID %d from server", factionId);
		}

		return new SPacketRegionLastViewedFaction(region, faction);
	}

	public static void encode(SPacketRegionLastViewedFaction packet, PacketBuffer buf) {
		buf.writeInt(packet.region.getAssignedId());
		buf.writeInt(packet.faction.getAssignedId());
	}

	public static void handle(SPacketRegionLastViewedFaction packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		FactionRegion region = packet.region;
		Faction faction = packet.faction;
		if (region != null && faction != null) {
			pd.getAlignmentData().setRegionLastViewedFaction(region, faction);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
