package lotr.common.block;

import lotr.common.init.*;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.*;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.common.ToolType;

public class PalantirBlock extends Block {
	private static final VoxelShape SHAPE = Block.box(1.0D, 0.0D, 1.0D, 15.0D, 14.0D, 15.0D);

	public PalantirBlock() {
		super(Properties.of(LOTRBlockMaterial.PALANTIR).requiresCorrectToolForDrops().strength(5.0F, 6.0F).noOcclusion().lightLevel(LOTRBlocks.constantLight(3)).sound(SoundType.GLASS).harvestTool(ToolType.PICKAXE).harvestLevel(2));
	}

	@Override
	public boolean canSurvive(BlockState state, IWorldReader world, BlockPos pos) {
		BlockPos belowPos = pos.below();
		return world.getBlockState(belowPos).isFaceSturdy(world, belowPos, Direction.UP);
	}

	@Override
	public TileEntity createTileEntity(BlockState state, IBlockReader world) {
		return ((TileEntityType) LOTRTileEntities.PALANTIR.get()).create();
	}

	@Override
	public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
		return SHAPE;
	}

	@Override
	public boolean hasTileEntity(BlockState state) {
		return true;
	}

	@Override
	public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult target) {
		if (!world.isClientSide) {
			LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.palantir.ponder"));
			world.playSound((PlayerEntity) null, pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, LOTRSoundEvents.PALANTIR_PONDER, SoundCategory.BLOCKS, 1.0F, 1.0F);
		}

		return ActionResultType.SUCCESS;
	}
}
