package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import lotr.common.fac.RankGender;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketChoosePreferredRankGender {
	private final RankGender preferredRankGender;

	public CPacketChoosePreferredRankGender(RankGender gender) {
		preferredRankGender = gender;
	}

	public static CPacketChoosePreferredRankGender decode(PacketBuffer buf) {
		int genderId = buf.readVarInt();
		RankGender preferredRankGender = RankGender.forNetworkID(genderId);
		if (preferredRankGender == null) {
			LOTRLog.warn("Received nonexistent preferred rank gender ID %d from client", genderId);
		}

		return new CPacketChoosePreferredRankGender(preferredRankGender);
	}

	public static void encode(CPacketChoosePreferredRankGender packet, PacketBuffer buf) {
		buf.writeVarInt(packet.preferredRankGender.networkID);
	}

	public static void handle(CPacketChoosePreferredRankGender packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		LOTRPlayerData pd = LOTRLevelData.serverInstance().getData(player);
		RankGender preferredRankGender = packet.preferredRankGender;
		if (preferredRankGender != null) {
			pd.getMiscData().setPreferredRankGender(preferredRankGender);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
