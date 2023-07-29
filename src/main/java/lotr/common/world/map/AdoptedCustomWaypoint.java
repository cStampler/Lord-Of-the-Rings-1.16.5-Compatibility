package lotr.common.world.map;

import java.util.UUID;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.util.UsernameHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;

public class AdoptedCustomWaypoint extends AbstractCustomWaypoint {
	private String ownerUsername;

	public AdoptedCustomWaypoint(MapSettings map, UUID createdPlayer, int id, String name, String lore, BlockPos worldPos) {
		super(map, createdPlayer, id, name, lore, worldPos);
	}

	public AdoptedCustomWaypointKey getAdoptedKey() {
		return AdoptedCustomWaypointKey.keyFor(this);
	}

	@Override
	public ITextComponent getDisplayOwnership() {
		String ownerNameOrId = ownerUsername != null ? ownerUsername : getCreatedPlayer().toString();
		return new TranslationTextComponent("gui.lotr.map.waypoint.adopted.owner", ownerNameOrId);
	}

	@Override
	public WaypointNetworkType getNetworkType() {
		return WaypointNetworkType.ADOPTED_CUSTOM;
	}

	@Override
	protected void removeFromPlayerData(PlayerEntity player) {
		LOTRLevelData.sidedInstance(player.level).getData(player).getFastTravelData().removeAdoptedCustomWaypoint(player.level, this);
	}

	@Override
	public void save(CompoundNBT nbt) {
		super.save(nbt);
	}

	public void updateFromOriginal(CustomWaypoint originalWaypoint) {
		this.updateFromOriginal(originalWaypoint.getRawName(), originalWaypoint.getRawLore());
	}

	public void updateFromOriginal(String newName, String newLore) {
		setName(newName);
		setLore(newLore);
	}

	@Override
	public void write(PacketBuffer buf) {
		super.write(buf);
		String username = UsernameHelper.getLastKnownUsernameOrFallback(getCreatedPlayer());
		buf.writeUtf(username);
	}

	public static AdoptedCustomWaypoint adopt(MapSettings map, CustomWaypoint waypoint) {
		return new AdoptedCustomWaypoint(map, waypoint.getCreatedPlayer(), waypoint.getCustomId(), waypoint.getRawName(), waypoint.getRawLore(), waypoint.getPosition());
	}

	public static AdoptedCustomWaypoint load(MapSettings map, CompoundNBT nbt) {
		return (AdoptedCustomWaypoint) baseLoad(map, nbt, AdoptedCustomWaypoint::new);
	}

	public static AdoptedCustomWaypoint read(MapSettings map, PacketBuffer buf) {
		AdoptedCustomWaypoint waypoint = (AdoptedCustomWaypoint) baseRead(map, buf, AdoptedCustomWaypoint::new);
		waypoint.ownerUsername = buf.readUtf();
		return waypoint;
	}

	public static AdoptedCustomWaypoint readFromIdentification(PacketBuffer buf, LOTRPlayerData pd) {
		AdoptedCustomWaypointKey key = AdoptedCustomWaypointKey.read(buf);
		AdoptedCustomWaypoint wp = pd.getFastTravelData().getAdoptedCustomWaypointByKey(key);
		if (wp == null) {
			LOTRLog.warn("Received nonexistent adopted custom waypoint (creator %s, ID %d) from %s", key.getCreatedPlayer(), key.getWaypointId(), pd.getLogicalSide());
		}

		return wp;
	}

	public static void writeIdentification(PacketBuffer buf, AdoptedCustomWaypoint wp) {
		wp.getAdoptedKey().write(buf);
	}
}
