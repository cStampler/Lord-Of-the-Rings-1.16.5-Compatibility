package lotr.common.entity.npc.ai.goal;

import java.util.*;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;

public abstract class NPCConsumeGoal extends Goal {
	protected NPCEntity theEntity;
	protected Random rand;
	private int chanceToConsume;
	private int consumeTick;

	public NPCConsumeGoal(NPCEntity entity, int chance) {
		theEntity = entity;
		rand = theEntity.getRandom();
		chanceToConsume = chance;
		setFlags(EnumSet.of(Flag.MOVE, Flag.LOOK));
	}

	@Override
	public boolean canContinueToUse() {
		return consumeTick > 0 && !theEntity.getMainHandItem().isEmpty() && theEntity.getTarget() == null;
	}

	@Override
	public boolean canUse() {
		if (theEntity.isBaby() || theEntity.getTarget() != null) {
			return false;
		}
		return theEntity.getNPCItemsInv().getIsEating() ? false : shouldConsume();
	}

	protected abstract void consume();

	protected abstract ItemStack createConsumable();

	protected int getConsumeTime() {
		return 32;
	}

	protected final ItemStack getHeldConsumingItem() {
		return theEntity.getMainHandItem();
	}

	protected boolean shouldConsume() {
		boolean needsHeal = theEntity.getHealth() < theEntity.getMaxHealth();
		return needsHeal && rand.nextInt(chanceToConsume / 4) == 0 || rand.nextInt(chanceToConsume) == 0;
	}

	@Override
	public void start() {
		theEntity.getNPCItemsInv().backupHeldAndStartEating(createConsumable());
		consumeTick = getConsumeTime();
	}

	@Override
	public void stop() {
		theEntity.getNPCItemsInv().stopEatingAndRestoreHeld();
		theEntity.refreshCurrentAttackMode();
		consumeTick = 0;
	}

	@Override
	public void tick() {
		--consumeTick;
		updateConsumeTick(consumeTick);
		if (consumeTick == 0) {
			consume();
		}

	}

	protected abstract void updateConsumeTick(int var1);
}
