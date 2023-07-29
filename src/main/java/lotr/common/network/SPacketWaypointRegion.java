package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.data.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketWaypointRegion {
	private final WaypointRegion region;
	private final boolean isUnlocked;

	public SPacketWaypointRegion(WaypointRegion r, boolean unlock) {
		region = r;
		isUnlocked = unlock;
	}

	public static SPacketWaypointRegion decode(PacketBuffer buf) {
		MapSettings mapSettings = MapSettingsManager.clientInstance().getCurrentLoadedMap();
		WaypointRegion region = mapSettings.getWaypointRegionByID(buf.readInt());
		boolean isUnlocked = buf.readBoolean();
		return new SPacketWaypointRegion(region, isUnlocked);
	}

	public static void encode(SPacketWaypointRegion packet, PacketBuffer buf) {
		buf.writeInt(packet.region.getAssignedId());
		buf.writeBoolean(packet.isUnlocked);
	}

	public static void handle(SPacketWaypointRegion packet, Supplier context) {
		PlayerEntity player = LOTRMod.PROXY.getClientPlayer();
		LOTRPlayerData pd = LOTRLevelData.clientInstance().getData(player);
		if (packet.isUnlocked) {
			pd.getFastTravelData().unlockWaypointRegion(packet.region);
		} else {
			pd.getFastTravelData().lockWaypointRegion(packet.region);
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
