package lotr.common.network;

import lotr.common.LOTRLog;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.*;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.*;
import net.minecraftforge.fml.network.PacketDistributor.TargetPoint;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class LOTRPacketHandler {
	private static SimpleChannel CHANNEL;

	public static void register() {
		CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation("lotr", "main"), () -> "11", "11"::equals, "11"::equals);
		int id = 0;
		CHANNEL.registerMessage(id++, SPacketLoginLOTR.class, SPacketLoginLOTR::encode, SPacketLoginLOTR::decode, SPacketLoginLOTR::handle);
		CHANNEL.registerMessage(id++, SPacketRingPortalPos.class, SPacketRingPortalPos::encode, SPacketRingPortalPos::decode, SPacketRingPortalPos::handle);
		CHANNEL.registerMessage(id++, SPacketLOTRTimeUpdate.class, SPacketLOTRTimeUpdate::encode, SPacketLOTRTimeUpdate::decode, SPacketLOTRTimeUpdate::handle);
		CHANNEL.registerMessage(id++, SPacketIsOpResponse.class, SPacketIsOpResponse::encode, SPacketIsOpResponse::decode, SPacketIsOpResponse::handle);
		CHANNEL.registerMessage(id++, SPacketMapSettings.class, SPacketMapSettings::encode, SPacketMapSettings::decode, SPacketMapSettings::handle);
		CHANNEL.registerMessage(id++, SPacketMapPlayerLocations.class, SPacketMapPlayerLocations::encode, SPacketMapPlayerLocations::decode, SPacketMapPlayerLocations::handle);
		CHANNEL.registerMessage(id++, SPacketLoginPlayerDataModule.class, SPacketLoginPlayerDataModule::encode, SPacketLoginPlayerDataModule::decode, SPacketLoginPlayerDataModule::handle);
		CHANNEL.registerMessage(id++, SPacketWaypointRegion.class, SPacketWaypointRegion::encode, SPacketWaypointRegion::decode, SPacketWaypointRegion::handle);
		CHANNEL.registerMessage(id++, SPacketTimeSinceFT.class, SPacketTimeSinceFT::encode, SPacketTimeSinceFT::decode, SPacketTimeSinceFT::handle);
		CHANNEL.registerMessage(id++, SPacketWaypointUseCount.class, SPacketWaypointUseCount::encode, SPacketWaypointUseCount::decode, SPacketWaypointUseCount::handle);
		CHANNEL.registerMessage(id++, SPacketWorldWaypointCooldown.class, SPacketWorldWaypointCooldown::encode, SPacketWorldWaypointCooldown::decode, SPacketWorldWaypointCooldown::handle);
		CHANNEL.registerMessage(id++, SPacketDate.class, SPacketDate::encode, SPacketDate::decode, SPacketDate::handle);
		CHANNEL.registerMessage(id++, SPacketFactionSettings.class, SPacketFactionSettings::encode, SPacketFactionSettings::decode, SPacketFactionSettings::handle);
		CHANNEL.registerMessage(id++, SPacketViewingFaction.class, SPacketViewingFaction::encode, SPacketViewingFaction::decode, SPacketViewingFaction::handle);
		CHANNEL.registerMessage(id++, SPacketRegionLastViewedFaction.class, SPacketRegionLastViewedFaction::encode, SPacketRegionLastViewedFaction::decode, SPacketRegionLastViewedFaction::handle);
		CHANNEL.registerMessage(id++, SPacketAlignment.class, SPacketAlignment::encode, SPacketAlignment::decode, SPacketAlignment::handle);
		CHANNEL.registerMessage(id++, SPacketPledge.class, SPacketPledge::encode, SPacketPledge::decode, SPacketPledge::handle);
		CHANNEL.registerMessage(id++, SPacketPledgeBreak.class, SPacketPledgeBreak::encode, SPacketPledgeBreak::decode, SPacketPledgeBreak::handle);
		CHANNEL.registerMessage(id++, SPacketAlignmentDrain.class, SPacketAlignmentDrain::encode, SPacketAlignmentDrain::decode, SPacketAlignmentDrain::handle);
		CHANNEL.registerMessage(id++, SPacketPlayerMessage.class, SPacketPlayerMessage::encode, SPacketPlayerMessage::decode, SPacketPlayerMessage::handle);
		CHANNEL.registerMessage(id++, SPacketPreferredRankGender.class, SPacketPreferredRankGender::encode, SPacketPreferredRankGender::decode, SPacketPreferredRankGender::handle);
		CHANNEL.registerMessage(id++, SPacketOpenScreen.class, SPacketOpenScreen::encode, SPacketOpenScreen::decode, SPacketOpenScreen::handle);
		CHANNEL.registerMessage(id++, SPacketCreateCustomWaypoint.class, SPacketCreateCustomWaypoint::encode, SPacketCreateCustomWaypoint::decode, SPacketCreateCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketDeleteCustomWaypoint.class, SPacketDeleteCustomWaypoint::encode, SPacketDeleteCustomWaypoint::decode, SPacketDeleteCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketShowWaypoints.class, SPacketShowWaypoints::encode, SPacketShowWaypoints::decode, SPacketShowWaypoints::handle);
		CHANNEL.registerMessage(id++, SPacketUpdateCustomWaypoint.class, SPacketUpdateCustomWaypoint::encode, SPacketUpdateCustomWaypoint::decode, SPacketUpdateCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketOpenUpdateCustomWaypointScreen.class, SPacketOpenUpdateCustomWaypointScreen::encode, SPacketOpenUpdateCustomWaypointScreen::decode, SPacketOpenUpdateCustomWaypointScreen::handle);
		CHANNEL.registerMessage(id++, SPacketOpenAdoptCustomWaypointScreen.class, SPacketOpenAdoptCustomWaypointScreen::encode, SPacketOpenAdoptCustomWaypointScreen::decode, SPacketOpenAdoptCustomWaypointScreen::handle);
		CHANNEL.registerMessage(id++, SPacketOpenViewAdoptedCustomWaypointScreen.class, SPacketOpenViewAdoptedCustomWaypointScreen::encode, SPacketOpenViewAdoptedCustomWaypointScreen::decode, SPacketOpenViewAdoptedCustomWaypointScreen::handle);
		CHANNEL.registerMessage(id++, SPacketAdoptCustomWaypoint.class, SPacketAdoptCustomWaypoint::encode, SPacketAdoptCustomWaypoint::decode, SPacketAdoptCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketUpdateAdoptedCustomWaypoint.class, SPacketUpdateAdoptedCustomWaypoint::encode, SPacketUpdateAdoptedCustomWaypoint::decode, SPacketUpdateAdoptedCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketDeleteAdoptedCustomWaypoint.class, SPacketDeleteAdoptedCustomWaypoint::encode, SPacketDeleteAdoptedCustomWaypoint::decode, SPacketDeleteAdoptedCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, SPacketCustomWaypointAdoptedCount.class, SPacketCustomWaypointAdoptedCount::encode, SPacketCustomWaypointAdoptedCount::decode, SPacketCustomWaypointAdoptedCount::handle);
		CHANNEL.registerMessage(id++, SPacketToggle.class, SPacketToggle::encode, SPacketToggle::decode, SidedTogglePacket::handle);
		CHANNEL.registerMessage(id++, SPacketCreateMapMarker.class, SPacketCreateMapMarker::encode, SPacketCreateMapMarker::decode, SPacketCreateMapMarker::handle);
		CHANNEL.registerMessage(id++, SPacketDeleteMapMarker.class, SPacketDeleteMapMarker::encode, SPacketDeleteMapMarker::decode, SPacketDeleteMapMarker::handle);
		CHANNEL.registerMessage(id++, SPacketUpdateMapMarker.class, SPacketUpdateMapMarker::encode, SPacketUpdateMapMarker::decode, SPacketUpdateMapMarker::handle);
		CHANNEL.registerMessage(id++, SPacketFastTravel.class, SPacketFastTravel::encode, SPacketFastTravel::decode, SPacketFastTravel::handle);
		CHANNEL.registerMessage(id++, SPacketNPCPersonalInfo.class, SPacketNPCPersonalInfo::encode, SPacketNPCPersonalInfo::decode, SPacketNPCPersonalInfo::handle);
		CHANNEL.registerMessage(id++, SPacketNPCEntitySettings.class, SPacketNPCEntitySettings::encode, SPacketNPCEntitySettings::decode, SPacketNPCEntitySettings::handle);
		CHANNEL.registerMessage(id++, SPacketNPCTalkAnimations.class, SPacketNPCTalkAnimations::encode, SPacketNPCTalkAnimations::decode, SPacketNPCTalkAnimations::handle);
		CHANNEL.registerMessage(id++, SPacketNPCState.class, SPacketNPCState::encode, SPacketNPCState::decode, SPacketNPCState::handle);
		CHANNEL.registerMessage(id++, SPacketFactionStats.class, SPacketFactionStats::encode, SPacketFactionStats::decode, SPacketFactionStats::handle);
		CHANNEL.registerMessage(id++, SPacketAlignmentBonus.class, SPacketAlignmentBonus::encode, SPacketAlignmentBonus::decode, SPacketAlignmentBonus::handle);
		CHANNEL.registerMessage(id++, SPacketSetAttackTarget.class, SPacketSetAttackTarget::encode, SPacketSetAttackTarget::decode, SPacketSetAttackTarget::handle);
		CHANNEL.registerMessage(id++, SPacketMapExplorationFull.class, SPacketMapExplorationFull::encode, SPacketMapExplorationFull::decode, SPacketMapExplorationFull::handle);
		CHANNEL.registerMessage(id++, SPacketMapExplorationTile.class, SPacketMapExplorationTile::encode, SPacketMapExplorationTile::decode, SPacketMapExplorationTile::handle);
		CHANNEL.registerMessage(id++, SPacketSpeechbank.class, SPacketSpeechbank::encode, SPacketSpeechbank::decode, SPacketSpeechbank::handle);
		CHANNEL.registerMessage(id++, SPacketNotifyAlignRequirement.class, SPacketNotifyAlignRequirement::encode, SPacketNotifyAlignRequirement::decode, SPacketNotifyAlignRequirement::handle);
		CHANNEL.registerMessage(id++, CPacketKegBrewButton.class, CPacketKegBrewButton::encode, CPacketKegBrewButton::decode, CPacketKegBrewButton::handle);
		CHANNEL.registerMessage(id++, CPacketFactionCraftingToggle.class, CPacketFactionCraftingToggle::encode, CPacketFactionCraftingToggle::decode, CPacketFactionCraftingToggle::handle);
		CHANNEL.registerMessage(id++, CPacketIsOpRequest.class, CPacketIsOpRequest::encode, CPacketIsOpRequest::decode, CPacketIsOpRequest::handle);
		CHANNEL.registerMessage(id++, CPacketMapTp.class, CPacketMapTp::encode, CPacketMapTp::decode, CPacketMapTp::handle);
		CHANNEL.registerMessage(id++, CPacketFastTravel.class, CPacketFastTravel::encode, CPacketFastTravel::decode, CPacketFastTravel::handle);
		CHANNEL.registerMessage(id++, CPacketViewedFactions.class, CPacketViewedFactions::encode, CPacketViewedFactions::decode, CPacketViewedFactions::handle);
		CHANNEL.registerMessage(id++, CPacketChoosePreferredRankGender.class, CPacketChoosePreferredRankGender::encode, CPacketChoosePreferredRankGender::decode, CPacketChoosePreferredRankGender::handle);
		CHANNEL.registerMessage(id++, CPacketSetPledge.class, CPacketSetPledge::encode, CPacketSetPledge::decode, CPacketSetPledge::handle);
		CHANNEL.registerMessage(id++, CPacketCreateCustomWaypoint.class, CPacketCreateCustomWaypoint::encode, CPacketCreateCustomWaypoint::decode, CPacketCreateCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, CPacketToggleShowWaypoints.class, CPacketToggleShowWaypoints::encode, CPacketToggleShowWaypoints::decode, CPacketToggleShowWaypoints::handle);
		CHANNEL.registerMessage(id++, CPacketUpdateCustomWaypoint.class, CPacketUpdateCustomWaypoint::encode, CPacketUpdateCustomWaypoint::decode, CPacketUpdateCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, CPacketDestroyCustomWaypoint.class, CPacketDestroyCustomWaypoint::encode, CPacketDestroyCustomWaypoint::decode, CPacketDestroyCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, CPacketAdoptCustomWaypoint.class, CPacketAdoptCustomWaypoint::encode, CPacketAdoptCustomWaypoint::decode, CPacketAdoptCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, CPacketForsakeAdoptedCustomWaypoint.class, CPacketForsakeAdoptedCustomWaypoint::encode, CPacketForsakeAdoptedCustomWaypoint::decode, CPacketForsakeAdoptedCustomWaypoint::handle);
		CHANNEL.registerMessage(id++, CPacketToggle.class, CPacketToggle::encode, CPacketToggle::decode, SidedTogglePacket::handle);
		CHANNEL.registerMessage(id++, CPacketCreateMapMarker.class, CPacketCreateMapMarker::encode, CPacketCreateMapMarker::decode, CPacketCreateMapMarker::handle);
		CHANNEL.registerMessage(id++, CPacketDeleteMapMarker.class, CPacketDeleteMapMarker::encode, CPacketDeleteMapMarker::decode, CPacketDeleteMapMarker::handle);
		CHANNEL.registerMessage(id++, CPacketUpdateMapMarker.class, CPacketUpdateMapMarker::encode, CPacketUpdateMapMarker::decode, CPacketUpdateMapMarker::handle);
		CHANNEL.registerMessage(id++, CPacketRenamePouch.class, CPacketRenamePouch::encode, CPacketRenamePouch::decode, CPacketRenamePouch::handle);
		CHANNEL.registerMessage(id++, CPacketRestockPouches.class, CPacketRestockPouches::encode, CPacketRestockPouches::decode, CPacketRestockPouches::handle);
		if (id >= 255) {
			LOTRLog.error("DEVELOPMENT ERROR: Number of packet types has reached the maximum of %d! Need to combine some or register a new channel.", id);
		}

	}

	public static void sendTo(Object msg, ServerPlayerEntity player) {
		CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), msg);
	}

	public static void sendToAll(Object msg) {
		CHANNEL.send(PacketDistributor.ALL.noArg(), msg);
	}

	public static void sendToAllExcept(Object msg, ServerPlayerEntity player) {
		for (ServerPlayerEntity otherPlayer : player.server.getPlayerList().getPlayers()) {
			if (!player.getUUID().equals(otherPlayer.getUUID())) {
				sendTo(msg, otherPlayer);
			}
		}

	}

	public static void sendToAllTrackingEntity(Object msg, Entity entity) {
		CHANNEL.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
	}

	public static void sendToDimension(Object msg, RegistryKey worldDim) {
		CHANNEL.send(PacketDistributor.DIMENSION.with(() -> worldDim), msg);
	}

	public static void sendToDimensionWorld(Object msg, World world) {
		sendToDimension(msg, world.dimension());
	}

	public static void sendToNear(Object msg, TargetPoint target) {
		CHANNEL.send(PacketDistributor.NEAR.with(() -> target), msg);
	}

	public static void sendToServer(Object msg) {
		CHANNEL.send(PacketDistributor.SERVER.noArg(), msg);
	}
}
