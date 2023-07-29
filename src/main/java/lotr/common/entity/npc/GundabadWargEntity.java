package lotr.common.entity.npc;

import lotr.common.init.LOTREntities;
import net.minecraft.entity.EntityType;
import net.minecraft.world.World;

public class GundabadWargEntity extends WargEntity {
	public GundabadWargEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected WargType chooseWargType() {
		if (random.nextInt(500) == 0) {
			return WargType.WHITE;
		}
		if (random.nextInt(24) == 0) {
			return WargType.BLACK;
		}
		if (random.nextInt(10) == 0) {
			return WargType.SILVER;
		}
		return random.nextInt(5) == 0 ? WargType.BROWN : WargType.GREY;
	}

	@Override
	protected NPCEntity createWargRider() {
		return random.nextBoolean() ? (NPCEntity) ((EntityType) LOTREntities.GUNDABAD_ORC_ARCHER.get()).create(level) : (NPCEntity) ((EntityType) LOTREntities.GUNDABAD_ORC.get()).create(level);
	}
}
