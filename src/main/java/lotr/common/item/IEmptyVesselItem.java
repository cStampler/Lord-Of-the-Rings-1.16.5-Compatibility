package lotr.common.item;

import lotr.common.init.*;
import net.minecraft.block.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.CowEntity;
import net.minecraft.entity.player.*;
import net.minecraft.item.*;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.RayTraceContext.FluidMode;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;

public interface IEmptyVesselItem {
	default ItemStack createWaterVessel(ItemStack heldItem, PlayerEntity player) {
		ItemStack waterDrink = new ItemStack((IItemProvider) LOTRItems.WATER_DRINK.get());
		VesselDrinkItem.setVessel(waterDrink, getVesselType());
		heldItem.shrink(1);
		player.awardStat(Stats.ITEM_USED.get((Item) this));
		if (heldItem.isEmpty()) {
			return waterDrink;
		}
		if (!player.inventory.add(waterDrink)) {
			player.drop(waterDrink, false);
		}

		return heldItem;
	}

	default ActionResult doEmptyVesselRightClick(World world, PlayerEntity player, Hand hand) {
		ItemStack heldItem = player.getItemInHand(hand);
		RayTraceResult target = IEmptyVesselItem.HackyProxyItemImpl.proxyRayTrace(world, player, FluidMode.SOURCE_ONLY);
		if (target.getType() == Type.MISS) {
			return ActionResult.pass(heldItem);
		}
		if (target.getType() == Type.BLOCK) {
			BlockPos pos = ((BlockRayTraceResult) target).getBlockPos();
			if (!world.mayInteract(player, pos)) {
				return ActionResult.pass(heldItem);
			}

			if (world.getFluidState(pos).is(FluidTags.WATER)) {
				world.playSound(player, player.getX(), player.getY(), player.getZ(), LOTRSoundEvents.MUG_FILL, SoundCategory.NEUTRAL, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
				return ActionResult.success(createWaterVessel(heldItem, player));
			}
		}

		return ActionResult.pass(heldItem);
	}

	default ActionResultType doEmptyVesselUseOnBlock(ItemUseContext context) {
		ItemStack heldItem = context.getItemInHand();
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.CAULDRON) {
			int level = state.getValue(CauldronBlock.LEVEL);
			if (level > 0) {
				if (!world.isClientSide) {
					if (!player.abilities.instabuild) {
						ItemStack waterDrink = new ItemStack((IItemProvider) LOTRItems.WATER_DRINK.get());
						VesselDrinkItem.setVessel(waterDrink, getVesselType());
						player.awardStat(Stats.USE_CAULDRON);
						heldItem.shrink(1);
						if (heldItem.isEmpty()) {
							player.setItemInHand(context.getHand(), waterDrink);
						} else if (!player.inventory.add(waterDrink)) {
							player.drop(waterDrink, false);
						} else if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity) player).refreshContainer(player.inventoryMenu);
						}
					}

					world.playSound((PlayerEntity) null, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
					((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, level - 1);
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}

	VesselType getVesselType();

	default ActionResultType tryToPlaceVesselBlock(ItemUseContext context) {
		return ActionResultType.PASS;
	}

	static boolean canMilk(Entity target) {
		return target instanceof CowEntity && !((CowEntity) target).isBaby();
	}

	public static final class HackyProxyItemImpl extends Item {
		public HackyProxyItemImpl(Properties properties) {
			super(properties);
		}

		protected static RayTraceResult proxyRayTrace(World world, PlayerEntity player, FluidMode fluidMode) {
			return Item.getPlayerPOVHitResult(world, player, fluidMode);
		}
	}
}
