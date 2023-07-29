package lotr.common.event;

import java.util.*;
import java.util.Map.Entry;

import lotr.common.config.LOTRConfig;
import lotr.common.data.LOTRLevelData;
import lotr.common.entity.item.RingPortalEntity;
import lotr.common.init.LOTRDimensions;
import lotr.common.time.*;
import lotr.common.world.RingPortalTeleporter;
import lotr.common.world.spawning.RenewedNPCSpawner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent.*;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

public class LOTRTickHandlerServer {
	public static final int RING_PORTAL_PLAYER_TIME = 100;
	private Map ringPortalTransfers = new HashMap();
	private Map ringPortalPlayerTicks = new HashMap();

	public LOTRTickHandlerServer() {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onPlayerTick(PlayerTickEvent event) {
		if (event.side == LogicalSide.SERVER) {
			ServerPlayerEntity player = (ServerPlayerEntity) event.player;
			ServerWorld world = player.getLevel();
			Phase phase = event.phase;
			if (phase == Phase.END) {
				LOTRLevelData.serverInstance().getData(player).onUpdate(player, world);
			}
		}

	}

	@SubscribeEvent
	public void onWorldTick(WorldTickEvent event) {
		if (event.side == LogicalSide.SERVER) {
			ServerWorld world = (ServerWorld) event.world;
			Phase phase = event.phase;
			LOTRLevelData levelData = LOTRLevelData.serverInstance();
			if (phase == Phase.START && LOTRDimensions.isDimension(world, World.OVERWORLD)) {
				if (levelData.needsLoad()) {
					levelData.load(world);
				}

				if (LOTRTime.needsLoad()) {
					LOTRTime.load(world);
				}
			}

			if (phase == Phase.END) {
				if (LOTRDimensions.isDimension(world, World.OVERWORLD)) {
					if (levelData.anyDataNeedsSave()) {
						levelData.save(world);
					}

					if (world.getGameTime() % 600L == 0L) {
						levelData.save(world);
					}

					if (world.getGameTime() % (long) (Integer) LOTRConfig.SERVER.playerDataClearingInterval.get() == 0L) {
						levelData.saveAndClearUnusedPlayerData(world);
					}
				} else if (LOTRDimensions.isDimension(world, LOTRDimensions.MIDDLE_EARTH_WORLD_KEY)) {
					LOTRTime.updateTime(world);
					LOTRDate.updateDate(world);
					RenewedNPCSpawner.getForWorld(world).runSpawning(world);
					if (world.getGameTime() % 20L == 0L) {
						for (PlayerEntity player : world.players()) {
							levelData.sendPlayerLocationsToPlayer(player, world);
						}
					}
				}

				Set removes = new HashSet();
				Iterator var14 = ringPortalTransfers.entrySet().iterator();

				while (true) {
					while (var14.hasNext()) {
						Entry entry = (Entry) var14.next();
						Entity e = (Entity) entry.getKey();
						RingPortalEntity portal = (RingPortalEntity) entry.getValue();
						if (e.isAlive() && e.level != null) {
							if (e.level == world) {
								boolean inPortal = checkInRingPortal(e);
								if (e instanceof PlayerEntity) {
									PlayerEntity player = (PlayerEntity) e;
									if (inPortal) {
										int i = (Integer) ringPortalPlayerTicks.getOrDefault(player, 0);
										++i;
										ringPortalPlayerTicks.put(player, i);
										if (i >= 100) {
											RingPortalTeleporter.transferEntity(world, player, Optional.of(portal), true);
											ringPortalPlayerTicks.remove(player);
											removes.add(player);
										}
									} else {
										ringPortalPlayerTicks.remove(player);
									}
								} else {
									if (inPortal) {
										RingPortalTeleporter.transferEntity(world, e, Optional.of(portal), true);
									}

									removes.add(e);
								}
							}
						} else {
							removes.add(e);
						}
					}

					Map var10001 = ringPortalTransfers;
					removes.forEach(var10001::remove);
					break;
				}
			}
		}

	}

	@SubscribeEvent
	public void onWorldUnload(Unload event) {
		IWorld world = event.getWorld();
		if (world instanceof ServerWorld) {
			ServerWorld sWorld = (ServerWorld) world;
			boolean isCompleteGameUnload = LOTRDimensions.isDimension(sWorld, World.OVERWORLD);
			if (isCompleteGameUnload) {
				ringPortalTransfers.clear();
				ringPortalPlayerTicks.clear();
			}
		}

	}

	public void prepareRingPortal(Entity entity, RingPortalEntity portal) {
		if (!ringPortalTransfers.containsKey(entity)) {
			ringPortalTransfers.put(entity, portal);
		}

	}

	public static boolean checkInRingPortal(Entity entity) {
		if (entity instanceof RingPortalEntity || entity.isPassenger()) {
			return false;
		}
		double searchRange = 8.0D;
		List portals = entity.level.getEntitiesOfClass(RingPortalEntity.class, entity.getBoundingBox().expandTowards(searchRange, searchRange, searchRange));
		Iterator var5 = portals.iterator();

		RingPortalEntity portal;
		do {
			if (!var5.hasNext()) {
				return false;
			}

			portal = (RingPortalEntity) var5.next();
		} while (!portal.getBoundingBox().intersects(entity.getBoundingBox()));

		return true;
	}
}
