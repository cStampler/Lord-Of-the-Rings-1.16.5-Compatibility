package lotr.common.world.map;

import javax.annotation.Nullable;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

public interface Waypoint extends SelectableMapObject {
	default ITextComponent getCoordsText() {
		return new TranslationTextComponent("gui.lotr.map.coords", getWorldX(), getWorldZ());
	}

	@Nullable
	ITextComponent getDisplayLore();

	ITextComponent getDisplayName();

	@Nullable
	ITextComponent getDisplayOwnership();

	Waypoint.WaypointDisplayState getDisplayState(@Nullable PlayerEntity var1);

	default double getDistanceFromPlayer(PlayerEntity player) {
		Vector3d playerPos = player.position();
		double x = getWorldX() + 0.5D;
		double z = getWorldZ() + 0.5D;
		Vector3d pos = new Vector3d(x, playerPos.y, z);
		return pos.distanceTo(playerPos);
	}

	@Override
	default int getMapIconWidth() {
		return 6;
	}

	double getMapX();

	double getMapZ();

	WaypointNetworkType getNetworkType();

	ITextComponent getNotUnlockedMessage(PlayerEntity var1);

	String getRawName();

	@Nullable
	BlockPos getTravelPosition(ServerWorld var1, PlayerEntity var2);

	@Override
	int getWorldX();

	@Override
	int getWorldZ();

	boolean hasPlayerUnlocked(PlayerEntity var1);

	boolean isCustom();

	boolean isSharedCustom();

	boolean isSharedHidden();

	default boolean verifyFastTravellable(ServerWorld world, PlayerEntity player) {
		return true;
	}

	public enum WaypointDisplayState {
		HIDDEN(0, 0), STANDARD(0, 200), STANDARD_LOCKED(6, 200), STANDARD_LOCKED_TO_ENEMIES(12, 200), STANDARD_CONQUERED(18, 200), CUSTOM(24, 200), CUSTOM_LOCKED(30, 200), SHARED_CUSTOM(36, 200), SHARED_CUSTOM_LOCKED(42, 200);

		public final int iconU;
		public final int iconV;
		public final int highlightIconU;
		public final int highlightIconV;

		WaypointDisplayState(int u, int v) {
			iconU = u;
			iconV = v;
			highlightIconU = iconU;
			highlightIconV = iconV + 6;
		}

		public boolean isHidden() {
			return this == HIDDEN;
		}
	}
}
