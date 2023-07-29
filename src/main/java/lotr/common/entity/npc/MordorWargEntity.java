package lotr.common.entity.npc;

import lotr.common.init.LOTREntities;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class MordorWargEntity extends WargEntity {
	public MordorWargEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected WargType chooseWargType() {
		return random.nextInt(5) == 0 ? WargType.BROWN : WargType.BLACK;
	}

	@Override
	protected NPCEntity createWargRider() {
		return random.nextBoolean() ? (NPCEntity) ((EntityType) LOTREntities.MORDOR_ORC_ARCHER.get()).create(level) : (NPCEntity) ((EntityType) LOTREntities.MORDOR_ORC.get()).create(level);
	}
}
