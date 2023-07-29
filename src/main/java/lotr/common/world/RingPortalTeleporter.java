package lotr.common.world;

import java.util.Optional;
import java.util.function.Function;

import lotr.common.data.LOTRLevelData;
import lotr.common.dim.MiddleEarthDimensionType;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.init.LOTRDimensions;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.util.ITeleporter;

public class RingPortalTeleporter implements ITeleporter {
	private RegistryKey targetDim;
	private boolean makeRingPortalIfNotMade;

	public RingPortalTeleporter(RegistryKey dim, boolean make) {
		targetDim = dim;
		makeRingPortalIfNotMade = make;
	}

	@Override
	public Entity placeEntity(Entity entity, ServerWorld currentWorld, ServerWorld destWorld, float yaw, Function repositionEntity) {
		LOTRLevelData levelData = LOTRLevelData.serverInstance();
		BlockPos target;
		if (targetDim.equals(LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
			MiddleEarthDimensionType meDim = (MiddleEarthDimensionType) destWorld.getServer().getLevel(targetDim).dimensionType();
			if (levelData.madeMiddleEarthPortal()) {
				target = meDim.getSpawnCoordinate(destWorld);
			} else {
				target = meDim.getDefaultPortalCoordinate(destWorld);
			}
		} else {
			target = levelData.getOverworldPortalLocation();
		}

		while (destWorld.getBlockState(target).canOcclude()) {
			target = target.above();
		}

		while (!destWorld.getBlockState(target.below()).canOcclude()) {
			target = target.below();
		}

		Vector3d prevMotion = entity.getDeltaMovement();
		if (entity instanceof ServerPlayerEntity) {
			ServerPlayerEntity player = (ServerPlayerEntity) entity;
			player.moveTo(target.getX() + 0.5D, target.getY(), target.getZ() + 0.5D, player.yRot, 0.0F);
			player.setLevel(destWorld);
			destWorld.addDuringPortalTeleport(player);
			RegistryKey prevDimKey = currentWorld.dimension();
			RegistryKey newDimKey = player.level.dimension();
			CriteriaTriggers.CHANGED_DIMENSION.trigger(player, prevDimKey, newDimKey);
			player.connection.teleport(player.getX(), player.getY(), player.getZ(), player.yRot, player.xRot);
		} else {
			Entity entityNew = entity.getType().create(destWorld);
			if (entityNew != null) {
				entityNew.restoreFrom(entity);
				entityNew.moveTo(target, entityNew.yRot, 0.0F);
				entityNew.setDeltaMovement(prevMotion);
				destWorld.addFromAnotherDimension(entityNew);
			}

			entity = entityNew;
		}

		if (entity != null) {
			entity.setPortalCooldown();
		}

		if (targetDim.equals(LOTRDimensions.MIDDLE_EARTH_WORLD_KEY) && !levelData.madeMiddleEarthPortal()) {
			if (makeRingPortalIfNotMade) {
				RingPortalEntity portal = new RingPortalEntity(destWorld);
				portal.moveTo(target.getX() + 0.5D, target.getY() + 3.5D, target.getZ() + 0.5D, 0.0F, 0.0F);
				destWorld.addFreshEntity(portal);
			}

			levelData.setMadeMiddleEarthPortal(true);
			levelData.markMiddleEarthPortalLocation(destWorld, target);
		}

		return entity;
	}

	public static void transferEntity(ServerWorld world, Entity entity, Optional portal, boolean makePortal) {
		portal.ifPresent(hummel -> ((RingPortalEntity) hummel).onTransferEntity());
		RegistryKey targetDim = LOTRDimensions.MIDDLE_EARTH_WORLD_KEY;
		if (LOTRDimensions.isDimension(world, targetDim)) {
			targetDim = World.OVERWORLD;
		}

		ServerWorld targetWorld = world.getServer().getLevel(targetDim);
		entity.changeDimension(targetWorld, new RingPortalTeleporter(targetDim, makePortal));
	}
}
