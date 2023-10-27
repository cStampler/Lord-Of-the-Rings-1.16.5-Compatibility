package lotr.common.time;

import java.io.File;
import java.io.FileOutputStream;

import lotr.common.LOTRLog;
import lotr.common.data.SaveUtil;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketLOTRTimeUpdate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.world.GameRules;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class LOTRTime {
	public static final int DAY_FACTOR = 2;
	public static final long DAY_LENGTH = 48000L;
	private static long worldTime;
	private static long worldTimeClient;
	private static boolean needsLoad = true;

	public static void addWorldTime(World world, long time) {
		setWorldTime(world, getWorldTime(world) + time);
	}

	public static void advanceToMorning(ServerWorld world) {
		if (world.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
			long l = worldTime + 48000L;
			setWorldTime(world, l - l % 48000L);
		}

	}

	private static SPacketLOTRTimeUpdate createTimePacket() {
		return new SPacketLOTRTimeUpdate(worldTime);
	}

	private static File getTimeDat(ServerWorld world) {
		return new File(SaveUtil.getOrCreateLOTRDir(world), "LOTRTime.dat");
	}

	public static long getWorldTime(boolean isRemote) {
		return !isRemote ? worldTime : worldTimeClient;
	}

	public static long getWorldTime(IWorldReader world) {
		return getWorldTime(world.isClientSide());
	}

	public static void load(ServerWorld world) {
		try {
			CompoundNBT timeData = SaveUtil.loadNBTFromFile(getTimeDat(world));
			worldTime = timeData.getLong("LOTRWorldTime");
			needsLoad = false;
			save(world);
		} catch (Exception var2) {
			LOTRLog.error("Error loading LOTR time data");
			var2.printStackTrace();
		}

	}

	public static boolean needsLoad() {
		return needsLoad;
	}

	public static void resetNeedsLoad() {
		needsLoad = true;
	}

	public static void save(ServerWorld world) {
		try {
			File time_dat = getTimeDat(world);
			if (!time_dat.exists()) {
				CompressedStreamTools.writeCompressed(new CompoundNBT(), new FileOutputStream(time_dat));
			}

			CompoundNBT timeData = new CompoundNBT();
			timeData.putLong("LOTRWorldTime", worldTime);
			SaveUtil.saveNBTToFile(time_dat, timeData);
		} catch (Exception var3) {
			LOTRLog.error("Error saving world time");
			var3.printStackTrace();
		}

	}

	public static void sendLoginPacket(ServerPlayerEntity player) {
		LOTRPacketHandler.sendTo(createTimePacket(), player);
	}

	public static void setWorldTime(World world, long time) {
		if (!world.isClientSide) {
			long prevTime = getWorldTime(world);
			worldTime = time;
			boolean bigChange = Math.abs(worldTime - prevTime) >= 100L;
			if (worldTime % 20L == 0L || bigChange) {
				LOTRPacketHandler.sendToDimensionWorld(createTimePacket(), world);
			}
		} else {
			worldTimeClient = time;
		}

	}

	public static void updateTime(World world_ME) {
		if (world_ME.getGameRules().getBoolean(GameRules.RULE_DAYLIGHT)) {
			addWorldTime(world_ME, 1L);
		}

	}
}
