package lotr.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.World;

public class PocketMatchItem extends Item {
	public PocketMatchItem(Properties properties) {
		super(properties);
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		World world = context.getLevel();
		ItemStack itemstack = context.getItemInHand();
		ItemStack proxyItem = new ItemStack(Items.FLINT_AND_STEEL);
		ItemUseContext proxyContext = new PocketMatchItem.ProxyItemUse(world, context.getPlayer(), context.getHand(), proxyItem, new BlockRayTraceResult(context.getClickLocation(), context.getClickedFace(), context.getClickedPos(), context.isInside()));
		if (proxyItem.useOn(proxyContext) == ActionResultType.SUCCESS) {
			itemstack.shrink(1);
			return ActionResultType.SUCCESS;
		}
		return ActionResultType.PASS;
	}

	private static class ProxyItemUse extends ItemUseContext {
		protected ProxyItemUse(World world, PlayerEntity player, Hand hand, ItemStack heldItem, BlockRayTraceResult hit) {
			super(world, player, hand, heldItem, hit);
		}
	}
}
