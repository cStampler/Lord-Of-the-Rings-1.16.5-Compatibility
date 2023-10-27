package lotr.common;

import java.io.File;
import java.util.List;
import java.util.Optional;

import lotr.common.data.PlayerMessageType;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.network.SPacketAlignmentBonus;
import lotr.common.network.SPacketNotifyAlignRequirement;
import lotr.common.network.SPacketOpenScreen;
import lotr.common.network.SPacketSetAttackTarget;
import lotr.common.network.SPacketSpeechbank;
import lotr.common.world.map.AdoptedCustomWaypoint;
import lotr.common.world.map.CustomWaypoint;
import lotr.common.world.map.MapPlayerLocation;
import lotr.common.world.map.Waypoint;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public interface LOTRProxy {
	default void displayAdoptCustomWaypointScreen(CustomWaypoint waypoint, String createdPlayerName) {
	}

	default void displayAlignmentDrain(int numFactions) {
	}

	default void displayFastTravelScreen(Waypoint waypoint, int startX, int startZ) {
	}

	default void displayMessageType(PlayerMessageType messageType, boolean isCommandSent, String customText) {
	}

	default void displayNewDate() {
	}

	default void displayPacketOpenScreen(SPacketOpenScreen.Type type) {
	}

	default void displayUpdateCustomWaypointScreen(CustomWaypoint waypoint) {
	}

	default void displayViewAdoptedCustomWaypointScreen(AdoptedCustomWaypoint waypoint, String createdPlayerName) {
	}

	PlayerEntity getClientPlayer();

	World getClientWorld();

	default float getCurrentSandstormFogStrength() {
		return 0.0F;
	}

	File getGameRootDirectory();

	Optional<LivingEntity> getSidedAttackTarget(MobEntity var1);

	boolean isClient();

	boolean isSingleplayer();

	default void mapHandleIsOp(boolean isOp) {
	}

	default void mapHandlePlayerLocations(List<MapPlayerLocation> playerLocations) {
	}

	void receiveClientAttackTarget(SPacketSetAttackTarget var1);

	default void receiveNotifyAlignRequirementPacket(SPacketNotifyAlignRequirement packet) {
	}

	default void receiveSpeechbankPacket(SPacketSpeechbank packet) {
	}

	void setInRingPortal(Entity var1, RingPortalEntity var2);

	default void spawnAlignmentBonus(SPacketAlignmentBonus packet) {
	}
}
