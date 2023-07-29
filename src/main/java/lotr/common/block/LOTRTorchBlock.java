package lotr.common.block;

import java.util.*;
import java.util.function.*;

import lotr.common.init.LOTRBlocks;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.particles.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.*;

public class LOTRTorchBlock extends TorchBlock {
	protected List torchParticles;

	public LOTRTorchBlock(int light) {
		this(light, SoundType.WOOD);
	}

	public LOTRTorchBlock(int light, SoundType sound) {
		this(LOTRBlocks.constantLight(light), sound);
	}

	public LOTRTorchBlock(Properties properties) {
		super(properties, ParticleTypes.FLAME);
		setParticles(() -> ParticleTypes.SMOKE, () -> ParticleTypes.FLAME);
	}

	public LOTRTorchBlock(ToIntFunction lightLevel, SoundType sound) {
		this(Properties.of(Material.DECORATION).noCollission().strength(0.0F).lightLevel(lightLevel).sound(sound));
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		animateTorch(world, pos, rand, 0.5D, 0.7D, 0.5D);
	}

	protected void animateTorch(World world, BlockPos pos, Random rand, double x, double y, double z) {
		double d0 = pos.getX() + x;
		double d1 = pos.getY() + y;
		double d2 = pos.getZ() + z;
		Iterator var16 = torchParticles.iterator();

		while (var16.hasNext()) {
			Supplier particle = (Supplier) var16.next();
			world.addParticle((IParticleData) particle.get(), d0, d1, d2, 0.0D, 0.0D, 0.0D);
		}

	}

	public List copyTorchParticles() {
		return new ArrayList(torchParticles);
	}

	public LOTRTorchBlock setParticles(Supplier... pars) {
		torchParticles = Arrays.asList(pars);
		return this;
	}
}
