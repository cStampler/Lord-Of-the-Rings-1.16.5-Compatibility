package lotr.common.network;

import java.util.*;
import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.DataUtil;
import lotr.common.fac.*;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNotifyAlignRequirement {
	private final List anyOfFactions;
	private final float alignmentRequired;

	public SPacketNotifyAlignRequirement(List anyOfFactions, float alignmentRequired) {
		this.anyOfFactions = anyOfFactions;
		this.alignmentRequired = alignmentRequired;
	}

	public float getAlignmentRequired() {
		return alignmentRequired;
	}

	public List getAnyOfFactions() {
		return anyOfFactions;
	}

	public static SPacketNotifyAlignRequirement decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		List anyOfFactions = (List) DataUtil.readNewCollectionFromBuffer(buf, ArrayList::new, () -> {
			int factionId = buf.readVarInt();
			Faction faction = facSettings.getFactionByID(factionId);
			if (faction == null) {
				LOTRLog.warn("Received nonexistent faction ID %d from server in notify alignment requirement packet", factionId);
			}

			return faction;
		});
		float alignmentRequired = buf.readFloat();
		return new SPacketNotifyAlignRequirement(anyOfFactions, alignmentRequired);
	}

	public static void encode(SPacketNotifyAlignRequirement packet, PacketBuffer buf) {
		DataUtil.writeCollectionToBuffer(buf, packet.anyOfFactions, fac -> {
			buf.writeVarInt(((Faction) fac).getAssignedId());
		});
		buf.writeFloat(packet.alignmentRequired);
	}

	public static void handle(SPacketNotifyAlignRequirement packet, Supplier context) {
		LOTRMod.PROXY.receiveNotifyAlignRequirementPacket(packet);
		((Context) context.get()).setPacketHandled(true);
	}
}
