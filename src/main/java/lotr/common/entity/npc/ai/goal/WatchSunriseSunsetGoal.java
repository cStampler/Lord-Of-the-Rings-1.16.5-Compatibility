package lotr.common.entity.npc.ai.goal;

import lotr.common.util.LOTRUtil;
import net.minecraft.entity.MobEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;

public class WatchSunriseSunsetGoal extends SkyWatchingGoal {
	private static final long MIN_WATCH_INTERVAL = LOTRUtil.secondsToTicks(20);
	private WatchSunriseSunsetGoal.Watching watching;
	private long lastWatchTime;

	public WatchSunriseSunsetGoal(MobEntity entity, float chance) {
		super(entity, chance);
	}

	private boolean canSeeSunProjectedPos() {
		world.getProfiler().push("canSeeSunProjectedPos");
		boolean canSee = canSeeSkyWatchTarget(getSunProjectedPos());
		world.getProfiler().pop();
		return canSee;
	}

	private boolean canSeeSunrise() {
		return isSunrise() && canSeeSunProjectedPos();
	}

	private boolean canSeeSunset() {
		return isSunset() && canSeeSunProjectedPos();
	}

	private long getCurrentGameTime() {
		return world.getGameTime();
	}

	@Override
	protected Vector3d getCurrentWatchTarget() {
		return getSunProjectedPos();
	}

	private Vector3d getSunProjectedPos() {
		world.getProfiler().push("getSunProjectedPos");
		Vector3d eyePos = entity.getEyePosition(1.0F);
		Vector3d sunNoonVector = new Vector3d(0.0D, 100.0D, 0.0D);
		float sunAngleFromNoon = world.getSunAngle(1.0F);
		sunAngleFromNoon *= -1.0F;
		float cos = MathHelper.cos(sunAngleFromNoon);
		float sin = MathHelper.sin(sunAngleFromNoon);
		double x = sunNoonVector.x * cos + sunNoonVector.y * sin;
		double y = sunNoonVector.y * cos - sunNoonVector.x * sin;
		double z = sunNoonVector.z;
		Vector3d sunVector = new Vector3d(x, y, z);
		Vector3d sunPos = eyePos.add(sunVector);
		world.getProfiler().pop();
		return sunPos;
	}

	@Override
	protected int getWatchingDuration() {
		return LOTRUtil.secondsToTicks(5 + rand.nextInt(15));
	}

	private boolean isSunrise() {
		float sunCycle = world.getTimeOfDay(1.0F);
		return sunCycle > 0.729F && sunCycle < 0.76F;
	}

	private boolean isSunset() {
		float sunCycle = world.getTimeOfDay(1.0F);
		return sunCycle > 0.24F && sunCycle < 0.271F;
	}

	@Override
	protected boolean recheckShouldContinueWatching() {
		if (watching == WatchSunriseSunsetGoal.Watching.SUNRISE) {
			return canSeeSunrise();
		}
		return watching == WatchSunriseSunsetGoal.Watching.SUNSET ? canSeeSunset() : false;
	}

	@Override
	protected boolean shouldStartWatching() {
		if (getCurrentGameTime() - lastWatchTime < MIN_WATCH_INTERVAL) {
			return false;
		}
		if (canSeeSunset()) {
			watching = WatchSunriseSunsetGoal.Watching.SUNSET;
			return true;
		}
		if (canSeeSunrise()) {
			watching = WatchSunriseSunsetGoal.Watching.SUNRISE;
			return true;
		}
		return false;
	}

	@Override
	public void stop() {
		super.stop();
		watching = null;
	}

	@Override
	public void tick() {
		super.tick();
		lastWatchTime = getCurrentGameTime();
	}

	private enum Watching {
		SUNRISE, SUNSET;
	}
}
