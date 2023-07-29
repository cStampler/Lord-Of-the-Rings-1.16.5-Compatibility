package lotr.common.command;

import java.util.List;

import lotr.common.data.*;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;

public class LOTRBaseCommand {
	protected static List allFactions() {
		return FactionSettingsManager.serverInstance().getCurrentLoadedFactions().getAllPlayableAlignmentFactions();
	}

	protected static MapSettings currentLoadedMap() {
		return MapSettingsManager.serverInstance().getCurrentLoadedMap();
	}

	protected static AlignmentDataModule getAlignData(ServerPlayerEntity player) {
		return getPlayerData(player).getAlignmentData();
	}

	protected static LOTRLevelData getLevelData() {
		return LOTRLevelData.serverInstance();
	}

	protected static LOTRPlayerData getPlayerData(ServerPlayerEntity player) {
		return getLevelData().getData(player);
	}
}
