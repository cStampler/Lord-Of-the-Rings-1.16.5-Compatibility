package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRSoundEvents;
import lotr.common.init.LOTRTileEntities;
import lotr.common.item.IEmptyVesselItem;
import lotr.common.item.VesselDrinkItem;
import lotr.common.item.VesselType;
import lotr.common.stat.LOTRStats;
import lotr.common.tileentity.KegTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.network.NetworkHooks;

public class KegBlock extends HorizontalBlock implements IWaterLoggable {
	public static final BooleanProperty UP;
	public static final BooleanProperty OPEN;
	public static final BooleanProperty WATERLOGGED;
	private static final VoxelShape KEG_SHAPE;

	static {
		UP = BlockStateProperties.UP;
		OPEN = BlockStateProperties.OPEN;
		WATERLOGGED = BlockStateProperties.WATERLOGGED;
		KEG_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);
	}

	public KegBlock() {
		this(Properties.of(Material.WOOD, MaterialColor.COLOR_BROWN).strength(3.0F, 5.0F).sound(SoundType.WOOD));
	}

	public KegBlock(Properties properties) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(UP, false).setValue(OPEN, false).setValue(WATERLOGGED, false));
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING, UP, OPEN, WATERLOGGED);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.KEG.get()).create();
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
		return Container.getRedstoneSignalFromBlockEntity(world.getBlockEntity(pos));
	}

	@Override
	public FluidState getFluidState(BlockState state) {
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return KEG_SHAPE;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		Direction facing = context.getHorizontalDirection().getOpposite();
		boolean up = context.getLevel().getBlockState(context.getClickedPos().above()).getBlock() == this;
		FluidState fluid = context.getLevel().getFluidState(context.getClickedPos());
		return defaultBlockState().setValue(FACING, facing).setValue(UP, up).setValue(WATERLOGGED, fluid.getType() == Fluids.WATER);
	}

	@Override
	public boolean hasAnalogOutputSignal(BlockState state) {
		return true;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof KegTileEntity) {
				KegTileEntity keg = (KegTileEntity) te;
				keg.dropContentsExceptBrew();
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}

	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof KegTileEntity) {
				((KegTileEntity) te).setCustomName(stack.getHoverName());
			}
		}

	}

	@Override
	public void tick(BlockState state, ServerWorld world, BlockPos pos, Random rand) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof KegTileEntity) {
			((KegTileEntity) te).kegTick();
		}

	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(WATERLOGGED)) {
			world.getLiquidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickDelay(world));
		}

		if (facing != Direction.UP) {
			return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
		}
		Block block = facingState.getBlock();
		return state.setValue(UP, block == this);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult target) {
		ItemStack heldItem = player.getItemInHand(hand);
		TileEntity te = world.getBlockEntity(pos);
		if (!(te instanceof KegTileEntity)) {
			return super.use(state, world, pos, player, hand, target);
		}
		KegTileEntity keg = (KegTileEntity) te;
		ItemStack kegDrink = keg.getFinishedBrewDrink();
		if (target.getDirection() == state.getValue(FACING)) {
			ItemStack kegFill;
			if (!kegDrink.isEmpty() && heldItem.getItem() instanceof IEmptyVesselItem) {
				VesselType ves = ((IEmptyVesselItem) heldItem.getItem()).getVesselType();
				kegFill = kegDrink.copy();
				kegFill.setCount(1);
				VesselDrinkItem.setVessel(kegFill, ves);
				if (!player.abilities.instabuild) {
					heldItem.shrink(1);
				}

				if (heldItem.isEmpty()) {
					player.setItemInHand(hand, kegFill);
				} else if (!player.inventory.add(kegFill)) {
					player.drop(kegFill, false);
				}

				keg.consumeServing();
				if (!world.isClientSide) {
					world.playSound((PlayerEntity) null, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
				}

				player.awardStat(LOTRStats.INTERACT_KEG);
				return ActionResultType.SUCCESS;
			}

			if (!heldItem.isEmpty() && heldItem.getItem() instanceof VesselDrinkItem && ((VesselDrinkItem) heldItem.getItem()).hasPotencies) {
				boolean match = false;
				if (keg.getKegMode() == KegTileEntity.KegMode.EMPTY) {
					match = true;
				} else if (!kegDrink.isEmpty() && kegDrink.getCount() < 16) {
					match = kegDrink.getItem() == heldItem.getItem() && VesselDrinkItem.getPotency(kegDrink) == VesselDrinkItem.getPotency(heldItem);
				}

				if (match) {
					if (kegDrink.isEmpty()) {
						kegFill = heldItem.copy();
						kegFill.setCount(1);
						VesselDrinkItem.setVessel(kegFill, VesselType.WOODEN_MUG);
						keg.fillBrewedWith(kegFill);
					} else {
						kegDrink.grow(1);
						keg.fillBrewedWith(kegDrink);
					}

					if (!player.abilities.instabuild) {
						VesselType ves = VesselDrinkItem.getVessel(heldItem);
						ItemStack emptyMug = ves.createEmpty();
						heldItem.shrink(1);
						if (heldItem.isEmpty()) {
							player.setItemInHand(hand, emptyMug);
							player.containerMenu.broadcastChanges();
						} else if (!player.inventory.add(emptyMug)) {
							player.drop(emptyMug, false);
						}
					}

					if (!world.isClientSide) {
						world.playSound((PlayerEntity) null, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
					}

					player.awardStat(LOTRStats.INTERACT_KEG);
					return ActionResultType.SUCCESS;
				}
			}
		}

		if (!world.isClientSide) {
			NetworkHooks.openGui((ServerPlayerEntity) player, keg, extraData -> {
			});
			player.awardStat(LOTRStats.INTERACT_KEG);
		}

		return ActionResultType.SUCCESS;
	}
}
