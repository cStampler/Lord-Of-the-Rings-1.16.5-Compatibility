package lotr.common.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Function;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketLoginPlayerDataModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

public class LOTRPlayerData {
	private final LOTRLevelData levelData;
	private final UUID playerUUID;
	private final List<PlayerDataModule> modules = new ArrayList<>();
	private final BiMap<String, PlayerDataModule> modulesByName = HashBiMap.create();
	private final FastTravelDataModule fastTravelData;
	private final MapMarkerDataModule mapMarkerData;
	private final AlignmentDataModule alignmentData;
	private final FactionStatsDataModule factionStatsData;
	private final MessageDataModule messageData;
	private final FogDataModule fogData;
	private final MiscDataModule miscData;
	private boolean needsSave = false;
	private int updateTick;

	public LOTRPlayerData(LOTRLevelData level, UUID player) {
		levelData = level;
		playerUUID = player;
		fastTravelData = addModule("FastTravel", hummel -> new FastTravelDataModule(hummel));
		mapMarkerData = addModule("MapMarkers", hummel -> new MapMarkerDataModule(hummel));
		alignmentData = addModule("Alignment", hummel -> new AlignmentDataModule(hummel));
		factionStatsData = addModule("FactionStats", hummel -> new FactionStatsDataModule(hummel));
		messageData = addModule("Messages", hummel -> new MessageDataModule(hummel));
		fogData = addModule("Fog", hummel -> new FogDataModule(hummel));
		miscData = addModule("Misc", hummel -> new MiscDataModule(hummel));
	}

	private <M extends PlayerDataModule> M addModule(String code, Function<LOTRPlayerData, M> moduleConstructor) {
		M module = moduleConstructor.apply(this);
		modules.add(module);
		modulesByName.put(code, module);
		return (M)module;
	}

	public boolean executeIfPlayerExistsServerside(Consumer<ServerPlayerEntity> action) {
		PlayerEntity player = findPlayer();
		if (player instanceof ServerPlayerEntity) {
			action.accept((ServerPlayerEntity)player);
			return true;
		}
		return false;
	}

	private PlayerEntity findPlayer() {
		if (LOTRMod.PROXY.isClient()) {
			return LOTRMod.PROXY.getClientPlayer();
		}
		MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
		return server.getPlayerList().getPlayer(playerUUID);
	}

	public boolean getAdminHideMap() {
		return false;
	}

	public AlignmentDataModule getAlignmentData() {
		return alignmentData;
	}

	public FactionStatsDataModule getFactionStatsData() {
		return factionStatsData;
	}

	public FastTravelDataModule getFastTravelData() {
		return fastTravelData;
	}

	public FogDataModule getFogData() {
		return fogData;
	}

	public boolean getHideMapLocation() {
		return !miscData.getShowMapLocation();
	}

	public LogicalSide getLogicalSide() {
		return levelData.getLogicalSide();
	}

	public MapMarkerDataModule getMapMarkerData() {
		return mapMarkerData;
	}

	public MessageDataModule getMessageData() {
		return messageData;
	}

	public MiscDataModule getMiscData() {
		return miscData;
	}

	private String getModuleCode(PlayerDataModule module) {
		return (String) modulesByName.inverse().get(module);
	}

	public LOTRLevelData getParentLevelData() {
		return levelData;
	}

	public UUID getPlayerUUID() {
		return playerUUID;
	}

	public void handleLoginAndSendLoginData(ServerPlayerEntity player) {
	    for (PlayerDataModule module : this.modules) {
	      module.handleLogin(player);
	      PacketBuffer buf = new PacketBuffer(Unpooled.buffer());
	      module.sendLoginData(buf);
	      if (buf.writerIndex() > 0) {
	        String moduleCode = getModuleCode(module);
	        SPacketLoginPlayerDataModule packet = new SPacketLoginPlayerDataModule(moduleCode, buf);
	        LOTRPacketHandler.sendTo(packet, player);
	      } 
	    } 
	  }

	public void load(CompoundNBT playerNBT) {
	    for (PlayerDataModule module : this.modules) {
	      try {
	        module.load(playerNBT);
	      } catch (Exception e) {
	        LOTRLog.error("Error loading player data module %s for player %s", new Object[] { getModuleCode(module), this.playerUUID.toString() });
	        e.printStackTrace();
	      } 
	    } 
	  }

	protected void logPlayerError(String msg, Object... args) {
		LOTRLog.error("playerdata %s: %s", playerUUID.toString(), String.format(msg, args));
	}

	protected void markDirty() {
		needsSave = true;
	}

	public boolean needsSave() {
		return needsSave;
	}

	public void onUpdate(ServerPlayerEntity player, ServerWorld world) {
		++updateTick;
		modules.forEach(module -> {
			((PlayerDataModule) module).onUpdate(player, world, updateTick);
		});
	}

	public void receiveLoginData(String moduleCode, ByteBuf moduleData) {
		PlayerDataModule module = (PlayerDataModule) modulesByName.get(moduleCode);
		if (module != null) {
			module.receiveLoginData(new PacketBuffer(moduleData));
		} else {
			LOTRLog.error("Received login playerdata for nonexistent data module %s", moduleCode);
		}

	}

	public void save(CompoundNBT playerNBT) {
	    for (PlayerDataModule module : this.modules) {
	      try {
	        module.save(playerNBT);
	      } catch (Exception e) {
	        LOTRLog.error("Error saving player data module %s for player %s", new Object[] { getModuleCode(module), this.playerUUID.toString() });
	        e.printStackTrace();
	      } 
	    } 
	    this.needsSave = false;
	  }

	public void sendPacketToClient(Object packet) {
		boolean executed = executeIfPlayerExistsServerside(player -> {
			LOTRPacketHandler.sendTo(packet, (ServerPlayerEntity) player);
		});
		if (!executed && getParentLevelData().getLogicalSide() == LogicalSide.SERVER) {
			LOTRLog.error("Server tried to send a playerdata packet (%s) to %s, but didn't find the player online!", packet.getClass().getSimpleName(), playerUUID);
		}

	}
}
