package lotr.common.block;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class SnowPathBlock extends LOTRPathBlock {
	public SnowPathBlock() {
		super(Properties.of(Material.SNOW).strength(0.2F).sound(SoundType.SNOW).requiresCorrectToolForDrops().harvestTool(ToolType.SHOVEL));
	}

	@Override
	protected BlockState getUnpathedBlockState() {
		return Blocks.SNOW_BLOCK.defaultBlockState();
	}

	public static ActionResultType makeSnowPathUnderSnowLayer(World world, BlockPos pos, Direction side, PlayerEntity player, Hand hand, ItemStack heldItem) {
		BlockState stateBefore = world.getBlockState(pos);
		BlockPos belowPos = pos.below();
		if (stateBefore.getBlock() != Blocks.SNOW || stateBefore.getValue(SnowBlock.LAYERS) != 1 || world.getBlockState(belowPos).getBlock() != Blocks.SNOW_BLOCK) {
			return ActionResultType.PASS;
		}
		world.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
		BlockRayTraceResult fakedBelowHitVec = new BlockRayTraceResult(Vector3d.atCenterOf(belowPos), side, belowPos, false);
		ItemUseContext context = new ItemUseContext(world, player, hand, heldItem, fakedBelowHitVec);
		ActionResultType shovelResult = heldItem.useOn(context);
		if (!shovelResult.consumesAction()) {
			world.setBlockAndUpdate(pos, stateBefore);
		}

		return shovelResult;
	}
}
