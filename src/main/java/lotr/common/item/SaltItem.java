package lotr.common.item;

import net.minecraft.block.*;
import net.minecraft.item.*;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class SaltItem extends Item {
	public SaltItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		BlockPos usePos = context.getClickedPos();
		ItemStack itemstack = context.getItemInHand();
		if (!world.isClientSide) {
			boolean usedAny = false;
			int range = 1 + world.random.nextInt(2);
			int yRange = range / 2;

			for (int i = -range; i <= range; ++i) {
				for (int j = -yRange; j <= yRange; ++j) {
					for (int k = -range; k <= range; ++k) {
						BlockPos nearPos = usePos.offset(i, j, k);
						if (nearPos.closerThan(usePos, range)) {
						}

						Block block = world.getBlockState(nearPos).getBlock();
						Block newBlock = null;
						if (block != Blocks.GRASS_BLOCK && block != Blocks.DIRT && block != Blocks.FARMLAND) {
							if (block == Blocks.SNOW) {
								newBlock = Blocks.AIR;
							}
						} else {
							newBlock = Blocks.COARSE_DIRT;
						}

						if (newBlock != null) {
							if (nearPos.equals(usePos) || world.random.nextInt(3) != 0) {
								world.setBlockAndUpdate(nearPos, newBlock.defaultBlockState());
								if (nearPos.equals(usePos) || world.random.nextInt(3) == 0) {
									((ServerWorld) world).sendParticles(ParticleTypes.CLOUD, nearPos.getX() + 0.5D, nearPos.getY() + 1.0D, nearPos.getZ() + 0.5D, 1, 0.0D, 0.0D, 0.0D, 0.0D);
								}
							}

							usedAny = true;
						}
					}
				}
			}

			if (usedAny) {
				itemstack.shrink(1);
				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}
}
