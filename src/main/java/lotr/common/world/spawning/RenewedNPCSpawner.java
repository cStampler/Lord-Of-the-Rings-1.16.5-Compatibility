package lotr.common.world.spawning;

import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Streams;

import lotr.common.LOTRLog;
import lotr.common.entity.npc.NPCEntity;
import lotr.common.init.LOTRDimensions;
import net.minecraft.block.BlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.EntitySpawnPlacementRegistry.PlacementType;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.*;
import net.minecraft.world.spawner.*;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.event.ForgeEventFactory;

public class RenewedNPCSpawner {
	private static final Map PER_WORLD_SPAWNERS = new HashMap();
	private long lastCycleTime;
	private final List spawnableChunks = new ArrayList();

	private RenewedNPCSpawner() {
	}

	private List gatherSpawnableChunks(ServerWorld world) {
		spawnableChunks.clear();
		ServerChunkProvider scp = world.getChunkSource();
		for (ServerPlayerEntity player : world.players()) {
			int chunkX = MathHelper.floor(player.getX() / 16.0D);
			int chunkZ = MathHelper.floor(player.getZ() / 16.0D);

			for (int dx = -7; dx <= 7; ++dx) {
				for (int dz = -7; dz <= 7; ++dz) {
					ChunkPos chunkPos = new ChunkPos(chunkX + dx, chunkZ + dz);
					if (!spawnableChunks.contains(chunkPos) && scp.isEntityTickingChunk(chunkPos)) {
						spawnableChunks.add(chunkPos);
					}
				}
			}
		}

		return spawnableChunks;
	}

	private double getNPCDensityCapForChunk(ServerWorld world, ChunkPos chunkPos) {
		Random rand = world.random;
		int x = MathHelper.nextInt(rand, chunkPos.getMinBlockX(), chunkPos.getMaxBlockX());
		int z = MathHelper.nextInt(rand, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ());
		int y = world.getSeaLevel();
		return getNPCDensityForBiome(world, world.getBiome(new BlockPos(x, y, z)));
	}

	private double getNPCDensityForBiome(ServerWorld world, Biome biome) {
		return NPCSpawnSettingsManager.getSpawnsForBiome(biome, world).getNPCDensity();
	}

	private boolean includeInNPCCount(Entity e) {
		return e.isAlive() && e instanceof NPCEntity;
	}

	private double performSpawningInChunk(ServerWorld world, ChunkPos chunkPos, Random rand) {
		double addedSpawnCountWeight = 0.0D;
		BlockPos pos = getRandomSpawnPositionInChunk(world, chunkPos, rand);
		if (pos.getY() >= 1) {
			int y = pos.getY();
			BlockState state = world.getBlockState(pos);
			if (!state.isRedstoneConductor(world, pos)) {
				Mutable movingPos = new Mutable();
				int groups = 3;

				for (int l = 0; l < groups; ++l) {
					int x = pos.getX();
					int z = pos.getZ();
					NPCSpawnEntry.EntryInContext spawnEntryInstance = getRandomSpawnListEntry(world, pos);
					if (spawnEntryInstance != null) {
						ILivingEntityData entityGroupData = null;
						spawnEntryInstance.isConquestSpawn();
						int groupSize = spawnEntryInstance.getRandomGroupSize(rand);
						int spawnedInGroup = 0;
						int attempts = groupSize * 8;

						for (int a = 0; a < attempts; ++a) {
							x += rand.nextInt(6) - rand.nextInt(6);
							z += rand.nextInt(6) - rand.nextInt(6);
							y += rand.nextInt(1) - rand.nextInt(1);
							movingPos.set(x, y, z);
							double xd = x + 0.5D;
							double zd = z + 0.5D;
							PlayerEntity closestPlayer = world.getNearestPlayer(xd, y, zd, -1.0D, false);
							if (closestPlayer != null) {
								double distSqToPlayer = closestPlayer.distanceToSqr(xd, y, zd);
								if (isSuitableSpawnLocation(world, chunkPos, movingPos, distSqToPlayer)) {
									EntityType typeToSpawn = spawnEntryInstance.getTypeToSpawn(rand);
									if (canNPCSpawnAtLocation(world, typeToSpawn, movingPos, distSqToPlayer)) {
										NPCEntity entity = tryCreateNPC(world, typeToSpawn);
										if (entity != null) {
											entity.moveTo(xd, y, zd, rand.nextFloat() * 360.0F, 0.0F);
											int canSpawn = ForgeHooks.canEntitySpawn(entity, world, xd, y, zd, (AbstractSpawner) null, SpawnReason.NATURAL);
											if (canSpawn != -1 && (canSpawn == 1 || canNPCSpawnNormally(world, entity, distSqToPlayer))) {
												if (!ForgeEventFactory.doSpecialSpawn(entity, world, (float) xd, y, (float) zd, (AbstractSpawner) null, SpawnReason.NATURAL)) {
													entityGroupData = entity.finalizeSpawn(world, world.getCurrentDifficultyAt(entity.blockPosition()), SpawnReason.NATURAL, entityGroupData, (CompoundNBT) null);
												}

												world.addFreshEntityWithPassengers(entity);
												addedSpawnCountWeight += entity.getSpawnCountWeight();
												++spawnedInGroup;
												if (spawnedInGroup >= groupSize || spawnedInGroup >= ForgeEventFactory.getMaxSpawnPackSize(entity) || entity.isMaxGroupSizeReached(spawnedInGroup)) {
													break;
												}
											}
										}
									}
								}
							}
						}
					}
				}
			}
		}

		return addedSpawnCountWeight;
	}

	private void runSpawnCycle(ServerWorld world) {
		List chunks = gatherSpawnableChunks(world);
		double sumDensity = (Double) chunks.stream().collect(Collectors.summingDouble(chunk -> getNPCDensityCapForChunk(world, (ChunkPos) chunk)));
		int cap = (int) (sumDensity * 0.51D);
		double count = Streams.stream(world.getAllEntities()).filter(this::includeInNPCCount).map(e -> ((NPCEntity) e)).collect(Collectors.summingDouble(NPCEntity::getSpawnCountWeight));
		if (count < cap) {
			Collections.shuffle(chunks);
			Random rand = world.random;
			Iterator var9 = chunks.iterator();

			while (var9.hasNext()) {
				ChunkPos chunkPos = (ChunkPos) var9.next();
				count += performSpawningInChunk(world, chunkPos, rand);
				if (count >= cap) {
					break;
				}
			}
		}

	}

	public void runSpawning(ServerWorld world) {
		if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
			world.getProfiler().push("lotrNpcSpawning");
			long worldTime = world.getGameTime();
			lastCycleTime = Math.min(lastCycleTime, worldTime);
			if (worldTime - lastCycleTime >= 20L) {
				runSpawnCycle(world);
				lastCycleTime = worldTime;
			}

			world.getProfiler().pop();
		}
	}

	private static boolean canNPCSpawnAtLocation(ServerWorld world, EntityType type, Mutable pos, double distSqToPlayer) {
		double despawnDist = type.getCategory().getDespawnDistance();
		if (!type.canSpawnFarFromPlayer() && distSqToPlayer > despawnDist * despawnDist || !type.canSummon()) {
			return false;
		}
		PlacementType placementType = EntitySpawnPlacementRegistry.getPlacementType(type);
		if (!WorldEntitySpawner.isSpawnPositionOk(placementType, world, pos, type)) {
			return false;
		}
		return !EntitySpawnPlacementRegistry.checkSpawnRules(type, world, SpawnReason.NATURAL, pos, world.random) ? false : world.noCollision(type.getAABB(pos.getX() + 0.5D, pos.getY(), pos.getZ() + 0.5D));
	}

	private static boolean canNPCSpawnNormally(ServerWorld world, NPCEntity entity, double distSqToPlayer) {
		double despawnDist = entity.getType().getCategory().getDespawnDistance();
		if (distSqToPlayer > despawnDist * despawnDist && entity.removeWhenFarAway(distSqToPlayer)) {
			return false;
		}
		return entity.checkSpawnRules(world, SpawnReason.NATURAL) && entity.checkSpawnObstruction(world);
	}

	public static RenewedNPCSpawner getForWorld(ServerWorld world) {
		return (RenewedNPCSpawner) PER_WORLD_SPAWNERS.computeIfAbsent(world.dimension(), hummel -> new RenewedNPCSpawner());
	}

	private static NPCSpawnEntry.EntryInContext getRandomSpawnListEntry(World world, BlockPos pos) {
		Random rand = world.random;
		Biome biome = world.getBiome(pos);
		BiomeNPCSpawnList spawnList = NPCSpawnSettingsManager.getSpawnsForBiome(biome, world);
		return spawnList.getRandomSpawnEntry(rand, world, pos);
	}

	private static BlockPos getRandomSpawnPositionInChunk(ServerWorld world, ChunkPos chunkPos, Random rand) {
		int x = MathHelper.nextInt(rand, chunkPos.getMinBlockX(), chunkPos.getMaxBlockX());
		int z = MathHelper.nextInt(rand, chunkPos.getMinBlockZ(), chunkPos.getMaxBlockZ());
		int topY = world.getHeight(Type.WORLD_SURFACE, x, z) + 1;
		int y = rand.nextInt(topY + 1);
		return new BlockPos(x, y, z);
	}

	private static boolean isSuitableSpawnLocation(ServerWorld world, ChunkPos mainChunkPos, Mutable pos, double distSqToPlayer) {
		if (distSqToPlayer <= 576.0D || LOTRDimensions.getDimensionSpawnPoint(world).closerThan(Vector3d.atBottomCenterOf(pos), 24.0D)) {
			return false;
		}
		ChunkPos chunkPosAtBlock = new ChunkPos(pos);
		return Objects.equals(chunkPosAtBlock, mainChunkPos) || world.getChunkSource().isEntityTickingChunk(chunkPosAtBlock);
	}

	private static NPCEntity tryCreateNPC(ServerWorld world, EntityType type) {
		try {
			Entity entity = type.create(world);
			if (!(entity instanceof NPCEntity)) {
				throw new IllegalStateException("LOTR mob spawner trying to spawn a non-NPC: " + Registry.ENTITY_TYPE.getKey(type));
			}
			return (NPCEntity) entity;
		} catch (Exception var3) {
			var3.printStackTrace();
			LOTRLog.warn("Failed to create spawned NPC", var3);
			return null;
		}
	}
}
