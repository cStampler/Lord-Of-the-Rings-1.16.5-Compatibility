package lotr.common.item;

import com.google.common.collect.ImmutableList;

import lotr.common.init.LOTRSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CauldronBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VesselWaterItem extends VesselDrinkItem {
	public VesselWaterItem() {
		super(0.0F, 0, 0.0F, false, 0.0F, ImmutableList.of());
	}

	@Override
	public boolean canBeginDrinking(PlayerEntity player, ItemStack stack) {
		return true;
	}

	@Override
	public ActionResultType useOn(ItemUseContext context) {
		ActionResultType vesselPlaceResult = super.useOn(context);
		if (vesselPlaceResult.consumesAction()) {
			return vesselPlaceResult;
		}
		ItemStack heldItem = context.getItemInHand();
		PlayerEntity player = context.getPlayer();
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		BlockState state = world.getBlockState(pos);
		if (state.getBlock() == Blocks.CAULDRON) {
			int level = state.getValue(CauldronBlock.LEVEL);
			if (level < 3) {
				if (!world.isClientSide) {
					if (!player.abilities.instabuild) {
						ItemStack emptyVessel = getVessel(heldItem).createEmpty();
						player.awardStat(Stats.USE_CAULDRON);
						player.setItemInHand(context.getHand(), emptyVessel);
						if (player instanceof ServerPlayerEntity) {
							((ServerPlayerEntity) player).refreshContainer(player.inventoryMenu);
						}
					}

					world.playSound((PlayerEntity) null, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
					((CauldronBlock) state.getBlock()).setWaterLevel(world, pos, state, level + 1);
				}

				return ActionResultType.SUCCESS;
			}
		}

		return ActionResultType.PASS;
	}
}
