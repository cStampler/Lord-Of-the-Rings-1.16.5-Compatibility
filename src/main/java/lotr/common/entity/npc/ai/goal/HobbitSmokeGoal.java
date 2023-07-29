package lotr.common.entity.npc.ai.goal;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.init.LOTRItems;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;

public class HobbitSmokeGoal extends NPCConsumeGoal {
	public HobbitSmokeGoal(NPCEntity entity, int chance) {
		super(entity, chance);
	}

	@Override
	protected void consume() {
		ItemStack itemstack = getHeldConsumingItem();
		itemstack.finishUsingItem(theEntity.level, theEntity);
		theEntity.heal(2.0F);
	}

	@Override
	protected ItemStack createConsumable() {
		return new ItemStack((IItemProvider) LOTRItems.SMOKING_PIPE.get());
	}

	@Override
	protected void updateConsumeTick(int tick) {
	}
}
