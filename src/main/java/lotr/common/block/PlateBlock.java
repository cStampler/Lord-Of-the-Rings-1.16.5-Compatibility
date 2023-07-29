package lotr.common.block;

import lotr.common.init.LOTRTileEntities;
import lotr.common.tileentity.PlateTileEntity;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;

public class PlateBlock extends Block {
	private static final VoxelShape PLATE_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 2.0D, 14.0D);

	public PlateBlock(Properties properties) {
		super(properties);
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return Block.canSupportCenter(world, pos.below(), Direction.UP);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.PLATE.get()).create();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		PlateTileEntity plate = (PlateTileEntity) world.getBlockEntity(pos);
		ItemStack foodItem = plate.getFoodItem();
		if (!foodItem.isEmpty()) {
			ItemStack copy = foodItem.copy();
			copy.setCount(1);
			return copy;
		}
		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return PLATE_SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof PlateTileEntity) {
				PlateTileEntity plate = (PlateTileEntity) te;
				InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), plate.getFoodItem());
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}

	}

	public boolean popOffOneItem(World world, BlockPos pos, PlayerEntity player) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof PlateTileEntity) {
			PlateTileEntity plate = (PlateTileEntity) te;
			return popOffOneItem1(plate, world, pos);
		}
		return false;
	}

	private boolean popOffOneItem1(PlateTileEntity plate, World world, BlockPos pos) {
		ItemStack plateItem = plate.getFoodItem();
		if (!plateItem.isEmpty()) {
			ItemStack dropItem = plateItem.copy();
			dropItem.setCount(1);
			InventoryHelper.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), dropItem);
			plateItem.shrink(1);
			plate.setFoodItem(plateItem);
			return true;
		}
		return false;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		ItemStack heldItem = player.getMainHandItem();
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof PlateTileEntity) {
			PlateTileEntity plate = (PlateTileEntity) te;
			ItemStack plateItem = plate.getFoodItem();
			SoundType sound;
			if (plateItem.isEmpty() && PlateTileEntity.isValidFoodItem(heldItem)) {
				if (!world.isClientSide) {
					plateItem = heldItem.copy();
					plateItem.setCount(1);
					plate.setFoodItem(plateItem);
				}

				if (!player.abilities.instabuild) {
					heldItem.shrink(1);
				}

				sound = state.getSoundType(world, pos, player);
				world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 4.0F, sound.getPitch() * 1.0F);
				return ActionResultType.SUCCESS;
			}

			if (!plateItem.isEmpty()) {
				if (heldItem.sameItem(plateItem) && ItemStack.tagMatches(heldItem, plateItem) && plateItem.getCount() < PlateTileEntity.getMaxStackSizeOnPlate(plateItem)) {
					if (!world.isClientSide) {
						plateItem.grow(1);
						plate.setFoodItem(plateItem);
					}

					if (!player.abilities.instabuild) {
						heldItem.shrink(1);
					}

					sound = state.getSoundType(world, pos, player);
					world.playSound(player, pos, sound.getPlaceSound(), SoundCategory.BLOCKS, (sound.getVolume() + 1.0F) / 4.0F, sound.getPitch() * 1.0F);
					return ActionResultType.SUCCESS;
				}

				if (!plateItem.isEdible()) {
					if (!world.isClientSide) {
						popOffOneItem1(plate, world, pos);
					}

					return ActionResultType.SUCCESS;
				}

				if (player.canEat(false)) {
					ItemStack onEaten = plateItem.finishUsingItem(world, player);
					if (!world.isClientSide) {
						plate.setFoodItem(onEaten);
					}

					return ActionResultType.SUCCESS;
				}
			}
		}

		return super.use(state, world, pos, player, hand, trace);
	}
}
