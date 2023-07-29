package lotr.common.event;

import lotr.common.item.RedBookItem;
import lotr.common.world.map.CustomWaypointStructureHandler;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.player.*;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;

public class TerrainProtections {
	private static BlockPos getEditingPos(PlayerEvent event, BlockPos pos, Direction side, ItemStack heldItem) {
		boolean isOffsetPos = isOffsetPosClick(event, heldItem);
		return isOffsetPos && side != null ? pos.relative(side) : pos;
	}

	private static boolean isOffsetPosClick(PlayerEvent event, ItemStack heldItem) {
		if (!(event instanceof RightClickBlock) && !(event instanceof FillBucketEvent)) {
			return false;
		}
		Item item = heldItem.getItem();
		return item instanceof BlockItem || item instanceof FlintAndSteelItem || item instanceof BucketItem && ((BucketItem) item).getFluid() != Fluids.EMPTY || item instanceof HangingEntityItem;
	}

	public static boolean isTerrainProtectedFromExplosion(World world, BlockPos pos) {
		return CustomWaypointStructureHandler.INSTANCE.isProtectedByWaypointStructure(world, pos);
	}

	public static boolean isTerrainProtectedFromPlayerEdits(PlayerEvent event, ItemStack heldItem, BlockPos pos, Direction side) {
		PlayerEntity player = event.getPlayer();
		World world = player.level;
		if (!world.isClientSide && !(heldItem.getItem() instanceof RedBookItem)) {
			BlockPos editPos = getEditingPos(event, pos, side, heldItem);
			if (CustomWaypointStructureHandler.INSTANCE.isProtectedByWaypointStructure(world, editPos, player)) {
				return true;
			}
		}

		return false;
	}
}
