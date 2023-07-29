package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import lotr.common.world.map.MapExploration;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketMapExplorationFull extends ByteArrayPacket {
	private SPacketMapExplorationFull(byte[] data) {
		super(data);
	}

	public SPacketMapExplorationFull(MapExploration mapExploration) {
		super(hummel -> mapExploration.write((PacketBuffer) hummel));
	}

	public static SPacketMapExplorationFull decode(PacketBuffer buf) {
		return (SPacketMapExplorationFull) decodeByteData(buf, hummel -> new SPacketMapExplorationFull((byte[]) hummel));
	}

	public static void encode(SPacketMapExplorationFull packet, PacketBuffer buf) {
		encodeByteData(packet, buf);
	}

	public static void handle(SPacketMapExplorationFull packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FogDataModule fogData = LOTRLevelData.clientInstance().getData(player).getFogData();
		fogData.receiveFullGridFromServer(packet.getBufferedByteData());
		((Context) context.get()).setPacketHandled(true);
	}
}
