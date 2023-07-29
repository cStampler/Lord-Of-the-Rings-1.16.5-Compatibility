package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRMod;
import lotr.common.config.ClientsideCurrentServerConfigSettings;
import lotr.common.data.LOTRLevelData;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketLoginLOTR {
	private BlockPos middleEarthPortalPos;
	private int wpCooldownMax;
	private int wpCooldownMin;
	private boolean areasOfInfluence;
	private boolean smallerBees;
	private boolean hasMapFeatures;
	private int forceFogOfWar;

	public void setAreasOfInfluence(boolean flag) {
		areasOfInfluence = flag;
	}

	public void setForceFogOfWar(int i) {
		forceFogOfWar = i;
	}

	public void setHasMapFeatures(boolean flag) {
		hasMapFeatures = flag;
	}

	public void setMiddleEarthPortalPos(int x, int y, int z) {
		middleEarthPortalPos = new BlockPos(x, y, z);
	}

	public void setSmallerBees(boolean flag) {
		smallerBees = flag;
	}

	public void setWaypointCooldownMaxMin(int max, int min) {
		wpCooldownMax = max;
		wpCooldownMin = min;
	}

	public static SPacketLoginLOTR decode(PacketBuffer buf) {
		SPacketLoginLOTR packet = new SPacketLoginLOTR();
		packet.middleEarthPortalPos = buf.readBlockPos();
		packet.wpCooldownMax = buf.readInt();
		packet.wpCooldownMin = buf.readInt();
		packet.areasOfInfluence = buf.readBoolean();
		packet.smallerBees = buf.readBoolean();
		packet.hasMapFeatures = buf.readBoolean();
		packet.forceFogOfWar = buf.readVarInt();
		return packet;
	}

	public static void encode(SPacketLoginLOTR packet, PacketBuffer buf) {
		buf.writeBlockPos(packet.middleEarthPortalPos);
		buf.writeInt(packet.wpCooldownMax);
		buf.writeInt(packet.wpCooldownMin);
		buf.writeBoolean(packet.areasOfInfluence);
		buf.writeBoolean(packet.smallerBees);
		buf.writeBoolean(packet.hasMapFeatures);
		buf.writeVarInt(packet.forceFogOfWar);
	}

	public static void handle(SPacketLoginLOTR packet, Supplier context) {
		World clientWorld = LOTRMod.PROXY.getClientWorld();
		LOTRLevelData levelData = LOTRLevelData.clientInstance();
		levelData.markMiddleEarthPortalLocation(clientWorld, packet.middleEarthPortalPos);
		levelData.setWaypointCooldown(clientWorld, packet.wpCooldownMax, packet.wpCooldownMin);
		ClientsideCurrentServerConfigSettings ccsConfig = ClientsideCurrentServerConfigSettings.INSTANCE;
		ccsConfig.areasOfInfluence = packet.areasOfInfluence;
		ccsConfig.smallerBees = packet.smallerBees;
		ccsConfig.hasMapFeatures = packet.hasMapFeatures;
		ccsConfig.forceFogOfWar = packet.forceFogOfWar;
		((Context) context.get()).setPacketHandled(true);
	}
}
