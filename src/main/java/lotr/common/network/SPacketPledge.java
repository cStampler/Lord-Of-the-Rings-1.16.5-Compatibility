package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketPledge {
	private final Faction faction;

	public SPacketPledge(Faction faction) {
		this.faction = faction;
	}

	public static SPacketPledge decode(PacketBuffer buf) {
		int factionId = buf.readVarInt();
		if (factionId == -1) {
			return new SPacketPledge((Faction) null);
		}
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		Faction faction = facSettings.getFactionByID(factionId);
		if (faction == null) {
			LOTRLog.warn("Received nonexistent pledge faction ID %d from server", factionId);
		}

		return new SPacketPledge(faction);
	}

	public static void encode(SPacketPledge packet, PacketBuffer buf) {
		Faction faction = packet.faction;
		buf.writeVarInt(faction != null ? faction.getAssignedId() : -1);
	}

	public static void handle(SPacketPledge packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		Faction faction = packet.faction;
		pd.getAlignmentData().setPledgeFaction(faction);
		((Context) context.get()).setPacketHandled(true);
	}
}
