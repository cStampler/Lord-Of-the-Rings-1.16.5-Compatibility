package lotr.common.data;

import java.util.BitSet;
import java.util.stream.Stream;

import lotr.common.config.*;
import lotr.common.util.LOTRUtil;
import lotr.common.world.map.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.profiler.IProfiler;
import net.minecraft.world.server.ServerWorld;

public class FogDataModule extends PlayerDataModule {
	private static final int EXPLORATION_UPDATE_INTERVAL = LOTRUtil.secondsToTicks(5);
	private final MapExploration mapExploration = new MapExploration();

	protected FogDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	public boolean isFogged(int mapX, int mapZ) {
		if (!isFogOfWarEnabledClientside()) {
			return false;
		}
		return !mapExploration.isExplored(mapX, mapZ);
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		mapExploration.load(playerNBT.getCompound("MapExploration"), getPlayerUUID());
	}

	@Override
	protected void onUpdate(ServerPlayerEntity player, ServerWorld world, int tick) {
		MapSettings currentMap = MapSettingsManager.serverInstance().getCurrentLoadedMap();
		if (currentMap != null) {
			if (mapExploration.initialiseIfEmptyOrChanged(player, currentMap)) {
				markDirty();
			}

			if (tick % EXPLORATION_UPDATE_INTERVAL == 0 && mapExploration.onUpdate(player, currentMap)) {
				markDirty();
			}
		}

	}

	public void receiveFullGridFromServer(PacketBuffer buf) {
		mapExploration.read(buf);
	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		mapExploration.read(buf);
	}

	public void receiveSingleTileUpdateFromServer(int mapX, int mapZ, BitSet tileBits) {
		mapExploration.receiveSingleTileUpdateFromServer(mapX, mapZ, tileBits);
	}

	@Override
	public void save(CompoundNBT playerNBT) {
		playerNBT.put("MapExploration", mapExploration.save(new CompoundNBT()));
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		mapExploration.write(buf);
	}

	public Stream streamTilesForRendering(double mapXMin, double mapXMax, double mapZMin, double mapZMax, IProfiler profiler) {
		return mapExploration.streamTilesForRendering(mapXMin, mapXMax, mapZMin, mapZMax, profiler);
	}

	public static boolean isFogOfWarEnabledClientside() {
		int forcedFromServer = ClientsideCurrentServerConfigSettings.INSTANCE.forceFogOfWar;
		if (forcedFromServer == 1) {
			return true;
		}
		return forcedFromServer == 2 ? false : (Boolean) LOTRConfig.CLIENT.fogOfWar.get();
	}
}
