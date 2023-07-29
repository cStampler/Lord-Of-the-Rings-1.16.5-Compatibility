package lotr.client.event;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent.CameraSetup;

public class AdvancedDrunkEffect {
	private int yawDirection = 1;
	private int rollDirection = 1;
	private float yawAdd;
	private float prevYawAdd;
	private float pitchAdd;
	private float prevPitchAdd;
	private float rollAdd;
	private float prevRollAdd;
	private float drunkFactor;
	private float prevDrunkFactor;

	public void handle(CameraSetup event) {
		float yaw = event.getYaw();
		float pitch = event.getPitch();
		float roll = event.getRoll();
		float tick = (float) event.getRenderPartialTicks();
		float factor = prevDrunkFactor + (drunkFactor - prevDrunkFactor) * tick;
		yaw += (prevYawAdd + (yawAdd - prevYawAdd) * tick) * factor;
		pitch += (prevPitchAdd + (pitchAdd - prevPitchAdd) * tick) * factor;
		roll += (prevRollAdd + (rollAdd - prevRollAdd) * tick) * factor;
		event.setYaw(yaw);
		event.setPitch(pitch);
		event.setRoll(roll);
	}

	public void reset() {
		yawDirection = rollDirection = 1;
		prevYawAdd = yawAdd = 0.0F;
		prevPitchAdd = pitchAdd = 0.0F;
		prevRollAdd = rollAdd = 0.0F;
		prevDrunkFactor = drunkFactor = 0.0F;
	}

	public void update(LivingEntity viewer) {
		prevYawAdd = yawAdd;
		prevPitchAdd = pitchAdd;
		prevRollAdd = rollAdd;
		prevDrunkFactor = drunkFactor;
		if (viewer.hasEffect(Effects.CONFUSION)) {
			float drunk = viewer.getEffect(Effects.CONFUSION).getDuration();
			drunk /= 20.0F;
			drunk /= 120.0F;
			drunkFactor = Math.min(drunk, 1.0F);
			yawAdd += rollDirection * 1.2F;
			yawAdd = MathHelper.clamp(yawAdd, -30.0F, 30.0F);
			rollAdd += rollDirection * 0.6F;
			rollAdd = MathHelper.clamp(rollAdd, -30.0F, 30.0F);
			if (viewer.getRandom().nextInt(200) == 0) {
				yawDirection *= -1;
			}

			if (viewer.getRandom().nextInt(100) == 0) {
				rollDirection *= -1;
			}
		} else {
			yawAdd = 0.0F;
			pitchAdd = 0.0F;
			rollAdd = 0.0F;
			drunkFactor = 0.0F;
		}

	}
}
