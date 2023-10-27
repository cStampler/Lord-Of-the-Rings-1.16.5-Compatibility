package lotr.common.world.map;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import lotr.common.LOTRLog;
import lotr.common.data.DataUtil;
import lotr.common.data.LOTRLevelData;
import lotr.common.data.LOTRPlayerData;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CustomWaypoint extends AbstractCustomWaypoint {
	private boolean isPublic;
	private List adoptedPlayers;
	private int adoptedCount;

	private CustomWaypoint(MapSettings map, UUID createdPlayer, int id, String name, String lore, BlockPos worldPos) {
		super(map, createdPlayer, id, name, lore, worldPos);
		adoptedPlayers = new ArrayList();
	}

	public CustomWaypoint(MapSettings map, UUID createdPlayer, int id, String name, String lore, BlockPos worldPos, boolean isPublic) {
		this(map, createdPlayer, id, name, lore, worldPos);
		this.isPublic = isPublic;
	}

	public int getAdoptedCountForDisplay() {
		return adoptedCount;
	}

	@Override
	@Nullable
	public ITextComponent getDisplayOwnership() {
		return null;
	}

	@Override
	public WaypointNetworkType getNetworkType() {
		return WaypointNetworkType.CUSTOM;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public void onAdoptedBy(UUID playerUUID, World world) {
		if (!adoptedPlayers.contains(playerUUID)) {
			adoptedPlayers.add(playerUUID);
			updateInPlayerDataAfterAdoption(world);
		}

	}

	public void onForsakenBy(UUID playerUUID, World world) {
		if (adoptedPlayers.contains(playerUUID)) {
			adoptedPlayers.remove(playerUUID);
			updateInPlayerDataAfterAdoption(world);
		}

	}

	public void receiveAdoptedCountFromServer(int numAdopted) {
		adoptedCount = numAdopted;
	}

	public void removeFromAllAdoptedPlayersWhenDestroyed(World world) {
		List copyOfAdoptedPlayers = new ArrayList(adoptedPlayers);
		Iterator var3 = copyOfAdoptedPlayers.iterator();

		while (var3.hasNext()) {
			UUID adoptedPlayer = (UUID) var3.next();
			LOTRLevelData.sidedInstance(world).getData(world, adoptedPlayer).getFastTravelData().removeAdoptedCustomWaypointWhenOriginalDestroyed(world, this);
		}

	}

	@Override
	protected void removeFromPlayerData(PlayerEntity player) {
		LOTRLevelData.sidedInstance(player.level).getData(player).getFastTravelData().removeCustomWaypoint(player.level, this);
	}

	@Override
	public void save(CompoundNBT nbt) {
		super.save(nbt);
		nbt.putBoolean("Public", isPublic);
		if (!adoptedPlayers.isEmpty()) {
			nbt.put("AdoptedPlayers", DataUtil.saveCollectionAsPrimitiveListNBT(adoptedPlayers, playerUuid -> StringNBT.valueOf(playerUuid.toString())));
		}

	}

	public void update(World world, String newName, String newLore, boolean newIsPublic) {
		setName(newName);
		setLore(newLore);
		if (!isPublic) {
			isPublic = newIsPublic;
		}

		Iterator var5 = adoptedPlayers.iterator();

		while (var5.hasNext()) {
			UUID adoptedPlayer = (UUID) var5.next();
			LOTRLevelData.sidedInstance(world).getData(world, adoptedPlayer).getFastTravelData().updateAdoptedCustomWaypointFromOriginal(this);
		}

	}

	private void updateInPlayerDataAfterAdoption(World world) {
		LOTRLevelData.sidedInstance(world).getData(world, getCreatedPlayer()).getFastTravelData().updateCustomWaypointAdoptedCount(this, adoptedPlayers.size());
	}

	@Override
	public void write(PacketBuffer buf) {
		super.write(buf);
		buf.writeBoolean(isPublic);
		buf.writeVarInt(adoptedCount);
	}

	public static CustomWaypoint load(MapSettings map, CompoundNBT nbt) {
		CustomWaypoint waypoint = (CustomWaypoint) baseLoad(map, nbt, CustomWaypoint::new);
		waypoint.isPublic = nbt.getBoolean("Public");
		DataUtil.loadCollectionFromPrimitiveListNBT(waypoint.adoptedPlayers, nbt.getList("AdoptedPlayers", 8), (h1, h2) -> ((ListNBT) h1).getString((int) h2), h1 -> UUID.fromString((String) h1));
		return waypoint;
	}

	public static CustomWaypoint read(MapSettings map, PacketBuffer buf) {
		CustomWaypoint waypoint = (CustomWaypoint) baseRead(map, buf, CustomWaypoint::new);
		waypoint.isPublic = buf.readBoolean();
		waypoint.adoptedCount = buf.readVarInt();
		return waypoint;
	}

	public static CustomWaypoint readFromIdentification(PacketBuffer buf, LOTRPlayerData pd) {
		int wpId = buf.readVarInt();
		CustomWaypoint wp = pd.getFastTravelData().getCustomWaypointById(wpId);
		if (wp == null) {
			LOTRLog.warn("Received nonexistent custom waypoint ID %d from %s", wpId, pd.getLogicalSide());
		}

		return wp;
	}

	public static void writeIdentification(PacketBuffer buf, CustomWaypoint wp) {
		buf.writeVarInt(wp.getCustomId());
	}
}
