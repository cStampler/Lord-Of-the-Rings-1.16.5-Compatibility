package lotr.common.entity.npc.ai.goal;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.item.VesselDrinkItem;
import net.minecraft.item.*;

public class NPCDrinkGoal extends NPCConsumeGoal {
	private final NPCFoodPool drinkPool;

	public NPCDrinkGoal(NPCEntity entity, NPCFoodPool foods, int chance) {
		super(entity, chance);
		drinkPool = foods;
	}

	@Override
	protected void consume() {
		ItemStack itemstack = theEntity.getMainHandItem();
		playDrinkSound(itemstack);
		Item item = itemstack.getItem();
		if (item instanceof VesselDrinkItem) {
			VesselDrinkItem drink = (VesselDrinkItem) item;
			drink.finishUsingItem(itemstack, theEntity.level, theEntity);
		}

	}

	@Override
	protected ItemStack createConsumable() {
		ItemStack drink = drinkPool.getRandomFood(rand);
		Item item = drink.getItem();
		if (item instanceof VesselDrinkItem && ((VesselDrinkItem) item).hasPotencies) {
			VesselDrinkItem.setPotency(drink, VesselDrinkItem.Potency.randomForNPC(rand));
		}

		return drink;
	}

	@Override
	protected int getConsumeTime() {
		int time = super.getConsumeTime();
		if (theEntity.isDrunk()) {
			time *= 1 + rand.nextInt(4);
		}

		return time;
	}

	private void playDrinkSound(ItemStack itemstack) {
		theEntity.playSound(theEntity.getNPCDrinkSound(itemstack), 0.5F, rand.nextFloat() * 0.1F + 0.9F);
	}

	@Override
	protected boolean shouldConsume() {
		return theEntity.isDrunk() && rand.nextInt(100) == 0 ? true : super.shouldConsume();
	}

	@Override
	protected void updateConsumeTick(int tick) {
		if (tick % 4 == 0) {
			playDrinkSound(theEntity.getMainHandItem());
		}

	}
}
