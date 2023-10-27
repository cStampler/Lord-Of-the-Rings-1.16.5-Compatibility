package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.AlignmentDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketSetPledge {
	private final Faction faction;

	public CPacketSetPledge(Faction faction) {
		this.faction = faction;
	}

	public static CPacketSetPledge decode(PacketBuffer buf) {
		int factionId = buf.readVarInt();
		if (factionId == -1) {
			return new CPacketSetPledge((Faction) null);
		}
		FactionSettings facSettings = FactionSettingsManager.serverInstance().getCurrentLoadedFactions();
		Faction faction = facSettings.getFactionByID(factionId);
		if (faction == null) {
			LOTRLog.warn("Received nonexistent pledge faction ID %d from client", factionId);
		}

		return new CPacketSetPledge(faction);
	}

	public static void encode(CPacketSetPledge packet, PacketBuffer buf) {
		Faction faction = packet.faction;
		buf.writeVarInt(faction != null ? faction.getAssignedId() : -1);
	}

	public static void handle(CPacketSetPledge packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		AlignmentDataModule alignData = LOTRLevelData.serverInstance().getData(player).getAlignmentData();
		Faction faction = packet.faction;
		if (faction == null) {
			alignData.revokePledgeFaction(player, true);
		} else if (alignData.canMakeNewPledge()) {
			if (alignData.canPledgeToNow(faction)) {
				alignData.setPledgeFaction(faction);
			} else {
				LOTRLog.warn("Player %s tried to pledge to faction %s for which they don't meet the requirements", player.getDisplayName(), faction.getDisplayName());
			}
		} else {
			LOTRLog.warn("Player %s tried to make a new pledge when they can't yet", player.getDisplayName());
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
