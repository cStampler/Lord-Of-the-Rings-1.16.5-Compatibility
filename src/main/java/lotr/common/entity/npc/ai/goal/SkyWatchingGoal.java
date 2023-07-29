package lotr.common.entity.npc.ai.goal;

import java.util.*;

import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceContext.*;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public abstract class SkyWatchingGoal extends Goal {
	protected final MobEntity entity;
	protected final World world;
	protected final Random rand;
	private final float chance;
	private int watchingTick;
	private int reCheckLookTick;

	public SkyWatchingGoal(MobEntity entity, float chance) {
		this.entity = entity;
		world = entity.level;
		rand = entity.getRandom();
		this.chance = chance;
		setFlags(EnumSet.of(Flag.LOOK, Flag.MOVE));
	}

	@Override
	public boolean canContinueToUse() {
		if (watchingTick <= 0) {
			return false;
		}
		if (reCheckLookTick <= 0) {
			reCheckLookTick = 20;
			return recheckShouldContinueWatching();
		}
		return true;
	}

	protected boolean canSeeSkyWatchTarget(Vector3d target) {
		if (!world.isRaining()) {
			Vector3d eyePos = entity.getEyePosition(1.0F);
			Type type = world.clip(new RayTraceContext(eyePos, target, BlockMode.VISUAL, FluidMode.NONE, entity)).getType();
			return type == Type.MISS;
		}
		return false;
	}

	@Override
	public boolean canUse() {
		return rand.nextFloat() < chance ? shouldStartWatching() : false;
	}

	protected abstract Vector3d getCurrentWatchTarget();

	protected abstract int getWatchingDuration();

	protected abstract boolean recheckShouldContinueWatching();

	protected abstract boolean shouldStartWatching();

	@Override
	public void start() {
		watchingTick = getWatchingDuration();
		reCheckLookTick = 20;
	}

	@Override
	public void stop() {
		watchingTick = 0;
		reCheckLookTick = 0;
	}

	@Override
	public void tick() {
		entity.getLookControl().setLookAt(getCurrentWatchTarget());
		--watchingTick;
		--reCheckLookTick;
	}
}
