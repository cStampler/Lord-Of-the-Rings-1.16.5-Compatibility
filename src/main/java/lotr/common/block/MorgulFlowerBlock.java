package lotr.common.block;

import java.util.List;
import java.util.Random;

import lotr.common.data.LOTRLevelData;
import lotr.common.fac.FactionPointers;
import lotr.common.init.LOTRParticles;
import lotr.common.init.LOTRTags;
import lotr.common.util.LOTRUtil;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MorgulFlowerBlock extends LOTRFlowerBlock {
	private static final VoxelShape MORGUL_FLOWER_SHAPE = Block.box(2.0D, 0.0D, 2.0D, 14.0D, 13.0D, 14.0D);

	public MorgulFlowerBlock() {
		super(Effects.BLINDNESS, 10, createDefaultFlowerProperties().randomTicks());
		flowerShape = MORGUL_FLOWER_SHAPE;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public void animateTick(BlockState state, World world, BlockPos pos, Random rand) {
		if (rand.nextInt(4) == 0) {
			Vector3d offset = state.getOffset(world, pos);
			double x = pos.getX() + offset.x;
			double y = pos.getY() + offset.y;
			double z = pos.getZ() + offset.z;
			x += MathHelper.nextFloat(rand, 0.1F, 0.9F);
			y += MathHelper.nextFloat(rand, 0.5F, 0.75F);
			z += MathHelper.nextFloat(rand, 0.1F, 0.9F);
			/*if (rand.nextBoolean()) {
				world.addParticle((IParticleData) LOTRParticles.MORGUL_WATER_EFFECT.get(), x, y, z, 0.0D, 0.0D, 0.0D);
			} else {
				world.addParticle((IParticleData) LOTRParticles.WHITE_SMOKE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
			}*/
			world.addParticle((IParticleData) LOTRParticles.WHITE_SMOKE.get(), x, y, z, 0.0D, 0.0D, 0.0D);
		}

	}

	@Override
	public void entityInside(BlockState state, World world, BlockPos pos, Entity entity) {
		super.entityInside(state, world, pos, entity);
		if (entity instanceof LivingEntity && !world.isClientSide) {
			LivingEntity living = (LivingEntity) entity;
			if (isEntityVulnerable(living)) {
				living.addEffect(new EffectInstance(Effects.POISON, LOTRUtil.secondsToTicks(5)));
				living.addEffect(new EffectInstance(Effects.BLINDNESS, LOTRUtil.secondsToTicks(10)));
			}
		}

	}

	private boolean isEntityVulnerable(LivingEntity entity) {
		if (!(entity instanceof PlayerEntity)) {
			return true;
		}
		PlayerEntity player = (PlayerEntity) entity;
		if (player.abilities.instabuild) {
			return false;
		}
		float alignment = LOTRLevelData.sidedInstance(player).getData(player).getAlignmentData().getAlignment(FactionPointers.MORDOR);
		float max = 250.0F;
		if (alignment >= max) {
			return false;
		}
		if (alignment > 0.0F) {
			float f = alignment / max;
			return entity.getRandom().nextFloat() < 1.0F - f;
		}
		return true;
	}

	@Override
	protected boolean mayPlaceOn(BlockState state, IBlockReader world, BlockPos pos) {
		return super.mayPlaceOn(state, world, pos) || state.is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES);
	}

	@Override
	public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
		super.randomTick(state, world, pos, random);
		world.getBiome(pos);
		double range = 12.0D;
		AxisAlignedBB aabb = new AxisAlignedBB(pos).inflate(range);
		List entities = world.getEntitiesOfClass(LivingEntity.class, aabb, this::isEntityVulnerable);
		entities.forEach(e -> {
			((LivingEntity) e).addEffect(new EffectInstance(Effects.CONFUSION, LOTRUtil.secondsToTicks(10)));
		});
	}
}
