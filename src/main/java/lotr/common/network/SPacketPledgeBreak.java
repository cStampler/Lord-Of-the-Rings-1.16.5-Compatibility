package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.fac.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketPledgeBreak {
	private final int cooldown;
	private final int cooldownStart;
	private final Faction brokenFaction;

	public SPacketPledgeBreak(int cooldown, int cooldownStart, Faction brokenFaction) {
		this.cooldown = cooldown;
		this.cooldownStart = cooldownStart;
		this.brokenFaction = brokenFaction;
	}

	public static SPacketPledgeBreak decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		int cooldown = buf.readInt();
		int cooldownStart = buf.readInt();
		int factionId = buf.readInt();
		Faction brokenFaction;
		if (factionId >= 0) {
			brokenFaction = facSettings.getFactionByID(factionId);
			if (brokenFaction == null) {
				LOTRLog.warn("Received nonexistent broken pledge faction ID %d from server", factionId);
			}
		} else {
			brokenFaction = null;
		}

		return new SPacketPledgeBreak(cooldown, cooldownStart, brokenFaction);
	}

	public static void encode(SPacketPledgeBreak packet, PacketBuffer buf) {
		buf.writeInt(packet.cooldown);
		buf.writeInt(packet.cooldownStart);
		Faction fac = packet.brokenFaction;
		if (fac != null) {
			buf.writeInt(fac.getAssignedId());
		} else {
			buf.writeInt(-1);
		}

	}

	public static void handle(SPacketPledgeBreak packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		AlignmentDataModule alignData = pd.getAlignmentData();
		alignData.setPledgeBreakCooldown(packet.cooldown);
		alignData.setPledgeBreakCooldownStart(packet.cooldownStart);
		alignData.setBrokenPledgeFaction(packet.brokenFaction);
		((Context) context.get()).setPacketHandled(true);
	}
}
