package lotr.common.block;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.SoundType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.state.EnumProperty;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VerticalOnlySlabBlock extends AxialSlabBlock {
	private static final BiMap VANILLA_SLABS_TO_VERTICAL_SLABS = HashBiMap.create();
	private final SlabBlock correspondingVanillaSlab;

	public VerticalOnlySlabBlock(Block vanillaSlab) {
		super(vanillaSlab);
		if (!(vanillaSlab instanceof SlabBlock)) {
			throw new IllegalArgumentException("Can only construct a VerticalOnlySlabBlock from a SlabBlock");
		}
		correspondingVanillaSlab = (SlabBlock) vanillaSlab;
		if (VANILLA_SLABS_TO_VERTICAL_SLABS.containsKey(correspondingVanillaSlab)) {
			throw new IllegalArgumentException("Vanilla slab " + correspondingVanillaSlab.getRegistryName() + " already corresponds to a VerticalOnlySlabBlock!");
		}
		VANILLA_SLABS_TO_VERTICAL_SLABS.put(correspondingVanillaSlab, this);
	}

	private boolean canPlace(BlockItemUseContext context, BlockState stateToPlace) {
		World world = context.getLevel();
		BlockPos pos = context.getClickedPos();
		PlayerEntity player = context.getPlayer();
		ISelectionContext selection = player == null ? ISelectionContext.empty() : ISelectionContext.of(player);
		return stateToPlace.canSurvive(world, pos) && world.isUnobstructed(stateToPlace, pos, selection);
	}

	@Override
	public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
		SlabBlock vanillaSlab = getVanillaSlabFor(this);
		return new ItemStack(vanillaSlab);
	}

	@Override
	protected EnumProperty getSlabAxisProperty() {
		return LOTRBlockStates.VERTICAL_ONLY_SLAB_AXIS;
	}

	private BlockState getStateForVerticalOrVanillaPlacement(BlockItemUseContext context) {
		AxialSlabBlock.AxialSlabPlacement slabPlacement = getSlabPlacementState(context);
		Axis placeAxis = slabPlacement.axis;
		BlockState stateToPlace = placeAxis == Axis.Y ? correspondingVanillaSlab.defaultBlockState() : (BlockState) defaultBlockState().setValue(getSlabAxisProperty(), placeAxis);
		stateToPlace = stateToPlace.setValue(TYPE, slabPlacement.slabType).setValue(WATERLOGGED, slabPlacement.waterlogged);
		return canPlace(context, stateToPlace) ? stateToPlace : null;
	}

	@Override
	protected boolean isSameSlab(SlabBlock otherSlab) {
		return otherSlab == this || otherSlab == getVanillaSlabFor(this);
	}

	public ActionResultType placeVerticalOrVanilla(PlayerEntity player, Hand hand, ItemStack heldItem, World world, BlockPos pos, Direction side, BlockRayTraceResult blockRayTrace) {
		ItemUseContext itemUseContext = new ItemUseContext(player, hand, blockRayTrace);
		BlockItemUseContext blockItemUseContext = new AxialSlabBlock.AxialSlabUseContext(itemUseContext);
		if (!blockItemUseContext.canPlace()) {
			return ActionResultType.FAIL;
		}
		BlockPos placePos = blockItemUseContext.getClickedPos();
		BlockState stateToPlace = getStateForVerticalOrVanillaPlacement(blockItemUseContext);
		if (stateToPlace == null || !world.setBlock(placePos, stateToPlace, 11)) {
			return ActionResultType.FAIL;
		}
		BlockState placedState = world.getBlockState(placePos);
		Block placedBlock = placedState.getBlock();
		if (placedBlock == stateToPlace.getBlock() && player instanceof ServerPlayerEntity) {
			CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayerEntity) player, placePos, heldItem);
		}

		SoundType blockSound = placedState.getSoundType(world, placePos, player);
		world.playSound(player, placePos, blockSound.getPlaceSound(), SoundCategory.BLOCKS, (blockSound.getVolume() + 1.0F) / 2.0F, blockSound.getPitch() * 0.8F);
		if (!player.abilities.instabuild) {
			heldItem.shrink(1);
		}

		return ActionResultType.SUCCESS;
	}

	public static SlabBlock getVanillaSlabFor(VerticalOnlySlabBlock verticalSlab) {
		return (SlabBlock) VANILLA_SLABS_TO_VERTICAL_SLABS.inverse().get(verticalSlab);
	}

	public static VerticalOnlySlabBlock getVerticalSlabFor(SlabBlock vanillaSlab) {
		return (VerticalOnlySlabBlock) VANILLA_SLABS_TO_VERTICAL_SLABS.get(vanillaSlab);
	}
}
