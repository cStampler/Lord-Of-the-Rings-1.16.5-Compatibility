package lotr.common.dispenser;

import lotr.common.entity.projectile.SpearEntity;
import net.minecraft.dispenser.IPosition;
import net.minecraft.dispenser.ProjectileDispenseBehavior;
import net.minecraft.entity.projectile.AbstractArrowEntity.PickupStatus;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispenseSpear extends ProjectileDispenseBehavior {
	@Override
	protected ProjectileEntity getProjectile(World world, IPosition pos, ItemStack stack) {
		SpearEntity spear = new SpearEntity(world, pos.x(), pos.y(), pos.z(), stack);
		spear.pickup = PickupStatus.ALLOWED;
		return spear;
	}
}
