package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.util.LOTRUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketMapTp {
	private final int xCoord;
	private final int zCoord;

	public CPacketMapTp(int x, int z) {
		xCoord = x;
		zCoord = z;
	}

	public static CPacketMapTp decode(PacketBuffer buf) {
		int xCoord = buf.readInt();
		int zCoord = buf.readInt();
		return new CPacketMapTp(xCoord, zCoord);
	}

	public static void encode(CPacketMapTp packet, PacketBuffer buf) {
		buf.writeInt(packet.xCoord);
		buf.writeInt(packet.zCoord);
	}

	public static void handle(CPacketMapTp packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		if (CPacketIsOpRequest.isOpCanTeleport(player)) {
			World world = player.level;
			int x = packet.xCoord;
			int z = packet.zCoord;
			int y = LOTRUtil.forceLoadChunkAndGetTopBlock(world, x, z);
			if (player.abilities.instabuild && player.abilities.flying) {
				BlockPos currentPos = player.blockPosition();
				int currentHeightAboveTop = currentPos.getY() - world.getHeightmapPos(Type.MOTION_BLOCKING_NO_LEAVES, currentPos).getY();
				if (currentHeightAboveTop > 0) {
					y += currentHeightAboveTop;
				}
			}

			String command = String.format("/tp %d %d %d", x, y, z);
			player.getServer().getCommands().performCommand(player.createCommandSourceStack(), command);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
