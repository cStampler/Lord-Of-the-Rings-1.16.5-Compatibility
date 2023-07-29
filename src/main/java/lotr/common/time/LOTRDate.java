package lotr.common.time;

import lotr.common.LOTRLog;
import lotr.common.data.LOTRLevelData;
import lotr.common.dim.LOTRDimensionType;
import lotr.common.network.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.server.ServerWorld;

public class LOTRDate {
	private static long prevWorldTime = -1L;

	private static SPacketDate createDatePacket(boolean displayNewDate) {
		CompoundNBT dateData = new CompoundNBT();
		saveDates(dateData);
		return new SPacketDate(dateData, displayNewDate);
	}

	public static void loadDates(CompoundNBT levelData) {
		if (levelData.contains("Dates", 10)) {
			CompoundNBT dateData = levelData.getCompound("Dates");
			int currentDay = 0;
			if (dateData.contains("CurrentDay")) {
				currentDay = dateData.getInt("CurrentDay");
			} else if (dateData.contains("ShireDate")) {
				currentDay = dateData.getInt("ShireDate");
			}

			MiddleEarthCalendar.currentDay = currentDay;
		} else {
			MiddleEarthCalendar.currentDay = 0;
		}

	}

	public static void resetWorldTimeInMenu() {
		prevWorldTime = -1L;
	}

	public static void saveDates(CompoundNBT levelData) {
		CompoundNBT dateData = new CompoundNBT();
		dateData.putInt("CurrentDay", MiddleEarthCalendar.currentDay);
		levelData.put("Dates", dateData);
	}

	public static void sendDisplayPacket(ServerPlayerEntity player) {
		LOTRPacketHandler.sendTo(createDatePacket(true), player);
	}

	public static void sendLoginPacket(ServerPlayerEntity player) {
		LOTRPacketHandler.sendTo(createDatePacket(false), player);
	}

	public static void setDate(ServerWorld world, int date) {
		MiddleEarthCalendar.currentDay = date;
		LOTRLevelData.sidedInstance(world).markDirty();
		LOTRLog.info("Updating LOTR day: " + ((ShireReckoning.ShireDate) ShireReckoning.INSTANCE.getCurrentDate()).getDateName(false).getString());
		LOTRPacketHandler.sendToAll(createDatePacket(true));
	}

	public static void updateDate(ServerWorld world) {
		if (world.dimensionType() instanceof LOTRDimensionType) {
			long worldTime = world.getDayTime();
			if (prevWorldTime == -1L) {
				prevWorldTime = worldTime;
			}

			long prevDay = prevWorldTime / 48000L;
			long day = worldTime / 48000L;
			if (day != prevDay) {
				setDate(world, MiddleEarthCalendar.currentDay + 1);
			}

			prevWorldTime = worldTime;
		}
	}
}
