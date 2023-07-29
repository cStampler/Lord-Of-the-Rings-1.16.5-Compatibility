package lotr.common.entity.npc;

import lotr.common.init.LOTREntities;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class IsengardWargEntity extends WargEntity {
	public IsengardWargEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected WargType chooseWargType() {
		if (random.nextInt(5) == 0) {
			return WargType.BLACK;
		}
		return random.nextInt(3) == 0 ? WargType.GREY : WargType.BROWN;
	}

	@Override
	protected NPCEntity createWargRider() {
		return random.nextBoolean() ? (NPCEntity) ((EntityType) LOTREntities.ISENGARD_SNAGA_ARCHER.get()).create(level) : (NPCEntity) ((EntityType) LOTREntities.ISENGARD_SNAGA.get()).create(level);
	}
}
