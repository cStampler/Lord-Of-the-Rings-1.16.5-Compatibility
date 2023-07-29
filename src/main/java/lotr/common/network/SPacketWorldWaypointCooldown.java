package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.LOTRLevelData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketWorldWaypointCooldown {
	private final int wpCooldownMax;
	private final int wpCooldownMin;

	public SPacketWorldWaypointCooldown(int max, int min) {
		wpCooldownMax = max;
		wpCooldownMin = min;
	}

	public static SPacketWorldWaypointCooldown decode(PacketBuffer buf) {
		int wpCooldownMax = buf.readInt();
		int wpCooldownMin = buf.readInt();
		return new SPacketWorldWaypointCooldown(wpCooldownMax, wpCooldownMin);
	}

	public static void encode(SPacketWorldWaypointCooldown packet, PacketBuffer buf) {
		buf.writeInt(packet.wpCooldownMax);
		buf.writeInt(packet.wpCooldownMin);
	}

	public static void handle(SPacketWorldWaypointCooldown packet, Supplier context) {
		World world = LOTRMod.PROXY.getClientWorld();
		LOTRLevelData.clientInstance().setWaypointCooldown(world, packet.wpCooldownMax, packet.wpCooldownMin);
		((Context) context.get()).setPacketHandled(true);
	}
}
