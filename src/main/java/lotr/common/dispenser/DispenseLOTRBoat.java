package lotr.common.dispenser;

import lotr.common.entity.item.LOTRBoatEntity;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DispenseLOTRBoat extends DefaultDispenseItemBehavior {
	private final DefaultDispenseItemBehavior dispenseItemBehaviour = new DefaultDispenseItemBehavior();
	private final LOTRBoatEntity.ModBoatType type;

	public DispenseLOTRBoat(LOTRBoatEntity.ModBoatType t) {
		type = t;
	}

	@Override
	public ItemStack execute(IBlockSource source, ItemStack stack) {
		Direction dir = source.getBlockState().getValue(DispenserBlock.FACING);
		World world = source.getLevel();
		double x = source.x() + dir.getStepX() * 1.125F;
		double y = source.y() + dir.getStepY() * 1.125F;
		double z = source.z() + dir.getStepZ() * 1.125F;
		BlockPos pos = source.getPos().relative(dir);
		double yOffset;
		if (world.getFluidState(pos).is(FluidTags.WATER)) {
			yOffset = 1.0D;
		} else {
			if (!world.getBlockState(pos).isAir() || !world.getFluidState(pos.below()).is(FluidTags.WATER)) {
				return dispenseItemBehaviour.dispense(source, stack);
			}

			yOffset = 0.0D;
		}

		LOTRBoatEntity boat = new LOTRBoatEntity(world, x, y + yOffset, z);
		boat.setModBoatType(type);
		boat.yRot = dir.toYRot();
		world.addFreshEntity(boat);
		stack.shrink(1);
		return stack;
	}

	@Override
	protected void playSound(IBlockSource source) {
		source.getLevel().levelEvent(1000, source.getPos(), 0);
	}
}
