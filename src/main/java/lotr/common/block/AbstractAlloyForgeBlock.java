package lotr.common.block;

import java.util.Random;
import java.util.function.ToIntFunction;

import lotr.common.tileentity.AbstractAlloyForgeTileEntity;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.*;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateHolder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;
import net.minecraftforge.fml.network.NetworkHooks;

public abstract class AbstractAlloyForgeBlock extends AbstractFurnaceBlock {
	public AbstractAlloyForgeBlock(MaterialColor color) {
		this(Properties.of(Material.STONE, color).requiresCorrectToolForDrops().strength(3.5F).lightLevel(lightIfLit(13)));
	}

	public AbstractAlloyForgeBlock(Properties properties) {
		super(properties);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (state.getValue(LIT)) {
			double x = pos.getX() + 0.5D;
			double y = pos.getY();
			double z = pos.getZ() + 0.5D;
			if (rand.nextDouble() < 0.1D) {
				world.playLocalSound(x, y, z, SoundEvents.FURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
			}

			Direction dir = state.getValue(FACING);
			Axis axis = dir.getAxis();
			double out = 0.52D;
			double front = rand.nextDouble() * 0.6D - 0.3D;
			double partX = axis == Axis.X ? dir.getStepX() * out : front;
			double partY = rand.nextDouble() * 6.0D / 16.0D;
			double partZ = axis == Axis.Z ? dir.getStepZ() * out : front;
			world.addParticle(ParticleTypes.SMOKE, x + partX, y + partY, z + partZ, 0.0D, 0.0D, 0.0D);
			world.addParticle(ParticleTypes.FLAME, x + partX, y + partY, z + partZ, 0.0D, 0.0D, 0.0D);
			if (!LOTRUtil.hasSolidSide(world, pos.above(), Direction.DOWN)) {
				for (int l = 0; l < 2; ++l) {
					double smokeX = x + rand.nextDouble() * 0.1D - 0.05D;
					double smokeY = y + 1.0D;
					double smokeZ = z + rand.nextDouble() * 0.1D - 0.05D;
					world.addParticle(ParticleTypes.SMOKE, smokeX, smokeY, smokeZ, 0.0D, 0.0D, 0.0D);
				}
			}
		}

	}

	protected abstract ResourceLocation getForgeInteractionStat();

	@Override
	public void onRemove(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving) {
		if (state.getBlock() != newState.getBlock()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof AbstractAlloyForgeTileEntity) {
				InventoryHelper.dropContents(world, pos, (AbstractAlloyForgeTileEntity) te);
				world.updateNeighbourForOutputSignal(pos, this);
			}

			super.onRemove(state, world, pos, newState, isMoving);
		}

	}

	@Override
	protected void openContainer(World world, BlockPos pos, PlayerEntity player) {
		TileEntity te = world.getBlockEntity(pos);
		if (te instanceof INamedContainerProvider) {
			NetworkHooks.openGui((ServerPlayerEntity) player, (INamedContainerProvider) te, extraData -> {
				extraData.writeBlockPos(pos);
			});
			player.awardStat(getForgeInteractionStat());
		}

	}

	@Override
	public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
		if (stack.hasCustomHoverName()) {
			TileEntity te = world.getBlockEntity(pos);
			if (te instanceof AbstractAlloyForgeTileEntity) {
				((AbstractAlloyForgeTileEntity) te).setCustomName(stack.getHoverName());
			}
		}

	}

	protected static ToIntFunction lightIfLit(int lightValue) {
		return state -> (((StateHolder<Block, BlockState>) state).getValue(BlockStateProperties.LIT) ? lightValue : 0);
	}
}
