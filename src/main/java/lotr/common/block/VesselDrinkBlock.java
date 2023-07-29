package lotr.common.block;

import lotr.common.init.*;
import lotr.common.item.*;
import lotr.common.tileentity.VesselDrinkTileEntity;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraftforge.event.ForgeEventFactory;

public class VesselDrinkBlock extends HorizontalBlock {
	public static final DirectionProperty FACING;
	static {
		FACING = HorizontalBlock.FACING;
	}

	private final VoxelShape vesselShape;

	public VesselDrinkBlock(Properties properties, VoxelShape shape) {
		super(properties);
		registerDefaultState(stateDefinition.any().setValue(FACING, Direction.NORTH));
		vesselShape = shape;
	}

	public VesselDrinkBlock(SoundType sound, float width, float height) {
		this(Properties.of(Material.DECORATION).strength(0.0F).sound(sound), createVesselShape(width, height));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		return Block.canSupportCenter(world, pos.below(), Direction.UP);
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(FACING);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.VESSEL_DRINK.get()).create();
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof VesselDrinkTileEntity) {
			VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
			if (!vessel.isEmpty()) {
				ItemStack copy = vessel.getVesselItem().copy();
				copy.setCount(1);
				return copy;
			}
		}

		return super.getPickBlock(state, target, world, pos, player);
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return vesselShape;
	}

	@Override
	public BlockState getStateForPlacement(BlockItemUseContext context) {
		return defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public void handleRain(World world, BlockPos pos) {
		if (world.random.nextInt(20) == 0) {
			float temp = world.getBiome(pos).getTemperature(pos);
			if (temp >= 0.15F) {
				TileEntity te = world.getBlockEntity(pos);
				if (te instanceof VesselDrinkTileEntity) {
					VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
					vessel.fillFromRain();
				}
			}
		}

	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		return facing == Direction.DOWN && !state.canSurvive(world, currentPos) ? Blocks.AIR.defaultBlockState() : super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult trace) {
		ItemStack heldItem = player.getMainHandItem();
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof VesselDrinkTileEntity) {
			VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
			ItemStack itemInVessel = vessel.getVesselItem();
			ItemStack equivalentDrink;
			if (!vessel.isEmpty() && VesselOperations.isItemEmptyVessel(heldItem)) {
				equivalentDrink = itemInVessel.copy();
				VesselType vesselType = VesselOperations.getVessel(heldItem);
				equivalentDrink = VesselOperations.getWithVesselSet(equivalentDrink, vesselType, true);
				if (player.abilities.instabuild) {
					player.setItemInHand(hand, equivalentDrink);
				} else {
					heldItem.shrink(1);
					if (heldItem.isEmpty()) {
						player.setItemInHand(hand, equivalentDrink);
					} else if (!player.inventory.add(equivalentDrink)) {
						player.drop(equivalentDrink, false);
					}
				}

				vessel.setEmpty();
				world.playSound(player, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
				return ActionResultType.SUCCESS;
			}

			if (vessel.isEmpty() && VesselOperations.isItemFullVessel(heldItem)) {
				if (!player.abilities.instabuild) {
					equivalentDrink = VesselOperations.getVessel(heldItem).createEmpty();
					player.setItemInHand(hand, equivalentDrink);
				}

				equivalentDrink = heldItem.copy();
				equivalentDrink.setCount(1);
				vessel.setVesselItem(equivalentDrink);
				world.playSound(player, pos, LOTRSoundEvents.MUG_FILL, SoundCategory.BLOCKS, 0.5F, 0.8F + world.random.nextFloat() * 0.4F);
				return ActionResultType.SUCCESS;
			}

			if (!vessel.isEmpty()) {
				equivalentDrink = VesselOperations.getEquivalentDrink(itemInVessel);
				Item eqItem = equivalentDrink.getItem();
				boolean canDrink = false;
				if (eqItem instanceof VesselDrinkItem) {
					canDrink = ((VesselDrinkItem) eqItem).canBeginDrinking(player, equivalentDrink);
				}

				if (canDrink) {
					ItemStack mugItemResult = itemInVessel.finishUsingItem(world, player);
					ForgeEventFactory.onItemUseFinish(player, itemInVessel, itemInVessel.getUseDuration(), mugItemResult);
					vessel.setEmpty();
					world.playSound(player, pos, SoundEvents.GENERIC_DRINK, SoundCategory.BLOCKS, 0.5F, world.random.nextFloat() * 0.1F + 0.9F);
					return ActionResultType.SUCCESS;
				}
			}
		}

		return super.use(state, world, pos, player, hand, trace);
	}

	private static VoxelShape createVesselShape(float width, float height) {
		float halfWidth = width / 2.0F;
		return Block.box(8.0D - halfWidth, 0.0D, 8.0D - halfWidth, 8.0D + halfWidth, height, 8.0D + halfWidth);
	}

	public static ItemStack getVesselDrinkItem(IWorld world, BlockPos pos) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof VesselDrinkTileEntity) {
			VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
			return vessel.getVesselItem();
		}
		return new ItemStack((IItemProvider) LOTRItems.WOODEN_MUG.get());
	}

	public static VesselDrinkBlock makeAleHorn() {
		return new VesselDrinkBlock(SoundType.STONE, 7.5F, 9.0F);
	}

	public static VesselDrinkBlock makeCeramicMug() {
		return new VesselDrinkBlock(LOTRBlocks.SOUND_CERAMIC, 4.5F, 6.0F);
	}

	public static VesselDrinkBlock makeMetalGoblet() {
		return new VesselDrinkBlock(SoundType.METAL, 3.75F, 6.75F);
	}

	public static VesselDrinkBlock makeWineGlass() {
		return new VesselDrinkBlock(SoundType.GLASS, 3.75F, 7.5F);
	}

	public static VesselDrinkBlock makeWoodenGoblet() {
		return new VesselDrinkBlock(SoundType.METAL, 3.75F, 6.75F);
	}

	public static VesselDrinkBlock makeWoodenMug() {
		return new VesselDrinkBlock(SoundType.WOOD, 4.5F, 6.0F);
	}

	public static void setVesselDrinkItem(IWorld world, BlockPos pos, ItemStack itemstack, VesselType vesselType) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof VesselDrinkTileEntity) {
			VesselDrinkTileEntity vessel = (VesselDrinkTileEntity) te;
			vessel.setVesselItem(itemstack);
			vessel.setVesselType(vesselType);
		}

	}
}
