package lotr.common.network;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.data.DataUtil;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketNotifyAlignRequirement {
	private final List<Faction> anyOfFactions;
	private final float alignmentRequired;

	public SPacketNotifyAlignRequirement(List<Faction> anyOfFactions, float alignmentRequired) {
		this.anyOfFactions = anyOfFactions;
		this.alignmentRequired = alignmentRequired;
	}

	public float getAlignmentRequired() {
		return alignmentRequired;
	}

	public List<Faction> getAnyOfFactions() {
		return anyOfFactions;
	}

	public static SPacketNotifyAlignRequirement decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		List<Faction> anyOfFactions = DataUtil.readNewCollectionFromBuffer(buf, ArrayList::new, () -> {
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

	public static void handle(SPacketNotifyAlignRequirement packet, Supplier<NetworkEvent.Context> context) {
		LOTRMod.PROXY.receiveNotifyAlignRequirementPacket(packet);
		((Context) context.get()).setPacketHandled(true);
	}
}
