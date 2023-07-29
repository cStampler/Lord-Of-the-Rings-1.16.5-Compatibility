package lotr.common.network;

import java.util.function.Supplier;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketIsOpRequest {
	public static CPacketIsOpRequest decode(PacketBuffer buf) {
		return new CPacketIsOpRequest();
	}

	public static void encode(CPacketIsOpRequest packet, PacketBuffer buf) {
	}

	public static void handle(CPacketIsOpRequest packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		boolean isOp = isOpCanTeleport(player);
		SPacketIsOpResponse response = new SPacketIsOpResponse(isOp);
		LOTRPacketHandler.sendTo(response, player);
		((Context) context.get()).setPacketHandled(true);
	}

	public static boolean isOpCanTeleport(ServerPlayerEntity player) {
		return player.getServer().getProfilePermissions(player.getGameProfile()) >= 2;
	}
}
