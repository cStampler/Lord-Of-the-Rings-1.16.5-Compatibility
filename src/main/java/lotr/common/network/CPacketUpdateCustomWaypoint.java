package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.LOTRLog;
import lotr.common.data.FastTravelDataModule;
import lotr.common.data.LOTRLevelData;
import lotr.common.item.RedBookItem;
import lotr.common.tileentity.CustomWaypointMarkerTileEntity;
import lotr.common.util.LOTRUtil;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.AbstractCustomWaypoint;
import lotr.common.world.map.CustomWaypoint;
import lotr.common.world.map.CustomWaypointStructureHandler;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketUpdateCustomWaypoint {
	private final int waypointId;
	private final String name;
	private final String lore;
	private final boolean isPublic;

	public CPacketUpdateCustomWaypoint(CustomWaypoint waypoint, String name, String lore, boolean isPublic) {
		this(waypoint.getCustomId(), name, lore, isPublic);
	}

	private CPacketUpdateCustomWaypoint(int waypointId, String name, String lore, boolean isPublic) {
		this.waypointId = waypointId;
		this.name = name;
		this.lore = lore;
		this.isPublic = isPublic;
	}

	public static CPacketUpdateCustomWaypoint decode(PacketBuffer buf) {
		int waypointId = buf.readVarInt();
		String name = buf.readUtf(40);
		String lore = buf.readUtf(160);
		boolean isPublic = buf.readBoolean();
		return new CPacketUpdateCustomWaypoint(waypointId, name, lore, isPublic);
	}

	private static void doUpdateCustomWaypoint(CPacketUpdateCustomWaypoint packet, ServerPlayerEntity player, FastTravelDataModule ftData) {
		ServerWorld world = player.getLevel();
		int waypointId = packet.waypointId;
		CustomWaypoint waypoint = ftData.getCustomWaypointById(waypointId);
		if (waypoint == null) {
			LOTRLog.warn("Player %s tried to update a custom waypoint with nonexistent ID %d", UsernameHelper.getRawUsername(player), waypointId);
		} else {
			String waypointName = packet.name.trim();
			String waypointLore = packet.lore.trim();
			if (waypointName.isEmpty()) {
				LOTRLog.warn("Player %s tried to update a custom waypoint with a blank name", UsernameHelper.getRawUsername(player));
			} else if (waypointName.length() > 40) {
				LOTRLog.warn("Player %s tried to update a custom waypoint with a name too long (%s)", UsernameHelper.getRawUsername(player), waypointName);
			} else if (waypointLore.length() > 160) {
				LOTRLog.warn("Player %s tried to update a custom waypoint with lore too long (%s)", UsernameHelper.getRawUsername(player), waypointLore);
			} else {
				boolean isPublic = packet.isPublic;
				BlockPos waypointPos = waypoint.getPosition();
				Vector3d playerPos = player.position();
				CustomWaypointMarkerTileEntity marker = CustomWaypointStructureHandler.INSTANCE.getAdjacentWaypointMarker(world, waypointPos, (AbstractCustomWaypoint) null);
				if (marker == null) {
					LOTRLog.warn("Player %s tried to update a custom waypoint where no waypoint structure exists (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
				} else if (!marker.matchesWaypointReference(waypoint)) {
					LOTRLog.warn("Player %s tried to update a custom waypoint at a marker which doesn't match the target waypoint (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
				} else if (waypointPos.distSqr(playerPos, false) >= 64.0D) {
					LOTRLog.warn("Player %s tried to update a custom waypoint on a block too far away (player pos = %s, clicked pos = %s)", UsernameHelper.getRawUsername(player), playerPos.toString(), waypointPos.toString());
				} else if (ftData.updateCustomWaypoint(world, waypoint, waypointName, waypointLore, isPublic)) {
					CustomWaypointStructureHandler.INSTANCE.updateWaypointStructure(player, waypoint);
					LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.cwp.update", waypointName));
					RedBookItem.playCompleteWaypointActionSound(world, waypointPos);
				}
			}
		}
	}

	public static void encode(CPacketUpdateCustomWaypoint packet, PacketBuffer buf) {
		buf.writeVarInt(packet.waypointId);
		buf.writeUtf(packet.name);
		buf.writeUtf(packet.lore);
		buf.writeBoolean(packet.isPublic);
	}

	public static void handle(CPacketUpdateCustomWaypoint packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		FastTravelDataModule ftData = LOTRLevelData.serverInstance().getData(player).getFastTravelData();
		doUpdateCustomWaypoint(packet, player, ftData);
		((Context) context.get()).setPacketHandled(true);
	}
}
