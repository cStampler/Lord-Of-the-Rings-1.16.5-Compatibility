package lotr.common.block;

import lotr.common.init.LOTRDamageSources;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NettleBlock extends LOTRGrassBlock {
	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity instanceof PlayerEntity) {
			PlayerEntity player = (PlayerEntity) entity;
			boolean bootsLegs = player.hasItemInSlot(EquipmentSlotType.FEET) && player.hasItemInSlot(EquipmentSlotType.LEGS);
			if (!bootsLegs) {
				player.hurt(LOTRDamageSources.PLANT, 0.25F);
			}
		}

	}
}
