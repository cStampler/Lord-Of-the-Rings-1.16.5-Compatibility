package lotr.common;

import java.io.File;
import java.util.*;

import lotr.common.data.PlayerMessageType;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.network.*;
import lotr.common.world.map.*;
import net.minecraft.entity.*;
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

	Optional getSidedAttackTarget(MobEntity var1);

	boolean isClient();

	boolean isSingleplayer();

	default void mapHandleIsOp(boolean isOp) {
	}

	default void mapHandlePlayerLocations(List playerLocations) {
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
