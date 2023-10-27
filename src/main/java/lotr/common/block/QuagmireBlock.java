package lotr.common.block;

import lotr.common.init.LOTRBlocks;
import lotr.common.init.LOTRDamageSources;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockDisplayReader;
import net.minecraft.world.World;
import net.minecraftforge.common.ToolType;

public class QuagmireBlock extends Block {
	public QuagmireBlock() {
		super(Properties.of(Material.DIRT, MaterialColor.PODZOL).strength(1.0F, 0.5F).noCollission().sound(SoundType.HONEY_BLOCK).harvestTool(ToolType.SHOVEL).isRedstoneConductor(LOTRBlocks::posPredicateTrue).isViewBlocking(LOTRBlocks::posPredicateTrue).isSuffocating(LOTRBlocks::posPredicateTrue));
	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		entity.makeStuckInBlock(state, new Vector3d(0.25D, 0.075D, 0.25D));
		if (entity instanceof LivingEntity && entity.isAlive() && entity.getEyeY() < pos.getY() + 1) {
			entity.hurt(LOTRDamageSources.QUAGMIRE, 1.0F);
		}

	}

	@Override
	public boolean shouldDisplayFluidOverlay(BlockState state, IBlockDisplayReader world, BlockPos pos, FluidState fluidState) {
		return true;
	}
}
