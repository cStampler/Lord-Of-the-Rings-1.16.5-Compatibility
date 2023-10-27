package lotr.common.network;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionRegion;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketViewedFactions {
	private final Faction currentViewedFaction;
	private final Map regionLastViewedFactions;

	public CPacketViewedFactions(Faction currentViewed, Map regionLastViewed) {
		currentViewedFaction = currentViewed;
		regionLastViewedFactions = regionLastViewed;
	}

	public static CPacketViewedFactions decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.serverInstance().getCurrentLoadedFactions();
		int currentFacId = buf.readVarInt();
		Faction currentFac = null;
		if (currentFacId >= 0) {
			currentFac = facSettings.getFactionByID(currentFacId);
			if (currentFac == null) {
				LOTRLog.warn("No faction for ID %d exists on the server!", currentFacId);
			}
		}

		Map regionMap = new HashMap();
		int regionMapSize = buf.readVarInt();

		for (int i = 0; i < regionMapSize; ++i) {
			int regionId = buf.readVarInt();
			int regionFacId = buf.readVarInt();
			FactionRegion region = facSettings.getRegionByID(regionId);
			Faction regionFac = facSettings.getFactionByID(regionFacId);
			if (region == null) {
				LOTRLog.warn("No faction region for ID %d exists on the server!", regionId);
			} else if (regionFac == null) {
				LOTRLog.warn("No faction for ID %d exists on the server!", regionFacId);
			} else {
				regionMap.put(region, regionFac);
			}
		}

		return new CPacketViewedFactions(currentFac, regionMap);
	}

	public static void encode(CPacketViewedFactions packet, PacketBuffer buf) {
		Faction currentViewedFaction = packet.currentViewedFaction;
		if (currentViewedFaction != null) {
			buf.writeVarInt(currentViewedFaction.getAssignedId());
		} else {
			buf.writeVarInt(-1);
		}

		buf.writeVarInt(packet.regionLastViewedFactions.size());
		packet.regionLastViewedFactions.forEach((region, faction) -> {
			buf.writeVarInt(((FactionRegion) region).getAssignedId());
			buf.writeVarInt(((Faction) faction).getAssignedId());
		});
	}

	public static void handle(CPacketViewedFactions packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		LOTRPlayerData playerData = LOTRLevelData.serverInstance().getData(player);
		AlignmentDataModule alignData = playerData.getAlignmentData();
		if (packet.currentViewedFaction != null) {
			alignData.setCurrentViewedFaction(packet.currentViewedFaction);
		}

		packet.regionLastViewedFactions.forEach((region, faction) -> {
			alignData.setRegionLastViewedFaction((FactionRegion) region, (Faction) faction);
		});
		((Context) context.get()).setPacketHandled(true);
	}
}
