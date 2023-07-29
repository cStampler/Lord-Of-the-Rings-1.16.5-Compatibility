package lotr.common.network;

import java.util.function.Supplier;

import lotr.common.*;
import lotr.common.data.*;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.util.*;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class CPacketFastTravel extends ByteArrayPacket {
	private CPacketFastTravel(byte[] data) {
		super(data);
	}

	public CPacketFastTravel(Waypoint wp) {
		super(buf -> {
			WaypointNetworkType.writeIdentification((PacketBuffer) buf, wp);
		});
	}

	public static CPacketFastTravel decode(PacketBuffer buf) {
		return (CPacketFastTravel) decodeByteData(buf, hummel -> new CPacketFastTravel((byte[]) hummel));
	}

	public static void encode(CPacketFastTravel packet, PacketBuffer buf) {
		encodeByteData(packet, buf);
	}

	public static void handle(CPacketFastTravel packet, Supplier context) {
		ServerPlayerEntity player = ((Context) context.get()).getSender();
		ServerWorld world = player.getLevel();
		if (!world.getGameRules().getBoolean(LOTRGameRules.FAST_TRAVEL)) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.disabled.gamerule"));
		} else if (!LOTRWorldTypes.hasMapFeatures(world)) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.disabled.worldtype"));
		} else {
			LOTRPlayerData playerData = LOTRLevelData.serverInstance().getData(player);
			FastTravelDataModule ftData = playerData.getFastTravelData();
			PacketBuffer waypointData = packet.getBufferedByteData();
			Waypoint waypoint = WaypointNetworkType.readFromIdentification(waypointData, playerData);
			if (waypoint != null && waypoint.verifyFastTravellable(world, player)) {
				if (waypoint.hasPlayerUnlocked(player)) {
					if (ftData.getTimeSinceFT() < ftData.getWaypointFTTime(waypoint, player)) {
						player.closeContainer();
						LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.moreTime", waypoint.getDisplayName()));
					} else {
						boolean underAttack = ftData.isUnderAttack(player);
						if (underAttack) {
							player.closeContainer();
							LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.underAttack"));
						} else if (player.isSleeping()) {
							player.closeContainer();
							LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.inBed"));
						} else {
							ftData.setTargetWaypoint(waypoint);
						}
					}
				} else {
					LOTRLog.warn("Player %s tried to FT to a waypoint (%s, %s) that they haven't unlocked", UsernameHelper.getRawUsername(player), waypoint.getClass().getSimpleName(), waypoint.getRawName());
				}
			}
		}

		((Context) context.get()).setPacketHandled(true);
	}
}
