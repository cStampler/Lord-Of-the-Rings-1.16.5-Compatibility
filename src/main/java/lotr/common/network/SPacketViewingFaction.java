package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketViewingFaction {
	private final Faction viewingFaction;

	public SPacketViewingFaction(Faction faction) {
		viewingFaction = faction;
	}

	public static SPacketViewingFaction decode(PacketBuffer buf) {
		int factionId = buf.readInt();
		Faction faction = FactionSettingsManager.clientInstance().getCurrentLoadedFactions().getFactionByID(factionId);
		if (faction == null) {
			LOTRLog.warn("Received nonexistent viewing faction ID %d from server", factionId);
		}

		return new SPacketViewingFaction(faction);
	}

	public static void encode(SPacketViewingFaction packet, PacketBuffer buf) {
		buf.writeInt(packet.viewingFaction.getAssignedId());
	}

	public static void handle(SPacketViewingFaction packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		Faction faction = packet.viewingFaction;
		if (faction != null) {
			pd.getAlignmentData().setCurrentViewedFaction(packet.viewingFaction);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
