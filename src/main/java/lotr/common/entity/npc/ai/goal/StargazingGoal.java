package lotr.common.entity.npc.ai.goal;

import lotr.common.util.LOTRUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class StargazingGoal extends SkyWatchingGoal {
	private Vector3d currentStargazeTarget;
	private int gazeHereTick;

	public StargazingGoal(MobEntity entity, float chance) {
		super(entity, chance);
	}

	private boolean canSeeStargazeTarget(Vector3d target) {
		world.getProfiler().push("canSeeStargazeTarget");
		boolean canSee = canSeeSkyWatchTarget(target);
		world.getProfiler().pop();
		return canSee;
	}

	private int getCurrentGazeDuration() {
		return LOTRUtil.secondsToTicks(4 + entity.getRandom().nextInt(12));
	}

	@Override
	protected Vector3d getCurrentWatchTarget() {
		return currentStargazeTarget;
	}

	private Vector3d getRandomStargazeTarget() {
		world.getProfiler().push("getRandomStargazeTarget");
		Vector3d eyePos = entity.getEyePosition(1.0F);
		Vector3d skyVector = new Vector3d(0.0D, 300.0D, 0.0D);
		float randPitch = (float) Math.toRadians(MathHelper.nextFloat(rand, -40.0F, 40.0F));
		float randYaw = rand.nextFloat() * 3.1415927F * 2.0F;
		skyVector = skyVector.xRot(randPitch).yRot(randYaw);
		Vector3d skyPos = eyePos.add(skyVector);
		world.getProfiler().pop();
		return skyPos;
	}

	@Override
	protected int getWatchingDuration() {
		return LOTRUtil.secondsToTicks(10 + entity.getRandom().nextInt(50));
	}

	private boolean isNightTime() {
		float sunCycle = world.getTimeOfDay(1.0F);
		return sunCycle > 0.26F && sunCycle < 0.74F;
	}

	@Override
	protected boolean recheckShouldContinueWatching() {
		return isNightTime() && canSeeStargazeTarget(currentStargazeTarget);
	}

	@Override
	protected boolean shouldStartWatching() {
		if (isNightTime()) {
			Vector3d target = getRandomStargazeTarget();
			if (canSeeStargazeTarget(target)) {
				currentStargazeTarget = target;
				gazeHereTick = getCurrentGazeDuration();
				return true;
			}
		}

		return false;
	}

	@Override
	public void stop() {
		super.stop();
		currentStargazeTarget = null;
		gazeHereTick = 0;
	}

	@Override
	public void tick() {
		super.tick();
		--gazeHereTick;
		if (gazeHereTick <= 0) {
			currentStargazeTarget = getRandomStargazeTarget();
			gazeHereTick = getCurrentGazeDuration();
		}

	}
}
