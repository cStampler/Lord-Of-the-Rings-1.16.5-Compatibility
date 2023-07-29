package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.fac.RankGender;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketPreferredRankGender {
	private final RankGender preferredRankGender;

	public SPacketPreferredRankGender(RankGender gender) {
		preferredRankGender = gender;
	}

	public static SPacketPreferredRankGender decode(PacketBuffer buf) {
		int genderId = buf.readVarInt();
		RankGender preferredRankGender = RankGender.forNetworkID(genderId);
		if (preferredRankGender == null) {
			LOTRLog.warn("Received nonexistent preferred rank gender ID %d from server", genderId);
		}

		return new SPacketPreferredRankGender(preferredRankGender);
	}

	public static void encode(SPacketPreferredRankGender packet, PacketBuffer buf) {
		buf.writeVarInt(packet.preferredRankGender.networkID);
	}

	public static void handle(SPacketPreferredRankGender packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		RankGender preferredRankGender = packet.preferredRankGender;
		if (preferredRankGender != null) {
			pd.getMiscData().setPreferredRankGender(preferredRankGender);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
