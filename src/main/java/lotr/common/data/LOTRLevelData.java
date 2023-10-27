package lotr.common.data;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import lotr.common.LOTRLog;
import lotr.common.config.LOTRConfig;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketAlignment;
import lotr.common.network.SPacketLoginLOTR;
import lotr.common.network.SPacketMapPlayerLocations;
import lotr.common.network.SPacketRingPortalPos;
import lotr.common.network.SPacketWorldWaypointCooldown;
import lotr.common.time.LOTRDate;
import lotr.common.util.UsernameHelper;
import lotr.common.world.map.MapPlayerLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;

public class LOTRLevelData {
	private static final LOTRLevelData SERVER_INSTANCE;
	private static final LOTRLevelData CLIENT_INSTANCE;
	static {
		SERVER_INSTANCE = new LOTRLevelData(LogicalSide.SERVER);
		CLIENT_INSTANCE = new LOTRLevelData(LogicalSide.CLIENT);
	}
	private final LogicalSide side;
	private boolean madeOverworldPortal = false;
	private int overworldPortalX;
	private int overworldPortalY;
	private int overworldPortalZ;
	private boolean madeMiddleEarthPortal = false;
	private int middleEarthPortalX;
	private int middleEarthPortalY;
	private int middleEarthPortalZ;
	private Map playerDataMap = new HashMap();
	private int waypointCooldownMax = 1800;
	private int waypointCooldownMin = 180;
	private boolean needsLoad = true;

	private boolean needsSave = false;

	private LOTRLevelData(LogicalSide side) {
		this.side = side;
	}

	public boolean anyDataNeedsSave() {
		if (needsSave) {
			return true;
		}
		Iterator var1 = playerDataMap.values().iterator();

		LOTRPlayerData pd;
		do {
			if (!var1.hasNext()) {
				return false;
			}

			pd = (LOTRPlayerData) var1.next();
		} while (!pd.needsSave());

		return true;
	}

	public void destroyAllPlayerData() {
		playerDataMap.clear();
	}

	public LOTRPlayerData getData(PlayerEntity player) {
		return this.getData(player.getCommandSenderWorld(), player.getUUID());
	}

	public LOTRPlayerData getData(World world, UUID player) {
		LOTRPlayerData pd = (LOTRPlayerData) playerDataMap.get(player);
		if (pd == null) {
			if (world instanceof ServerWorld) {
				pd = loadData((ServerWorld) world, player);
			}

			if (pd == null) {
				pd = new LOTRPlayerData(this, player);
			}

			playerDataMap.put(player, pd);
		}

		return pd;
	}

	public LogicalSide getLogicalSide() {
		return side;
	}

	public BlockPos getMiddleEarthPortalLocation() {
		return new BlockPos(middleEarthPortalX, middleEarthPortalY, middleEarthPortalZ);
	}

	public BlockPos getOverworldPortalLocation() {
		return new BlockPos(overworldPortalX, overworldPortalY, overworldPortalZ);
	}

	public int getWaypointCooldownMax() {
		return waypointCooldownMax;
	}

	public int getWaypointCooldownMin() {
		return waypointCooldownMin;
	}

	public void load(ServerWorld world) {
		try {
			CompoundNBT levelData = SaveUtil.loadNBTFromFile(getLOTRDat(world));
			madeOverworldPortal = levelData.getBoolean("MadePortal");
			overworldPortalX = levelData.getInt("OverworldX");
			overworldPortalY = levelData.getInt("OverworldY");
			overworldPortalZ = levelData.getInt("OverworldZ");
			madeMiddleEarthPortal = levelData.getBoolean("MadeMiddlePortal");
			middleEarthPortalX = levelData.getInt("MiddleEarthX");
			middleEarthPortalY = levelData.getInt("MiddleEarthY");
			middleEarthPortalZ = levelData.getInt("MiddleEarthZ");
			if (levelData.contains("WpCdMax") && levelData.contains("WpCdMin")) {
				int max = levelData.getInt("WpCdMax");
				int min = levelData.getInt("WpCdMin");
				max = Math.max(0, max);
				min = Math.max(0, min);
				if (min > max) {
					min = max;
				}

				waypointCooldownMax = max;
				waypointCooldownMin = min;
			} else {
				waypointCooldownMax = 1800;
				waypointCooldownMin = 180;
			}

			destroyAllPlayerData();
			LOTRDate.loadDates(levelData);
			needsLoad = false;
			needsSave = true;
			save(world);
		} catch (Exception var5) {
			LOTRLog.error("Error loading mod data");
			var5.printStackTrace();
		}

	}

	private LOTRPlayerData loadData(ServerWorld world, UUID player) {
		try {
			CompoundNBT nbt = SaveUtil.loadNBTFromFile(getLOTRPlayerDat(world, player));
			LOTRPlayerData pd = new LOTRPlayerData(this, player);
			pd.load(nbt);
			return pd;
		} catch (Exception var5) {
			LOTRLog.error("Error loading player data for %s", player);
			var5.printStackTrace();
			return null;
		}
	}

	public boolean madeMiddleEarthPortal() {
		return madeMiddleEarthPortal;
	}

	public boolean madePortal() {
		return madeOverworldPortal;
	}

	public void markDirty() {
		needsSave = true;
	}

	public void markMiddleEarthPortalLocation(World world, BlockPos pos) {
		middleEarthPortalX = pos.getX();
		middleEarthPortalY = pos.getY();
		middleEarthPortalZ = pos.getZ();
		markDirty();
		if (!world.isClientSide) {
			SPacketRingPortalPos packet = new SPacketRingPortalPos(pos);
			LOTRPacketHandler.sendToAll(packet);
		}

	}

	public void markOverworldPortalLocation(BlockPos pos) {
		overworldPortalX = pos.getX();
		overworldPortalY = pos.getY();
		overworldPortalZ = pos.getZ();
		markDirty();
	}

	public boolean needsLoad() {
		return needsLoad;
	}

	public void playerDataHandleLogin(ServerPlayerEntity player) {
		try {
			LOTRPlayerData pd = this.getData(player);
			pd.handleLoginAndSendLoginData(player);
		} catch (Exception var3) {
			LOTRLog.error("Failed to send player data to player %s", UsernameHelper.getRawUsername(player));
			var3.printStackTrace();
		}

	}

	public void resetNeedsLoad() {
		needsLoad = true;
	}

	public void save(ServerWorld world) {
		try {
			if (needsSave) {
				File LOTR_dat = getLOTRDat(world);
				if (!LOTR_dat.exists()) {
					SaveUtil.saveNBTToFile(LOTR_dat, new CompoundNBT());
				}

				CompoundNBT levelData = new CompoundNBT();
				levelData.putBoolean("MadePortal", madeOverworldPortal);
				levelData.putInt("OverworldX", overworldPortalX);
				levelData.putInt("OverworldY", overworldPortalY);
				levelData.putInt("OverworldZ", overworldPortalZ);
				levelData.putBoolean("MadeMiddlePortal", madeMiddleEarthPortal);
				levelData.putInt("MiddleEarthX", middleEarthPortalX);
				levelData.putInt("MiddleEarthY", middleEarthPortalY);
				levelData.putInt("MiddleEarthZ", middleEarthPortalZ);
				levelData.putInt("WpCdMax", waypointCooldownMax);
				levelData.putInt("WpCdMin", waypointCooldownMin);
				LOTRDate.saveDates(levelData);
				SaveUtil.saveNBTToFile(LOTR_dat, levelData);
				needsSave = false;
			}

			Iterator var9 = playerDataMap.entrySet().iterator();

			while (var9.hasNext()) {
				Entry e = (Entry) var9.next();
				UUID player = (UUID) e.getKey();
				LOTRPlayerData pd = (LOTRPlayerData) e.getValue();
				if (pd.needsSave()) {
					saveData(world, player);
				}
			}
		} catch (Exception var7) {
			LOTRLog.error("Error saving mod data");
			var7.printStackTrace();
		}

	}

	private boolean saveAndClearData(ServerWorld world, UUID player) {
		LOTRPlayerData pd = (LOTRPlayerData) playerDataMap.get(player);
		if (pd == null) {
			LOTRLog.warn("Attempted to clear player data for %s; no data found", player);
			return false;
		}
		boolean saved = false;
		if (pd.needsSave()) {
			saveData(world, player);
			saved = true;
		}

		playerDataMap.remove(player);
		return saved;
	}

	public void saveAndClearUnusedPlayerData(ServerWorld world) {
		List playersToClear = new ArrayList();
		PlayerList serverPlayerList = world.getServer().getPlayerList();
		Iterator var4 = playerDataMap.keySet().iterator();

		while (var4.hasNext()) {
			UUID player = (UUID) var4.next();
			if (serverPlayerList.getPlayer(player) == null) {
				playersToClear.add(player);
			}
		}

		playersToClear.size();
		playerDataMap.size();
		Iterator var7 = playersToClear.iterator();

		while (var7.hasNext()) {
			UUID player = (UUID) var7.next();
			boolean saved = saveAndClearData(world, player);
			if (saved) {
			}
		}

		playerDataMap.size();
	}

	public void saveData(ServerWorld world, UUID player) {
		try {
			CompoundNBT nbt = new CompoundNBT();
			LOTRPlayerData pd = (LOTRPlayerData) playerDataMap.get(player);
			pd.save(nbt);
			SaveUtil.saveNBTToFile(getLOTRPlayerDat(world, player), nbt);
		} catch (Exception var5) {
			LOTRLog.error("Error saving player data for %s", player);
			var5.printStackTrace();
		}

	}

	public void sendAllOtherPlayerAlignmentsToPlayer(ServerPlayerEntity player) {
		MinecraftServer server = player.server;
		for (ServerPlayerEntity otherPlayer : server.getPlayerList().getPlayers()) {
			if (!otherPlayer.getUUID().equals(player.getUUID())) {
				AlignmentDataModule otherAlignData = this.getData(otherPlayer).getAlignmentData();
				LOTRPacketHandler.sendTo(new SPacketAlignment(otherAlignData.getAlignmentsView(), otherPlayer), player);
			}
		}

	}

	public void sendLoginPacket(ServerPlayerEntity player) {
		SPacketLoginLOTR packet = new SPacketLoginLOTR();
		packet.setMiddleEarthPortalPos(middleEarthPortalX, middleEarthPortalY, middleEarthPortalZ);
		packet.setWaypointCooldownMaxMin(waypointCooldownMax, waypointCooldownMin);
		packet.setAreasOfInfluence((Boolean) LOTRConfig.COMMON.areasOfInfluence.get());
		packet.setSmallerBees((Boolean) LOTRConfig.COMMON.smallerBees.get());
		packet.setHasMapFeatures(LOTRWorldTypes.hasMapFeatures(player.getLevel()));
		packet.setForceFogOfWar((Integer) LOTRConfig.SERVER.forceFogOfWar.get());
		LOTRPacketHandler.sendTo(packet, player);
	}

	public void sendPlayerAlignmentToAllOtherPlayers(ServerPlayerEntity player) {
		AlignmentDataModule alignData = this.getData(player).getAlignmentData();
		LOTRPacketHandler.sendToAllExcept(new SPacketAlignment(alignData.getAlignmentsView(), player), player);
	}

	public void sendPlayerLocationsToPlayer(PlayerEntity targetPlayer, ServerWorld world) {
		List playerLocations = new ArrayList();
		MinecraftServer server = world.getServer();
		boolean isOp = server.getPlayerList().isOp(targetPlayer.getGameProfile());
		boolean creative = targetPlayer.abilities.instabuild;
		this.getData(targetPlayer);
		Iterator var8 = world.players().iterator();

		while (true) {
			PlayerEntity otherPlayer;
			do {
				if (!var8.hasNext()) {
					SPacketMapPlayerLocations packet = new SPacketMapPlayerLocations(playerLocations);
					LOTRPacketHandler.sendTo(packet, (ServerPlayerEntity) targetPlayer);
					return;
				}

				otherPlayer = (PlayerEntity) var8.next();
			} while (otherPlayer == targetPlayer);

			boolean show = !this.getData(otherPlayer).getHideMapLocation();
			if (!isOp && this.getData(otherPlayer).getAdminHideMap() || (Integer) LOTRConfig.SERVER.forceMapLocations.get() == 1) {
				show = false;
			} else if ((Integer) LOTRConfig.SERVER.forceMapLocations.get() == 2 || !show && isOp && creative) {
				show = true;
			}

			if (show) {
				playerLocations.add(MapPlayerLocation.ofPlayer(otherPlayer));
			}
		}
	}

	public void setMadeMiddleEarthPortal(boolean flag) {
		madeMiddleEarthPortal = flag;
		markDirty();
	}

	public void setMadePortal(boolean flag) {
		madeOverworldPortal = flag;
		markDirty();
	}

	public void setWaypointCooldown(World world, int max, int min) {
		int prevMax = waypointCooldownMax;
		int prevMin = waypointCooldownMin;
		max = Math.max(0, max);
		min = Math.max(0, min);
		if (min > max) {
			min = max;
		}

		if (max != prevMax || min != prevMin) {
			waypointCooldownMax = max;
			waypointCooldownMin = min;
			markDirty();
			if (!world.isClientSide) {
				SPacketWorldWaypointCooldown packet = new SPacketWorldWaypointCooldown(waypointCooldownMax, waypointCooldownMin);
				LOTRPacketHandler.sendToAll(packet);
			}
		}

	}

	public static LOTRLevelData clientInstance() {
		return CLIENT_INSTANCE;
	}

	private static File getLOTRDat(ServerWorld world) {
		String filename = "lotr".toUpperCase();
		return new File(SaveUtil.getOrCreateLOTRDir(world), filename + ".dat");
	}

	private static File getLOTRPlayerDat(ServerWorld world, UUID player) {
		File playerDir = new File(SaveUtil.getOrCreateLOTRDir(world), "players");
		if (!playerDir.exists()) {
			playerDir.mkdirs();
		}

		return new File(playerDir, player.toString() + ".dat");
	}

	public static LOTRPlayerData getSidedData(PlayerEntity player) {
		return sidedInstance(player).getData(player);
	}

	public static LOTRLevelData serverInstance() {
		return SERVER_INSTANCE;
	}

	public static LOTRLevelData sidedInstance(Entity e) {
		return sidedInstance(e.level);
	}

	public static LOTRLevelData sidedInstance(IWorldReader world) {
		return !world.isClientSide() ? SERVER_INSTANCE : CLIENT_INSTANCE;
	}
}
