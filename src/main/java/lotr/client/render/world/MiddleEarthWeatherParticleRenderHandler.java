package lotr.client.render.world;

import java.util.Random;

import lotr.common.init.*;
import lotr.common.world.biome.*;
import net.minecraft.block.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.fluid.FluidState;
import net.minecraft.particles.*;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.*;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraftforge.client.IWeatherParticleRenderHandler;

public class MiddleEarthWeatherParticleRenderHandler implements IWeatherParticleRenderHandler {
	private int rainSoundTime;

	@Override
	public void render(int ticks, ClientWorld world, Minecraft mc, ActiveRenderInfo activeRenderInfo) {
		float rainStrength = world.getRainLevel(1.0F) / (Minecraft.useFancyGraphics() ? 1.0F : 2.0F);
		if (rainStrength > 0.0F) {
			Random rand = new Random(ticks * 312987231L);
			BlockPos pos = new BlockPos(activeRenderInfo.getPosition());
			BlockPos soundPos = null;
			int numParticles = (int) (100.0F * rainStrength * rainStrength) / (mc.options.particles == ParticleStatus.DECREASED ? 2 : 1);

			for (int i = 0; i < numParticles; ++i) {
				int x = MathHelper.nextInt(rand, -10, 10);
				int z = MathHelper.nextInt(rand, -10, 10);
				BlockPos surfacePos = world.getHeightmapPos(Type.MOTION_BLOCKING, pos.offset(x, 0, z)).below();
				Biome biome = world.getBiome(surfacePos);
				LOTRBiomeWrapper biomeWrapper = LOTRBiomes.getWrapperFor(biome, world);
				if (surfacePos.getY() > 0 && surfacePos.getY() <= pos.getY() + 10 && surfacePos.getY() >= pos.getY() - 10 && biomeWrapper.getPrecipitationVisually() == RainType.RAIN && !LOTRBiomeBase.isSnowingVisually(biomeWrapper, world, surfacePos)) {
					soundPos = surfacePos;
					if (mc.options.particles == ParticleStatus.MINIMAL) {
						break;
					}

					double dx = rand.nextDouble();
					double dz = rand.nextDouble();
					BlockState blockstate = world.getBlockState(surfacePos);
					FluidState fluidstate = world.getFluidState(surfacePos);
					VoxelShape voxelshape = blockstate.getCollisionShape(world, surfacePos);
					double collisionShapeTop = voxelshape.max(Axis.Y, dx, dz);
					double topHeight = fluidstate.getHeight(world, surfacePos);
					double dy = Math.max(collisionShapeTop, topHeight);
					IParticleData particle = !fluidstate.is(FluidTags.LAVA) && !blockstate.is(Blocks.MAGMA_BLOCK) && !CampfireBlock.isLitCampfire(blockstate) ? (IParticleData) LOTRParticles.RAIN.get() : ParticleTypes.SMOKE;
					world.addParticle(particle, surfacePos.getX() + dx, surfacePos.getY() + dy, surfacePos.getZ() + dz, 0.0D, 0.0D, 0.0D);
				}
			}

			if (soundPos != null && rand.nextInt(3) < rainSoundTime++) {
				rainSoundTime = 0;
				if (soundPos.getY() > pos.getY() + 1 && world.getHeightmapPos(Type.MOTION_BLOCKING, pos).getY() > MathHelper.floor(pos.getY())) {
					mc.level.playLocalSound(soundPos, SoundEvents.WEATHER_RAIN_ABOVE, SoundCategory.WEATHER, 0.1F, 0.5F, false);
				} else {
					mc.level.playLocalSound(soundPos, LOTRSoundEvents.NEW_RAIN, SoundCategory.WEATHER, 0.2F, 1.0F, false);
				}
			}
		}

	}
}
