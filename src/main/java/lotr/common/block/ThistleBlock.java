package lotr.common.block;

import lotr.common.init.LOTRDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.potion.Effect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.World;

public class ThistleBlock extends LOTRFlowerBlock {
	private static final VoxelShape THISTLE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public ThistleBlock(Effect effect, int effectDuration) {
		super(effect, effectDuration);
		flowerShape = THISTLE_SHAPE;
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof LivingEntity && entity.isSprinting()) {
			LivingEntity living = (LivingEntity) entity;
			boolean bootsLegs = living.hasItemInSlot(EquipmentSlotType.FEET) && living.hasItemInSlot(EquipmentSlotType.LEGS);
			if (!bootsLegs) {
				living.hurt(LOTRDamageSources.PLANT, 0.25F);
			}
		}

	}
}
