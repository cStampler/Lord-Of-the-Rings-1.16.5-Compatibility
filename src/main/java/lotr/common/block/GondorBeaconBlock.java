package lotr.common.block;

import java.util.Random;

import lotr.common.init.LOTRTileEntities;
import lotr.common.item.PocketMatchItem;
import lotr.common.stat.LOTRStats;
import lotr.common.tileentity.GondorBeaconTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class GondorBeaconBlock extends Block {
	public static final BooleanProperty FULLY_LIT;
	private static final VoxelShape BEACON_SHAPE;

	static {
		FULLY_LIT = LOTRBlockStates.BEACON_FULLY_LIT;
		BEACON_SHAPE = Block.box(0.0D, 0.0D, 0.0D, 16.0D, 13.0D, 16.0D);
	}

	public GondorBeaconBlock() {
		super(Properties.of(Material.WOOD).strength(2.0F, 3.0F).noOcclusion().sound(SoundType.WOOD));
		registerDefaultState(defaultBlockState().setValue(FULLY_LIT, false));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (isBurning(world, pos)) {
			if (rand.nextInt(24) == 0) {
				world.playLocalSound(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, SoundEvents.FIRE_AMBIENT, SoundCategory.BLOCKS, 1.0F + rand.nextFloat(), rand.nextFloat() * 0.7F + 0.3F, false);
			}

			for (int l = 0; l < 3; ++l) {
				double px = pos.getX() + rand.nextFloat();
				double py = pos.getY() + 0.5F + rand.nextFloat() * 0.5F;
				double pz = pos.getZ() + rand.nextFloat();
				world.addParticle(ParticleTypes.LARGE_SMOKE, px, py, pz, 0.0D, 0.0D, 0.0D);
			}
		}

	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		return world.getBlockState(belowPos).isFaceSturdy(world, belowPos, Direction.UP);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FULLY_LIT);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.GONDOR_BEACON.get()).create();
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		if (entity.isOnFire() && !isBurning(world, pos) && !isWaterAbove(world, pos)) {
			playLightSound(world, pos);
			if (!world.isClientSide) {
				beginBurning(world, pos);
			}
		}

	}

	@Override
	public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
		return isFullyLit(state) ? 15 : 0;
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return BEACON_SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	private boolean isWaterAbove(IWorldReader world, BlockPos pos) {
		return world.getFluidState(pos.above()).is(FluidTags.WATER);
	}

	private void playExtinguishSound(IWorld world, BlockPos pos) {
		world.playSound((PlayerEntity) null, pos, SoundEvents.FIRE_EXTINGUISH, SoundCategory.BLOCKS, 0.5F, 2.6F + (world.getRandom().nextFloat() - world.getRandom().nextFloat()) * 0.8F);
	}

	private void playLightSound(IWorld world, BlockPos pos) {
		world.playSound((PlayerEntity) null, pos, SoundEvents.FLINTANDSTEEL_USE, SoundCategory.BLOCKS, 1.0F, world.getRandom().nextFloat() * 0.4F + 0.8F);
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (facing == Direction.DOWN && !state.canSurvive(world, currentPos)) {
			return Blocks.AIR.defaultBlockState();
		}
		if (facing == Direction.UP && isBurning(world, currentPos) && isWaterAbove(world, currentPos)) {
			playExtinguishSound(world, currentPos);
			if (!world.isClientSide()) {
				extinguish(world, currentPos);
			}
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult target) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (canItemLightBeacon(heldItem) && !isBurning(world, pos) && !isWaterAbove(world, pos)) {
			playLightSound(world, pos);
			if (!player.abilities.instabuild) {
				if (heldItem.isDamageableItem()) {
					heldItem.hurtAndBreak(1, player, p -> {
						p.broadcastBreakEvent(hand);
					});
				} else if (heldItem.getMaxStackSize() > 1) {
					heldItem.shrink(1);
				}
			}

			if (!world.isClientSide) {
				beginBurning(world, pos);
			}

			player.awardStat(LOTRStats.LIGHT_GONDOR_BEACON);
			return ActionResultType.SUCCESS;
		}
		if (!canItemExtinguishBeacon(heldItem) || !isBurning(world, pos)) {
			return super.use(state, world, pos, player, hand, target);
		}
		playExtinguishSound(world, pos);
		if (!player.abilities.instabuild) {
			if (heldItem.hasContainerItem()) {
				player.setItemInHand(hand, heldItem.getContainerItem());
			} else {
				heldItem.shrink(1);
			}
		}

		if (!world.isClientSide) {
			extinguish(world, pos);
		}

		player.awardStat(LOTRStats.EXTINGUISH_GONDOR_BEACON);
		return ActionResultType.SUCCESS;
	}

	public static void beginBurning(IBlockReader world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof GondorBeaconTileEntity) {
			((GondorBeaconTileEntity) te).beginBurning();
		}

	}

	private static boolean canItemExtinguishBeacon(ItemStack itemstack) {
		Item item = itemstack.getItem();
		return item instanceof BucketItem && ((BucketItem) item).getFluid().is(FluidTags.WATER);
	}

	private static boolean canItemLightBeacon(ItemStack itemstack) {
		Item item = itemstack.getItem();
		return item == Items.FLINT_AND_STEEL || item instanceof PocketMatchItem || item instanceof BlockItem && ((BlockItem) item).getBlock() instanceof TorchBlock;
	}

	public static void extinguish(IBlockReader world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof GondorBeaconTileEntity) {
			((GondorBeaconTileEntity) te).extinguish();
		}

	}

	public static boolean isBurning(IBlockReader world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		return te instanceof GondorBeaconTileEntity && ((GondorBeaconTileEntity) te).isBurning();
	}

	public static boolean isFullyLit(BlockState state) {
		return state.getValue(FULLY_LIT);
	}
}
