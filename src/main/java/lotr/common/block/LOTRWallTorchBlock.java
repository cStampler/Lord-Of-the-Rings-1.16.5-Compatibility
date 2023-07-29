package lotr.common.block;

import java.util.*;
import java.util.function.Supplier;

import net.minecraft.block.*;
import net.minecraft.particles.*;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

public class LOTRWallTorchBlock extends WallTorchBlock {
	private List<Supplier<? extends IParticleData>> torchParticles;

	public LOTRWallTorchBlock(Properties properties) {
		super(properties, ParticleTypes.FLAME);
		torchParticles = Arrays.asList(() -> ParticleTypes.SMOKE, () -> ParticleTypes.FLAME);
	}

	public LOTRWallTorchBlock(SoundType sound, Supplier lootBlock) {
		this(Properties.copy((AbstractBlock) lootBlock.get()).dropsLike((Block) lootBlock.get()));
		torchParticles = ((LOTRTorchBlock) lootBlock.get()).copyTorchParticles();
	}

	public LOTRWallTorchBlock(Supplier lootBlock) {
		this(SoundType.WOOD, lootBlock);
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		Direction facing = state.getValue(FACING);
		double d0 = pos.getX() + 0.5D;
		double d1 = pos.getY() + 0.7D;
		double d2 = pos.getZ() + 0.5D;
		double up = 0.22D;
		double across = 0.27D;
		Direction opposite = facing.getOpposite();
		Iterator var17 = torchParticles.iterator();

		while (var17.hasNext()) {
			Supplier particle = (Supplier) var17.next();
			world.addParticle((IParticleData) particle.get(), d0 + across * opposite.getStepX(), d1 + up, d2 + across * opposite.getStepZ(), 0.0D, 0.0D, 0.0D);
		}

	}
}
