package lotr.common.dispenser;

import lotr.common.entity.projectile.ThrownPlateEntity;
import net.minecraft.dispenser.*;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class DispensePlate extends ProjectileDispenseBehavior {
	@Override
	protected ProjectileEntity getProjectile(World world, IPosition pos, ItemStack stack) {
		return new ThrownPlateEntity(world, stack, pos.x(), pos.y(), pos.z());
	}
}
