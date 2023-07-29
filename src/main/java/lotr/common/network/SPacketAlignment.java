package lotr.common.network;

import java.util.*;
import java.util.function.*;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.fac.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketAlignment {
	private final Map alignmentMap;
	private final UUID otherPlayerId;

	public SPacketAlignment(Faction faction, float alignment) {
		this(ImmutableMap.of(faction, alignment), (UUID) null);
	}

	public SPacketAlignment(Faction faction, float alignment, PlayerEntity otherPlayer) {
		this(ImmutableMap.of(faction, alignment), otherPlayer);
	}

	public SPacketAlignment(Map alignmentMap, PlayerEntity otherPlayer) {
		this(alignmentMap, otherPlayer.getUUID());
	}

	private SPacketAlignment(Map alignmentMap, UUID otherPlayerId) {
		this.alignmentMap = alignmentMap;
		this.otherPlayerId = otherPlayerId;
	}

	public static SPacketAlignment decode(PacketBuffer buf) {
		FactionSettings facSettings = FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
		Map alignmentMap = DataUtil.readNewMapFromBuffer(buf, HashMap::new, () -> {
			int factionId = buf.readVarInt();
			Faction faction = facSettings.getFactionByID(factionId);
			float alignment = buf.readFloat();
			if (faction == null) {
				LOTRLog.warn("Alignment update packet received nonexistent faction ID %d from server", factionId);
				return null;
			}
			return Pair.of(faction, alignment);
		});
		UUID otherPlayerId = (UUID) DataUtil.readNullableFromBuffer(buf, hummel -> ((PacketBuffer) hummel).readUUID());
		return new SPacketAlignment(alignmentMap, otherPlayerId);
	}

	public static void encode(SPacketAlignment packet, PacketBuffer buf) {
		DataUtil.writeMapToBuffer(buf, packet.alignmentMap, (faction, alignment) -> {
			buf.writeVarInt(((Faction) faction).getAssignedId());
			buf.writeFloat((float) alignment);
		});
		DataUtil.writeNullableToBuffer(buf, packet.otherPlayerId, (BiFunction) (hummel, hummel2) -> ((PacketBuffer) hummel).writeUUID((UUID) hummel2));
	}

	public static void handle(SPacketAlignment packet, Supplier context) {
		LOTRPlayerData pd;
		if (packet.otherPlayerId != null) {
			pd = LOTRLevelData.clientInstance().getData(LOTRMod.PROXY.getClientWorld(), packet.otherPlayerId);
		} else {
			PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
			pd = LOTRLevelData.clientInstance().getData(player);
		}

		AlignmentDataModule alignData = pd.getAlignmentData();
		packet.alignmentMap.forEach((hummel, hummel2) -> alignData.setAlignment((Faction) hummel, (float) hummel2));
		((Context) context.get()).setPacketHandled(true);
	}
}
