package lotr.common.event;

import java.util.Optional;

import lotr.common.LOTRGameRules;
import lotr.common.init.LOTRDimensions;
import lotr.common.util.LOTRUtil;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class MiddleEarthRespawning {
	public static BlockPos getCheckedBedRespawnPosition(BlockPos bedRespawnPosition, ServerPlayerEntity player) {
		ServerWorld bedRespawnWorld = player.getServer().getLevel(player.getRespawnDimension());
		return isLOTRDimension(player) && bedRespawnPosition != null && bedRespawnWorld != null && !LOTRDimensions.isModDimension(bedRespawnWorld) ? null : bedRespawnPosition;
	}

	public static ServerWorld getDefaultRespawnWorld(ServerWorld defaultRespawnWorld, ServerPlayerEntity player) {
		return isLOTRDimension(player) && LOTRDimensions.isDimension(defaultRespawnWorld, World.OVERWORLD) ? player.getLevel() : defaultRespawnWorld;
	}

	private static boolean isGameruleEnabled(ServerWorld world) {
		return world.getGameRules().getBoolean(LOTRGameRules.MIDDLE_EARTH_RESPAWNING);
	}

	private static boolean isLOTRDimension(ServerPlayerEntity player) {
		return LOTRDimensions.isModDimension(player.getLevel());
	}

	private static void raisePlayerIfObstructed(ServerPlayerEntity newPlayer, ServerWorld world) {
		while (!world.noCollision(newPlayer) && newPlayer.getY() < world.getMaxBuildHeight()) {
			newPlayer.setPos(newPlayer.getX(), newPlayer.getY() + 1.0D, newPlayer.getZ());
		}

	}

	public static void relocatePlayerIfNeeded(Optional optBedRespawnPosition, ServerPlayerEntity newPlayer, ServerPlayerEntity deadPlayer) {
		if (isLOTRDimension(deadPlayer)) {
			ServerWorld world = deadPlayer.getLevel();
			Vector3d deathPoint = deadPlayer.position();
			if (optBedRespawnPosition.isPresent()) {
				Vector3d bedRespawnPos = (Vector3d) optBedRespawnPosition.get();
				if (!deathPoint.closerThan(bedRespawnPos, 5000.0D) && isGameruleEnabled(world)) {
					relocateRandomlyForDistantRespawn(newPlayer, deadPlayer, true);
				}
			} else {
				BlockPos worldSpawn = LOTRDimensions.getDimensionSpawnPoint(world);
				if (!deathPoint.closerThan(Vector3d.atCenterOf(worldSpawn), 2000.0D) && isGameruleEnabled(world)) {
					relocateRandomlyForDistantRespawn(newPlayer, deadPlayer, false);
				} else {
					int x = worldSpawn.getX();
					int z = worldSpawn.getZ();
					int y = LOTRUtil.forceLoadChunkAndGetTopBlock(world, x, z);
					newPlayer.moveTo(x + 0.5D, y, z + 0.5D, newPlayer.yRot, newPlayer.xRot);
					raisePlayerIfObstructed(newPlayer, world);
				}
			}
		}

	}

	private static void relocateRandomlyForDistantRespawn(ServerPlayerEntity newPlayer, ServerPlayerEntity deadPlayer, boolean hasBed) {
		if (isLOTRDimension(deadPlayer)) {
			ServerWorld world = newPlayer.getLevel();
			BlockPos deathPos = deadPlayer.blockPosition();
			double randomDistance = MathHelper.nextInt(world.random, 500, 1500);
			float angle = world.random.nextFloat() * 3.1415927F * 2.0F;
			int x = deathPos.getX() + (int) (randomDistance * MathHelper.sin(angle));
			int z = deathPos.getZ() + (int) (randomDistance * MathHelper.cos(angle));
			int y = LOTRUtil.forceLoadChunkAndGetTopBlock(world, x, z);
			newPlayer.moveTo(x + 0.5D, y, z + 0.5D, newPlayer.yRot, newPlayer.xRot);
			raisePlayerIfObstructed(newPlayer, world);
			if (hasBed) {
				LOTRUtil.sendMessage(newPlayer, new TranslationTextComponent("chat.lotr.respawn.farFromBed"));
			} else {
				LOTRUtil.sendMessage(newPlayer, new TranslationTextComponent("chat.lotr.respawn.farFromWorldSpawn"));
			}
		}

	}
}
