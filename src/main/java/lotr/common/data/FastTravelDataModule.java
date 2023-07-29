package lotr.common.data;

import java.util.*;

import org.apache.commons.lang3.tuple.Pair;

import lotr.common.*;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.network.*;
import lotr.common.stat.LOTRStats;
import lotr.common.util.*;
import lotr.common.world.map.*;
import net.minecraft.entity.*;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.player.*;
import net.minecraft.nbt.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.*;

public class FastTravelDataModule extends PlayerDataModule {
	private Set unlockedWaypointRegions = new HashSet();
	private Map waypointUseCounts = new HashMap();
	private int nextCustomWaypointId = 20000;
	private LookupList customWaypoints = new LookupList(hummel -> ((CustomWaypoint) hummel).getCustomId());
	private Map customWaypointUseCounts = new HashMap();
	private LookupList adoptedCustomWaypoints = new LookupList(hummel -> ((AdoptedCustomWaypoint) hummel).getAdoptedKey());
	private Map adoptedCustomWaypointUseCounts = new HashMap();
	private boolean showMapWaypoints = true;
	private boolean showCustomWaypoints = true;
	private int ftSinceTick = getLevelData().getWaypointCooldownMax() * 20;
	private long lastOnlineTime = -1L;
	private Waypoint targetFTWaypoint;
	private int ticksUntilFT;
	private double lastPosX;
	private double lastPosY;
	private double lastPosZ;
	private UUID uuidToMount;
	private int uuidToMountTime;

	protected FastTravelDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	public void addAdoptedCustomWaypointFromServer(AdoptedCustomWaypoint waypoint) {
		adoptedCustomWaypoints.add(waypoint);
		markDirty();
	}

	public void addCreatedCustomWaypointFromServer(CustomWaypoint waypoint) {
		customWaypoints.add(waypoint);
		markDirty();
	}

	public boolean adoptCustomWaypoint(ServerWorld world, CustomWaypoint originalWaypoint) {
		if (checkCustomWaypointBelongsToPlayer(originalWaypoint)) {
			playerData.logPlayerError("Tried to adopt a custom waypoint (%s) that is their own waypoint", originalWaypoint.getDisplayName());
			return false;
		}
		AdoptedCustomWaypoint waypoint = AdoptedCustomWaypoint.adopt(currentMapSettings(), originalWaypoint);
		adoptedCustomWaypoints.add(waypoint);
		markDirty();
		originalWaypoint.onAdoptedBy(getPlayerUUID(), world);
		sendPacketToClient(new SPacketAdoptCustomWaypoint(waypoint));
		return true;
	}

	public boolean canCreateOrAdoptMoreCustomWaypoints() {
		return getNumCustomWaypoints() < getMaxCustomWaypoints();
	}

	private boolean checkCustomWaypointBelongsToPlayer(CustomWaypoint waypoint) {
		return waypoint.getCreatedPlayer().equals(getPlayerUUID());
	}

	public CustomWaypoint createNewCustomWaypoint(String name, String lore, boolean isPublic, BlockPos pos) {
		int cwpId = nextCustomWaypointId++;
		CustomWaypoint waypoint = new CustomWaypoint(currentMapSettings(), getPlayerUUID(), cwpId, name, lore, pos, isPublic);
		customWaypoints.add(waypoint);
		markDirty();
		sendPacketToClient(new SPacketCreateCustomWaypoint(waypoint));
		return waypoint;
	}

	private MobEntity fastTravelEntity(ServerWorld world, MobEntity entity, double x, double y, double z) {
		entity.moveTo(x, y, z, entity.yRot, entity.xRot);
		entity.fallDistance = 0.0F;
		entity.getNavigation().stop();
		entity.setTarget((LivingEntity) null);
		ServerChunkProvider scp = world.getChunkSource();
		scp.removeEntity(entity);
		scp.addEntity(entity);
		return entity;
	}

	private void fastTravelTo(ServerPlayerEntity player, Waypoint waypoint) {
		ServerWorld world = player.getLevel();
		BlockPos travelPos = waypoint.getTravelPosition(world, player);
		if (travelPos == null) {
			LOTRLog.warn("Player %s fast travel to %s was cancelled because the waypoint returned a null travel position.", UsernameHelper.getRawUsername(player), waypoint.getRawName());
			return;
		}
		double startXF = player.getX();
		double startYF = player.getY();
		double startZF = player.getZ();
		int startX = MathHelper.floor(startXF);
		int startZ = MathHelper.floor(startZF);
		List<Entity> entities = world.getEntitiesOfClass(MobEntity.class, player.getBoundingBox().inflate(256.0));
		HashSet<Entity> entitiesToTransport = new HashSet<>();
		for (Entity entity : entities) {
			TameableEntity pet;
			if (entity instanceof TameableEntity && (pet = (TameableEntity) entity).getOwner() == player && !pet.isOrderedToSit()) {
				entitiesToTransport.add(pet);
				continue;
			}
			if (!((MobEntity) entity).isLeashed() || ((MobEntity) entity).getLeashHolder() != player) {
				continue;
			}
			entitiesToTransport.add(entity);
		}
		HashSet<Entity> transportExclusions = new HashSet<>();
		for (Entity entity : entitiesToTransport) {
			for (Entity rider : entity.getPassengers()) {
				if (!entitiesToTransport.contains(rider)) {
					continue;
				}
				transportExclusions.add(rider);
			}
		}
		entitiesToTransport.removeAll(transportExclusions);
		Entity playerMount = player.getVehicle();
		player.stopRiding();
		player.teleportTo(travelPos.getX() + 0.5D, travelPos.getY(), travelPos.getZ() + 0.5D);
		player.fallDistance = 0.0F;
		if (playerMount instanceof MobEntity) {
			playerMount = fastTravelEntity(world, (MobEntity) playerMount, travelPos.getX() + 0.5D, (double) travelPos.getY(), travelPos.getZ() + 0.5D);
		}
		if (playerMount != null) {
			setUUIDToMount(playerMount.getUUID());
		}
		for (Entity entit : entitiesToTransport) {
			MobEntity entity = (MobEntity) entit;
			Entity mount = entity.getVehicle();
			entity.stopRiding();
			entity = fastTravelEntity(world, entity, travelPos.getX() + 0.5, travelPos.getY(), travelPos.getZ() + 0.5);
			if (!(mount instanceof MobEntity)) {
				continue;
			}
			mount = fastTravelEntity(world, (MobEntity) mount, travelPos.getX() + 0.5, travelPos.getY(), travelPos.getZ() + 0.5);
			entity.startRiding(mount);
		}
		sendFTScreenPacket(player, waypoint, startX, startZ);
		setTimeSinceFTWithUpdate(0);
		incrementWPUseCount(waypoint);
		player.awardStat(LOTRStats.FAST_TRAVEL);
		double dx = player.getX() - startXF;
		double dy = player.getY() - startYF;
		double dz = player.getZ() - startZF;
		int distanceInM = Math.round(MathHelper.sqrt(dx * dx + dy * dy + dz * dz));
		if (distanceInM > 0) {
			player.awardStat(LOTRStats.FAST_TRAVEL_ONE_M, distanceInM);
		}
	}

	private CustomWaypoint findOriginalWaypoint(ServerWorld world, AdoptedCustomWaypoint waypoint) {
		FastTravelDataModule creatorFtData = getLevelData().getData(world, waypoint.getCreatedPlayer()).getFastTravelData();
		return creatorFtData.getCustomWaypointById(waypoint.getCustomId());
	}

	public AdoptedCustomWaypoint getAdoptedCustomWaypointByKey(AdoptedCustomWaypointKey key) {
		return (AdoptedCustomWaypoint) adoptedCustomWaypoints.lookup(key);
	}

	public List getAdoptedCustomWaypoints() {
		return adoptedCustomWaypoints;
	}

	private long getCurrentOnlineTime(ServerWorld serverWorld) {
		return serverWorld.getServer().getLevel(World.OVERWORLD).getGameTime();
	}

	public CustomWaypoint getCustomWaypointById(int customId) {
		return (CustomWaypoint) customWaypoints.lookup(customId);
	}

	public List getCustomWaypoints() {
		return customWaypoints;
	}

	public int getMaxCustomWaypoints() {
		return 20;
	}

	public int getNumCustomWaypoints() {
		return customWaypoints.size() + adoptedCustomWaypoints.size();
	}

	public boolean getShowCustomWaypoints() {
		return showCustomWaypoints;
	}

	public boolean getShowMapWaypoints() {
		return showMapWaypoints;
	}

	public int getTicksUntilFT() {
		return ticksUntilFT;
	}

	public int getTimeSinceFT() {
		return ftSinceTick;
	}

	public int getWaypointFTTime(Waypoint waypoint, PlayerEntity player) {
		int baseMin = getLevelData().getWaypointCooldownMin();
		int baseMax = getLevelData().getWaypointCooldownMax();
		int useCount = getWPUseCount(waypoint);
		double dist = waypoint.getDistanceFromPlayer(player);
		double time = baseMin;
		double added = (baseMax - baseMin) * Math.pow(0.9D, useCount);
		time += added;
		time *= Math.max(1.0D, dist * 1.2E-5D);
		int seconds = (int) Math.round(time);
		seconds = Math.max(seconds, 0);
		return seconds * 20;
	}

	public int getWPUseCount(Waypoint waypoint) {
		if (waypoint instanceof MapWaypoint) {
			MapWaypoint mapWp = (MapWaypoint) waypoint;
			ResourceLocation wpName = mapWp.getName();
			if (waypointUseCounts.containsKey(wpName)) {
				return (Integer) waypointUseCounts.get(wpName);
			}
		} else if (waypoint instanceof CustomWaypoint) {
			CustomWaypoint cwp = (CustomWaypoint) waypoint;
			int cwpId = cwp.getCustomId();
			if (cwp.getCreatedPlayer().equals(getPlayerUUID()) && customWaypointUseCounts.containsKey(cwpId)) {
				return (Integer) customWaypointUseCounts.get(cwpId);
			}
		} else if (waypoint instanceof AdoptedCustomWaypoint) {
			AdoptedCustomWaypoint cwp = (AdoptedCustomWaypoint) waypoint;
			AdoptedCustomWaypointKey key = cwp.getAdoptedKey();
			if (adoptedCustomWaypointUseCounts.containsKey(key)) {
				return (Integer) adoptedCustomWaypointUseCounts.get(key);
			}
		} else {
			LOTRLog.error("Tried to fetch the use count for an unknown waypoint type %s", waypoint.getClass());
		}

		return 0;
	}

	@Override
	protected void handleLogin(ServerPlayerEntity player) {
		updateFastTravelClockFromLastOnlineTime(player);
	}

	public void incrementWPUseCount(Waypoint waypoint) {
		setWPUseCount(waypoint, getWPUseCount(waypoint) + 1);
	}

	public boolean isUnderAttack(ServerPlayerEntity player) {
		World world = player.level;
		if (player.abilities.instabuild) {
			return false;
		}
		double range = 16.0D;
		List attackingEntities = world.getLoadedEntitiesOfClass(MobEntity.class, player.getBoundingBox().inflate(range), entity -> (entity.getTarget() == player));
		return !attackingEntities.isEmpty();
	}

	public boolean isWaypointRegionUnlocked(WaypointRegion region) {
		return unlockedWaypointRegions.contains(region);
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		DataUtil.loadCollectionFromPrimitiveListNBT(unlockedWaypointRegions, playerNBT.getList("UnlockedFTRegions", 8), (h1, h2) -> ((ListNBT) h1).getString((int) h2), regionName -> {
			WaypointRegion region = currentMapSettings().getWaypointRegionByName(new ResourceLocation((String) regionName));
			if (region != null) {
				return region;
			}
			playerData.logPlayerError("Loaded nonexistent waypoint region ID %s", regionName);
			return null;
		});

		DataUtil.loadMapFromListNBT(waypointUseCounts, playerNBT.getList("WPUses", 10), nbt -> {
			ResourceLocation wpName = new ResourceLocation(((CompoundNBT) nbt).getString("WPName"));
			MapWaypoint waypoint = currentMapSettings().getWaypointByName(wpName);
			if (waypoint != null) {
				int count = ((CompoundNBT) nbt).getInt("Count");
				return Pair.of(wpName, count);
			}
			playerData.logPlayerError("Loaded nonexistent map waypoint %s", wpName.toString());
			return null;
		});
		nextCustomWaypointId = (Integer) DataUtil.getIfNBTContains(nextCustomWaypointId, playerNBT, "NextCWPID", (h1, h2) -> ((CompoundNBT) h1).getInt((String) h2));
		DataUtil.loadCollectionFromCompoundListNBT(customWaypoints, playerNBT.getList("CustomWaypoints", 10), nbt -> CustomWaypoint.load(currentMapSettings(), (CompoundNBT) nbt));
		DataUtil.loadMapFromListNBT(customWaypointUseCounts, playerNBT.getList("CWPUses", 10), nbt -> {
			int cwpId = ((CompoundNBT) nbt).getInt("CustomID");
			if (customWaypoints.hasKey(cwpId)) {
				int count = ((CompoundNBT) nbt).getInt("Count");
				return Pair.of(cwpId, count);
			}
			playerData.logPlayerError("Loaded nonexistent custom waypoint (ID %d)", cwpId);
			return null;
		});
		DataUtil.loadCollectionFromCompoundListNBT(adoptedCustomWaypoints, playerNBT.getList("AdoptedCustomWaypoints", 10), nbt -> AdoptedCustomWaypoint.load(currentMapSettings(), (CompoundNBT) nbt));
		DataUtil.loadMapFromListNBT(adoptedCustomWaypointUseCounts, playerNBT.getList("AdoptedCWPUses", 10), nbt -> {
			AdoptedCustomWaypointKey key = AdoptedCustomWaypointKey.load((CompoundNBT) nbt);
			if (adoptedCustomWaypoints.hasKey(key)) {
				int count = ((CompoundNBT) nbt).getInt("Count");
				return Pair.of(key, count);
			}
			playerData.logPlayerError("Loaded nonexistent adopted custom waypoint (creator %s, ID %d)", key.getCreatedPlayer(), key.getWaypointId());
			return null;
		});
		showMapWaypoints = (Boolean) DataUtil.getIfNBTContains(showMapWaypoints, playerNBT, "ShowWP", (h1, h2) -> ((CompoundNBT) h1).getBoolean((String) h2));
		showCustomWaypoints = (Boolean) DataUtil.getIfNBTContains(showCustomWaypoints, playerNBT, "ShowCWP", (h1, h2) -> ((CompoundNBT) h1).getBoolean((String) h2));
		ftSinceTick = (Integer) DataUtil.getIfNBTContains(ftSinceTick, playerNBT, "FTSince", (h1, h2) -> ((CompoundNBT) h1).getInt((String) h2));
		lastOnlineTime = (Long) DataUtil.getIfNBTContains(lastOnlineTime, playerNBT, "LastOnlineTime", (h1, h2) -> ((CompoundNBT) h1).getLong((String) h2));
		targetFTWaypoint = null;
		uuidToMount = (UUID) DataUtil.getIfNBTContains(uuidToMount, playerNBT, "MountUUID", (h1, h2) -> DataUtil.getUniqueIdBackCompat((CompoundNBT) h1, (String) h2));
		uuidToMountTime = playerNBT.getInt("MountUUIDTime");
	}

	public void lockWaypointRegion(WaypointRegion region) {
		if (unlockedWaypointRegions.contains(region)) {
			unlockedWaypointRegions.remove(region);
			markDirty();
			sendPacketToClient(new SPacketWaypointRegion(region, false));
		}

	}

	@Override
	protected void onUpdate(ServerPlayerEntity player, ServerWorld world, int tick) {
		Biome biome = world.getBiome(player.blockPosition());
		List waypointRegions = currentMapSettings().getWaypointRegionsForBiome(biome, world);
		waypointRegions.forEach(hummel -> unlockWaypointRegion((WaypointRegion) hummel));
		this.setTimeSinceFT(ftSinceTick + 1);
		lastOnlineTime = getCurrentOnlineTime(world);
		double curPosX = player.getX();
		double curPosY = player.getY();
		double curPosZ = player.getZ();
		double mountHasTravelledRange;
		if (targetFTWaypoint != null) {
			if (player.isSleeping()) {
				setTargetWaypoint((Waypoint) null);
				LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.inBed"));
			} else {
				mountHasTravelledRange = curPosX - lastPosX;
				double dy = curPosY - lastPosY;
				double dz = curPosZ - lastPosZ;
				double dSqToLastPos = mountHasTravelledRange * mountHasTravelledRange + dy * dy + dz * dz;
				if (dSqToLastPos > 0.001D) {
					setTargetWaypoint((Waypoint) null);
					LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.motion"));
				} else if (ticksUntilFT > 0) {
					int seconds = ticksUntilFT / 20;
					if (ticksUntilFT == 200) {
						LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.ticksStart", targetFTWaypoint.getDisplayName(), seconds));
					} else if (ticksUntilFT % 20 == 0 && seconds <= 5) {
						LOTRUtil.sendMessage(player, new TranslationTextComponent("chat.lotr.ft.ticks", seconds));
					}

					--ticksUntilFT;
					setTicksUntilFT(ticksUntilFT);
				} else {
					fastTravelTo(player, targetFTWaypoint);
					setTargetWaypoint((Waypoint) null);
				}
			}
		} else {
			setTicksUntilFT(0);
		}

		lastPosX = curPosX;
		lastPosY = curPosY;
		lastPosZ = curPosZ;
		if (uuidToMount != null) {
			if (uuidToMountTime > 0) {
				--uuidToMountTime;
			} else {
				mountHasTravelledRange = 32.0D;
				List mountMatches = world.getEntitiesOfClass(Entity.class, player.getBoundingBox().inflate(mountHasTravelledRange), entity -> uuidToMount.equals(entity.getUUID()));
				if (!mountMatches.isEmpty()) {
					Entity travelledMount = (Entity) mountMatches.get(0);
					player.startRiding(travelledMount);
				} else {
					LOTRLog.warn("Tried to remount player %s after fast travel, but couldn't find the mount nearby", UsernameHelper.getRawUsername(player));
				}

				setUUIDToMount((UUID) null);
			}
		}

	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		DataUtil.fillCollectionFromBuffer(buf, unlockedWaypointRegions, () -> {
			int regionID = buf.readVarInt();
			WaypointRegion region = currentMapSettings().getWaypointRegionByID(regionID);
			if (region != null) {
				return region;
			}
			LOTRLog.warn("Received nonexistent region ID %d from server", regionID);
			return null;
		});
		DataUtil.fillMapFromBuffer(buf, waypointUseCounts, () -> {
			int wpID = buf.readVarInt();
			int count = buf.readVarInt();
			MapWaypoint wp = currentMapSettings().getWaypointByID(wpID);
			if (wp != null) {
				return Pair.of(wp.getName(), count);
			}
			LOTRLog.warn("Received nonexistent map waypoint ID %d from server", wpID);
			return null;
		});
		DataUtil.fillCollectionFromBuffer(buf, customWaypoints, () -> CustomWaypoint.read(currentMapSettings(), buf));
		DataUtil.fillMapFromBuffer(buf, customWaypointUseCounts, () -> {
			int cwpId = buf.readVarInt();
			int count = buf.readVarInt();
			if (customWaypoints.hasKey(cwpId)) {
				return Pair.of(cwpId, count);
			}
			LOTRLog.warn("Received nonexistent custom waypoint ID %d from server", cwpId);
			return null;
		});
		DataUtil.fillCollectionFromBuffer(buf, adoptedCustomWaypoints, () -> AdoptedCustomWaypoint.read(currentMapSettings(), buf));
		DataUtil.fillMapFromBuffer(buf, adoptedCustomWaypointUseCounts, () -> {
			AdoptedCustomWaypointKey key = AdoptedCustomWaypointKey.read(buf);
			int count = buf.readVarInt();
			if (adoptedCustomWaypoints.hasKey(key)) {
				return Pair.of(key, count);
			}
			LOTRLog.warn("Received nonexistent adopted custom waypoint key (creator %s, ID %d) from server", key.getCreatedPlayer(), key.getWaypointId());
			return null;
		});
		showMapWaypoints = buf.readBoolean();
		showCustomWaypoints = buf.readBoolean();
		ftSinceTick = buf.readInt();
	}

	public void removeAdoptedCustomWaypoint(World world, AdoptedCustomWaypoint waypoint) {
		AdoptedCustomWaypointKey key = waypoint.getAdoptedKey();
		AdoptedCustomWaypoint existingMatch = (AdoptedCustomWaypoint) adoptedCustomWaypoints.lookup(key);
		adoptedCustomWaypoints.remove(existingMatch);
		adoptedCustomWaypointUseCounts.remove(key);
		markDirty();
		if (world instanceof ServerWorld) {
			ServerWorld sWorld = (ServerWorld) world;
			findOriginalWaypoint(sWorld, waypoint).onForsakenBy(getPlayerUUID(), sWorld);
		}

		sendPacketToClient(new SPacketDeleteAdoptedCustomWaypoint(existingMatch));
	}

	public void removeAdoptedCustomWaypointWhenOriginalDestroyed(World world, CustomWaypoint originalWaypoint) {
		AdoptedCustomWaypointKey key = AdoptedCustomWaypointKey.of(originalWaypoint.getCreatedPlayer(), originalWaypoint.getCustomId());
		AdoptedCustomWaypoint adoptedWaypoint = (AdoptedCustomWaypoint) adoptedCustomWaypoints.lookup(key);
		if (adoptedWaypoint != null) {
			removeAdoptedCustomWaypoint(world, adoptedWaypoint);
			executeIfPlayerExistsServerside(player -> {
				ITextComponent msg = new TranslationTextComponent("chat.lotr.cwp.adopted.destroyed", originalWaypoint.getDisplayName()).withStyle(TextFormatting.RED);
				LOTRUtil.sendMessage((PlayerEntity) player, msg);
			});
		} else {
			playerData.logPlayerError("Tried to remove an adopted custom waypoint when its original was destroyed (creator %s, ID %d) but could not find it!", key.getCreatedPlayer(), key.getWaypointId());
		}

	}

	public boolean removeCustomWaypoint(World world, CustomWaypoint waypoint) {
		if (checkCustomWaypointBelongsToPlayer(waypoint)) {
			int waypointId = waypoint.getCustomId();
			CustomWaypoint existingMatch = (CustomWaypoint) customWaypoints.lookup(waypointId);
			existingMatch.removeFromAllAdoptedPlayersWhenDestroyed(world);
			customWaypoints.remove(existingMatch);
			customWaypointUseCounts.remove(waypointId);
			markDirty();
			sendPacketToClient(new SPacketDeleteCustomWaypoint(existingMatch));
			return true;
		}
		playerData.logPlayerError("Tried to delete a custom waypoint (%s) that actually belongs to %s", waypoint.getDisplayName(), waypoint.getCreatedPlayer());
		return false;
	}

	@Override
	public void save(CompoundNBT playerNBT) {
		if (!unlockedWaypointRegions.isEmpty()) {
			playerNBT.put("UnlockedFTRegions", DataUtil.saveCollectionAsPrimitiveListNBT(unlockedWaypointRegions, region -> StringNBT.valueOf(((WaypointRegion) region).getName().toString())));
		}

		if (!waypointUseCounts.isEmpty()) {
			playerNBT.put("WPUses", DataUtil.saveMapAsListNBT(waypointUseCounts, (nbt, wpName, count) -> {
				((CompoundNBT) nbt).putString("WPName", wpName.toString());
				((CompoundNBT) nbt).putInt("Count", (int) count);
			}));
		}

		playerNBT.putInt("NextCWPID", nextCustomWaypointId);
		if (!customWaypoints.isEmpty()) {
			playerNBT.put("CustomWaypoints", DataUtil.saveCollectionAsCompoundListNBT(customWaypoints, (nbt, cwp) -> {
				((Entity) cwp).save((CompoundNBT) nbt);
			}));
		}

		if (!customWaypointUseCounts.isEmpty()) {
			playerNBT.put("CWPUses", DataUtil.saveMapAsListNBT(customWaypointUseCounts, (nbt, cwpId, count) -> {
				((CompoundNBT) nbt).putInt("CustomID", (int) cwpId);
				((CompoundNBT) nbt).putInt("Count", (int) count);
			}));
		}

		if (!adoptedCustomWaypoints.isEmpty()) {
			playerNBT.put("AdoptedCustomWaypoints", DataUtil.saveCollectionAsCompoundListNBT(adoptedCustomWaypoints, (nbt, cwp) -> {
				((AdoptedCustomWaypoint) cwp).save((CompoundNBT) nbt);
			}));
		}

		if (!adoptedCustomWaypointUseCounts.isEmpty()) {
			playerNBT.put("AdoptedCWPUses", DataUtil.saveMapAsListNBT(adoptedCustomWaypointUseCounts, (nbt, cwpKey, count) -> {
				((AdoptedCustomWaypointKey) cwpKey).save((CompoundNBT) nbt);
				((CompoundNBT) nbt).putInt("Count", (int) count);
			}));
		}

		playerNBT.putBoolean("ShowWP", showMapWaypoints);
		playerNBT.putBoolean("ShowCWP", showCustomWaypoints);
		playerNBT.putInt("FTSince", ftSinceTick);
		playerNBT.putLong("LastOnlineTime", lastOnlineTime);
		if (uuidToMount != null) {
			playerNBT.putUUID("MountUUID", uuidToMount);
		}

		playerNBT.putInt("MountUUIDTime", uuidToMountTime);
	}

	private void sendFTScreenPacket(ServerPlayerEntity player, Waypoint waypoint, int startX, int startZ) {
		LOTRPacketHandler.sendTo(new SPacketFastTravel(waypoint, startX, startZ), player);
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		DataUtil.writeCollectionToBuffer(buf, unlockedWaypointRegions, region -> {
			buf.writeVarInt(((WaypointRegion) region).getAssignedId());
		});
		DataUtil.writeMapToBuffer(buf, waypointUseCounts, (wpName, count) -> {
			MapWaypoint wp = currentMapSettings().getWaypointByName((ResourceLocation) wpName);
			buf.writeVarInt(wp.getAssignedId());
			buf.writeVarInt((int) count);
		});
		DataUtil.writeCollectionToBuffer(buf, customWaypoints, cwp -> {
			((CustomWaypoint) cwp).write(buf);
		});
		DataUtil.writeMapToBuffer(buf, customWaypointUseCounts, (cwpId, count) -> {
			buf.writeVarInt((int) cwpId);
			buf.writeVarInt((int) count);
		});
		DataUtil.writeCollectionToBuffer(buf, adoptedCustomWaypoints, cwp -> {
			((AdoptedCustomWaypoint) cwp).write(buf);
		});
		DataUtil.writeMapToBuffer(buf, adoptedCustomWaypointUseCounts, (cwpKey, count) -> {
			((AdoptedCustomWaypoint) cwpKey).write(buf);
			buf.writeVarInt((int) count);
		});
		buf.writeBoolean(showMapWaypoints);
		buf.writeBoolean(showCustomWaypoints);
		buf.writeInt(ftSinceTick);
	}

	private void sendShowWaypointsToClient() {
		sendPacketToClient(new SPacketShowWaypoints(showMapWaypoints, showCustomWaypoints));
	}

	private void sendShowWaypointsToServer() {
		LOTRPacketHandler.sendToServer(new CPacketToggleShowWaypoints(showMapWaypoints, showCustomWaypoints));
	}

	public void setShowCustomWaypoints(boolean flag) {
		if (showCustomWaypoints != flag) {
			showCustomWaypoints = flag;
			markDirty();
			sendShowWaypointsToClient();
		}

	}

	public void setShowMapWaypoints(boolean flag) {
		if (showMapWaypoints != flag) {
			showMapWaypoints = flag;
			markDirty();
			sendShowWaypointsToClient();
		}

	}

	public void setTargetWaypoint(Waypoint waypoint) {
		targetFTWaypoint = waypoint;
		markDirty();
		if (waypoint != null) {
			setTicksUntilFT(200);
		} else {
			setTicksUntilFT(0);
		}

	}

	public void setTicksUntilFT(int i) {
		if (ticksUntilFT != i) {
			ticksUntilFT = i;
			if (ticksUntilFT == 200 || ticksUntilFT == 0) {
				markDirty();
			}
		}

	}

	public void setTimeSinceFT(int i) {
		this.setTimeSinceFT(i, false);
	}

	private void setTimeSinceFT(int i, boolean forceUpdate) {
		int preTick = ftSinceTick;
		i = Math.max(0, i);
		ftSinceTick = i;
		boolean bigChange = (ftSinceTick == 0 || preTick == 0) && ftSinceTick != preTick || preTick < 0 && ftSinceTick >= 0;
		if (forceUpdate) {
			bigChange = true;
		}

		if (bigChange || isTimerAutosaveTick()) {
			markDirty();
		}

		if (bigChange || ftSinceTick % 5 == 0) {
			sendPacketToClient(new SPacketTimeSinceFT(ftSinceTick));
		}

	}

	public void setTimeSinceFTWithUpdate(int i) {
		this.setTimeSinceFT(i, true);
	}

	private void setUUIDToMount(UUID uuid) {
		uuidToMount = uuid;
		if (uuidToMount != null) {
			uuidToMountTime = 20;
		} else {
			uuidToMountTime = 0;
		}

		markDirty();
	}

	public void setWPUseCount(Waypoint waypoint, int count) {
		if (waypoint instanceof MapWaypoint) {
			MapWaypoint mapWp = (MapWaypoint) waypoint;
			ResourceLocation wpName = mapWp.getName();
			waypointUseCounts.put(wpName, count);
		} else if (waypoint instanceof CustomWaypoint) {
			CustomWaypoint cwp = (CustomWaypoint) waypoint;
			if (cwp.getCreatedPlayer().equals(getPlayerUUID())) {
				customWaypointUseCounts.put(cwp.getCustomId(), count);
			}
		} else if (waypoint instanceof AdoptedCustomWaypoint) {
			AdoptedCustomWaypoint cwp = (AdoptedCustomWaypoint) waypoint;
			adoptedCustomWaypointUseCounts.put(cwp.getAdoptedKey(), count);
		} else {
			LOTRLog.error("Tried to update the use count for an unknown waypoint type %s", waypoint.getClass());
		}

		markDirty();
		sendPacketToClient(new SPacketWaypointUseCount(waypoint, count));
	}

	public void toggleShowCustomWaypointsAndSendToServer() {
		showCustomWaypoints = !showCustomWaypoints;
		sendShowWaypointsToServer();
	}

	public void toggleShowMapWaypointsAndSendToServer() {
		showMapWaypoints = !showMapWaypoints;
		sendShowWaypointsToServer();
	}

	public void unlockWaypointRegion(WaypointRegion region) {
		if (!unlockedWaypointRegions.contains(region)) {
			unlockedWaypointRegions.add(region);
			markDirty();
			sendPacketToClient(new SPacketWaypointRegion(region, true));
		}

	}

	public void updateAdoptedCustomWaypointFromOriginal(CustomWaypoint originalWaypoint) {
		AdoptedCustomWaypointKey key = AdoptedCustomWaypointKey.of(originalWaypoint.getCreatedPlayer(), originalWaypoint.getCustomId());
		AdoptedCustomWaypoint adoptedWaypoint = (AdoptedCustomWaypoint) adoptedCustomWaypoints.lookup(key);
		if (adoptedWaypoint != null) {
			adoptedWaypoint.updateFromOriginal(originalWaypoint);
			markDirty();
			sendPacketToClient(new SPacketUpdateAdoptedCustomWaypoint(adoptedWaypoint));
		} else {
			playerData.logPlayerError("Tried to update an adopted custom waypoint from its original (creator %s, ID %d) but could not find it!", key.getCreatedPlayer(), key.getWaypointId());
		}

	}

	public boolean updateCustomWaypoint(World world, CustomWaypoint waypoint, String name, String lore, boolean isPublic) {
		if (checkCustomWaypointBelongsToPlayer(waypoint)) {
			CustomWaypoint existingMatch = (CustomWaypoint) customWaypoints.lookup(waypoint.getCustomId());
			existingMatch.update(world, name, lore, isPublic);
			markDirty();
			sendPacketToClient(new SPacketUpdateCustomWaypoint(waypoint));
			return true;
		}
		playerData.logPlayerError("Tried to update a custom waypoint (%s) that actually belongs to %s", waypoint.getDisplayName(), waypoint.getCreatedPlayer());
		return false;
	}

	public void updateCustomWaypointAdoptedCount(CustomWaypoint customWaypoint, int adoptedCount) {
		markDirty();
		sendPacketToClient(new SPacketCustomWaypointAdoptedCount(customWaypoint, adoptedCount));
	}

	private void updateFastTravelClockFromLastOnlineTime(ServerPlayerEntity player) {
		if (lastOnlineTime > 0L) {
			ServerWorld world = player.getLevel();
			if (!world.getServer().isSingleplayer()) {
				long currentOnlineTime = getCurrentOnlineTime(world);
				int diff = (int) (currentOnlineTime - lastOnlineTime);
				double offlineFactor = 0.1D;
				int ftClockIncrease = (int) (diff * offlineFactor);
				if (ftClockIncrease > 0) {
					setTimeSinceFTWithUpdate(ftSinceTick + ftClockIncrease);
					if (world.getGameRules().getBoolean(LOTRGameRules.FAST_TRAVEL) && LOTRWorldTypes.hasMapFeatures(world)) {
						int ftClockIncreaseSecs = ftClockIncrease / 20;
						if (ftClockIncreaseSecs > 0) {
							ITextComponent msg = new TranslationTextComponent("chat.lotr.ft.offlineTick", LOTRUtil.getHMSTime_Ticks(diff), LOTRUtil.getHMSTime_Seconds(ftClockIncreaseSecs));
							LOTRUtil.sendMessage(player, msg);
						}
					}
				}
			}

		}
	}
}
