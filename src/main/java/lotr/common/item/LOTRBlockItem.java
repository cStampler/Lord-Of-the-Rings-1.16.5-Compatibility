package lotr.common.item;

import javax.annotation.Nullable;

import lotr.common.block.*;
import net.minecraft.block.Block;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipeType;

public class LOTRBlockItem extends BlockItem {
	private final int burnTime;

	public LOTRBlockItem(Block block, Properties properties) {
		super(block, properties);
		burnTime = computeBurnTime(block);
	}

	@Override
	public int getBurnTime(ItemStack stack, @Nullable IRecipeType recipeType) {
		return burnTime;
	}

	private static int computeBurnTime(Block block) {
		if (block instanceof LOTRFenceBlock || block instanceof LOTRFenceGateBlock || block instanceof WoodBeamBlock || block instanceof RottenWoodBeamBlock) {
			return 300;
		}
		if (block instanceof LOTRSlabBlock) {
			LOTRSlabBlock slab = (LOTRSlabBlock) block;
			Block full = slab.getModelBlock();
			if (full instanceof WoodBeamBlock || full instanceof RottenWoodBeamBlock || slab instanceof LogSlabBlock) {
				return 150;
			}
		} else if (block instanceof LogStairsBlock || block instanceof BranchBlock) {
			return 300;
		}

		if (block instanceof ReedsBlock || block instanceof ThatchBlock) {
			return 100;
		}
		if (block instanceof ThatchSlabBlock) {
			return 50;
		}
		if (block instanceof ThatchStairsBlock) {
			return 67;
		}
		if (block instanceof WickerFenceBlock || block instanceof WickerFenceGateBlock) {
			return 100;
		}
		return -1;
	}
}
