package lotr.common.entity.projectile;

import net.minecraft.entity.*;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.network.IPacket;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class LOTRAbstractArrowEntity extends AbstractArrowEntity {
	protected LOTRAbstractArrowEntity(EntityType type, double x, double y, double z, World w) {
		super(type, x, y, z, w);
	}

	protected LOTRAbstractArrowEntity(EntityType type, LivingEntity thrower, World w) {
		super(type, thrower, w);
	}

	protected LOTRAbstractArrowEntity(EntityType type, World w) {
		super(type, w);
	}

	protected int calculateImpactDamageIncludingCritical(float baseDamage) {
		int dmgInt = MathHelper.ceil(MathHelper.clamp(baseDamage, 0.0F, 2.14748365E9F));
		if (isCritArrow()) {
			long extraDmg = random.nextInt(dmgInt / 2 + 2);
			dmgInt = (int) Math.min(extraDmg + dmgInt, 2147483647L);
		}

		return dmgInt;
	}

	@Override
	public IPacket getAddEntityPacket() {
		return NetworkHooks.getEntitySpawningPacket(this);
	}

	protected int getLifespanTicksInGround() {
		return 1200;
	}
}
