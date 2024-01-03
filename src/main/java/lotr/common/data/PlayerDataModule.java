package lotr.common.data;

import java.util.UUID;
import java.util.function.Consumer;

import lotr.common.LOTRLog;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionSettings;
import lotr.common.fac.FactionSettingsManager;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public abstract class PlayerDataModule {
	protected final LOTRPlayerData playerData;

	protected PlayerDataModule(LOTRPlayerData pd) {
		playerData = pd;
	}

	protected final FactionSettings currentFactionSettings() {
		return FactionSettingsManager.sidedInstance(getLevelData().getLogicalSide()).getCurrentLoadedFactions();
	}

	protected final MapSettings currentMapSettings() {
		return MapSettingsManager.sidedInstance(getLevelData().getLogicalSide()).getCurrentLoadedMap();
	}

	protected final void executeIfPlayerExistsServerside(Consumer<ServerPlayerEntity> action) {
		playerData.executeIfPlayerExistsServerside(action);
	}

	protected final LOTRLevelData getLevelData() {
		return playerData.getParentLevelData();
	}

	protected final UUID getPlayerUUID() {
		return playerData.getPlayerUUID();
	}

	protected void handleLogin(ServerPlayerEntity player) {
	}

	public abstract void load(CompoundNBT var1);

	protected final Faction loadFactionFromNBT(CompoundNBT nbt, String key, String messageIfNotExists) {
		if (!nbt.contains(key)) {
			return null;
		}
		String facName = nbt.getString(key);
		Faction faction = currentFactionSettings().getFactionByName(new ResourceLocation(facName));
		if (faction == null) {
			playerData.logPlayerError(messageIfNotExists, facName);
		}

		return faction;
	}

	protected final void markDirty() {
		playerData.markDirty();
	}

	protected void onUpdate(ServerPlayerEntity player, ServerWorld world, int tick) {
	}

	protected final Faction readFactionFromBuffer(PacketBuffer buf, String messageIfNotExists) {
		int facId = buf.readVarInt();
		if (facId < 0) {
			return null;
		}
		Faction faction = currentFactionSettings().getFactionByID(facId);
		if (faction == null) {
			LOTRLog.warn(messageIfNotExists, facId);
		}

		return faction;
	}

	protected void receiveLoginData(PacketBuffer buf) {
	}

	public abstract void save(CompoundNBT var1);

	protected void sendLoginData(PacketBuffer buf) {
	}

	protected final void sendPacketToClient(Object message) {
		playerData.sendPacketToClient(message);
	}

	protected final void writeFactionToBuffer(PacketBuffer buf, Faction faction) {
		buf.writeVarInt(faction != null ? faction.getAssignedId() : -1);
	}

	protected final void writeFactionToNBT(CompoundNBT nbt, String key, Faction faction) {
		if (faction != null) {
			nbt.putString(key, faction.getName().toString());
		}

	}

	protected static boolean isTimerAutosaveTick() {
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server != null && server.getTickCount() % 200 == 0;
	}
}
