package lotr.common.block;

import java.util.List;
import java.util.stream.Stream;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.common.Tags.Items;
import net.minecraftforge.common.extensions.IForgeBlock;
import net.minecraftforge.common.extensions.IForgeBlockState;

public class WattleAndDaubBlock extends Block implements IForgeBlockState {
	public static final BooleanProperty CONNECTED;

	static {
		CONNECTED = LOTRBlockStates.WATTLE_CONNECTED;
	}

	public WattleAndDaubBlock() {
		this(Properties.of(Material.GRASS, MaterialColor.QUARTZ).strength(1.0F).sound(SoundType.SCAFFOLDING));
	}

	public WattleAndDaubBlock(Properties props) {
		super(props);
		registerDefaultState(defaultBlockState().setValue(CONNECTED, true));
	}

	@Override
	protected void createBlockStateDefinition(Builder builder) {
		builder.add(CONNECTED);
	}

	@Override
	public int getFireSpreadSpeed(IBlockReader world, BlockPos pos, Direction face) {
		return 40;
	}

	@Override
	public int getFlammability(IBlockReader world, BlockPos pos, Direction face) {
		return 40;
	}

	private boolean isSurroundedByAnyWattle(IWorld world, BlockPos pos) {
		Stream var10000 = Stream.of(Direction.values());
		pos.getClass();
		var10000 = var10000.map(hummel -> pos.relative((Direction) hummel));
		world.getClass();
		return var10000.map(hummel -> world.getBlockState((BlockPos) hummel)).anyMatch(state -> (((IForgeBlock) state).getBlock() == this));
	}

	@Override
	public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, IWorld world, BlockPos currentPos, BlockPos facingPos) {
		if (state.getValue(CONNECTED) && !isSurroundedByAnyWattle(world, currentPos)) {
			state = state.setValue(CONNECTED, true);
		}

		return super.updateShape(state, facing, facingState, world, currentPos, facingPos);
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult target) {
		ItemStack heldItem = player.getItemInHand(hand);
		if (heldItem.getItem().is(Items.RODS_WOODEN) && isSurroundedByAnyWattle(world, pos)) {
			world.setBlockAndUpdate(pos, state.setValue(CONNECTED, !(Boolean) state.getValue(CONNECTED)));
			world.playSound(player, pos, soundType.getPlaceSound(), SoundCategory.BLOCKS, (soundType.getVolume() + 1.0F) / 4.0F, soundType.getPitch() * 1.0F);
			world.levelEvent(2001, pos, Block.getId(state));
			return ActionResultType.SUCCESS;
		}
		return super.use(state, world, pos, player, hand, target);
	}

	public static boolean doBlocksConnectVisually(BlockState state, BlockState otherState, List connectOffsets) {
		Block otherBlock = otherState.getBlock();
		if (!(otherBlock instanceof WattleAndDaubBlock)) {
			return false;
		}
		return state.getValue(CONNECTED) && otherState.getValue(CONNECTED);
	}
}
