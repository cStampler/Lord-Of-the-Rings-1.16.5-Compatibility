package lotr.common.world.map;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.*;

public abstract class VerticallyPositionedWaypoint implements Waypoint {
	@Override
	public ITextComponent getCoordsText() {
		return new TranslationTextComponent("gui.lotr.map.coordsY", getWorldX(), getWorldY(), getWorldZ());
	}

	@Override
	public double getDistanceFromPlayer(PlayerEntity player) {
		Vector3d playerPos = player.position();
		double x = getWorldX() + 0.5D;
		double z = getWorldZ() + 0.5D;
		double y = getWorldY();
		Vector3d pos = new Vector3d(x, y, z);
		return pos.distanceTo(playerPos);
	}

	public abstract int getWorldY();
}
