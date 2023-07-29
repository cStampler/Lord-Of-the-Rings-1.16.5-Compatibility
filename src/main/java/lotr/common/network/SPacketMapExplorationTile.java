package lotr.common.network;

import java.util.BitSet;
import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketMapExplorationTile {
	private final int mapX;
	private final int mapZ;
	private final BitSet tileBits;

	public SPacketMapExplorationTile(int mapX, int mapZ, BitSet tileBits) {
		this.mapX = mapX;
		this.mapZ = mapZ;
		this.tileBits = tileBits;
	}

	public static SPacketMapExplorationTile decode(PacketBuffer buf) {
		int mapX = buf.readVarInt();
		int mapZ = buf.readVarInt();
		BitSet tileBits = BitSet.valueOf(buf.readByteArray());
		return new SPacketMapExplorationTile(mapX, mapZ, tileBits);
	}

	public static void encode(SPacketMapExplorationTile packet, PacketBuffer buf) {
		buf.writeVarInt(packet.mapX);
		buf.writeVarInt(packet.mapZ);
		buf.writeByteArray(packet.tileBits.toByteArray());
	}

	public static void handle(SPacketMapExplorationTile packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		FogDataModule fogData = LOTRLevelData.clientInstance().getData(player).getFogData();
		fogData.receiveSingleTileUpdateFromServer(packet.mapX, packet.mapZ, packet.tileBits);
		((Context) context.get()).setPacketHandled(true);
	}
}
