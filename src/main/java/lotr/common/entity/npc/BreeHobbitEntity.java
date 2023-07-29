package lotr.common.entity.npc;

import lotr.common.entity.npc.data.*;
import lotr.common.entity.npc.data.name.*;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class BreeHobbitEntity extends HobbitEntity {
	public BreeHobbitEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.BREE_DRINK;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.BREE;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.BREE_HOBBIT;
	}
}
