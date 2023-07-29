package lotr.common.entity.capabilities;

import lotr.common.LOTRLog;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;

public class PlateFallingData {
	private Entity theEntity;
	private int updateTick;
	private float[] posXTicksAgo = new float[65];
	private boolean[] isFalling = new boolean[65];
	private float[] fallerPos = new float[65];
	private float[] prevFallerPos = new float[65];
	private float[] fallerSpeed = new float[65];
	private boolean loggedNullEntityWarning = false;

	public float getFoodOffsetY(int foodSlot, float partialTick) {
		return getOffsetY(foodSlot - 1, partialTick);
	}

	private float getOffsetY(int index, float partialTick) {
		if (theEntity != null) {
			index = MathHelper.clamp(index, 0, fallerPos.length - 1);
			float pos = prevFallerPos[index] + (fallerPos[index] - prevFallerPos[index]) * partialTick;
			float offset = pos - (float) (theEntity.yo + (theEntity.getY() - theEntity.yo) * partialTick);
			return Math.max(offset, 0.0F);
		}
		if (!loggedNullEntityWarning) {
			LOTRLog.warn("A PlateFallingData was asked for y-offset, but its entity was not set! This should not happen - it may be a compatibility problem. Stack trace:");
			Thread.dumpStack();
			loggedNullEntityWarning = true;
		}

		return 0.0F;
	}

	public float getPlateOffsetY(float partialTick) {
		return getOffsetY(0, partialTick);
	}

	public boolean isEntitySet() {
		return theEntity != null;
	}

	public PlateFallingData setEntity(Entity e) {
		if (isEntitySet()) {
			throw new IllegalStateException("Entity is already set");
		}
		theEntity = e;
		return this;
	}

	public void update() {
		float curPos = (float) theEntity.getY();
		int l;
		if (!theEntity.isOnGround() && theEntity.getDeltaMovement().y() > 0.0D) {
			for (l = 0; l < posXTicksAgo.length; ++l) {
				posXTicksAgo[l] = Math.max(posXTicksAgo[l], curPos);
			}
		}

		if (updateTick % 1 == 0) {
			for (l = posXTicksAgo.length - 1; l > 0; --l) {
				posXTicksAgo[l] = posXTicksAgo[l - 1];
			}

			posXTicksAgo[0] = curPos;
		}

		++updateTick;

		for (l = 0; l < fallerPos.length; ++l) {
			prevFallerPos[l] = fallerPos[l];
			float pos = fallerPos[l];
			float speed = fallerSpeed[l];
			boolean fall = isFalling[l];
			if (!fall && pos > posXTicksAgo[l]) {
				fall = true;
			}

			isFalling[l] = fall;
			if (fall) {
				speed = (float) (speed + 0.08D);
				pos -= speed;
				speed = (float) (speed * 0.98D);
			} else {
				speed = 0.0F;
			}

			if (pos < curPos) {
				pos = curPos;
				speed = 0.0F;
				isFalling[l] = false;
			}

			fallerPos[l] = pos;
			fallerSpeed[l] = speed;
		}

	}
}
