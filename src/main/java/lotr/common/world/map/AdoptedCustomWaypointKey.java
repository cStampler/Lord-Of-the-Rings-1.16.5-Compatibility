package lotr.common.world.map;

import java.util.Objects;
import java.util.UUID;

import lotr.common.data.DataUtil;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class AdoptedCustomWaypointKey {
	private final UUID createdPlayer;
	private final int waypointId;

	private AdoptedCustomWaypointKey(UUID createdPlayer, int waypointId) {
		this.createdPlayer = createdPlayer;
		this.waypointId = waypointId;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AdoptedCustomWaypointKey && obj.getClass() == this.getClass()) {
			AdoptedCustomWaypointKey otherKey = (AdoptedCustomWaypointKey) obj;
			return otherKey.createdPlayer.equals(createdPlayer) && otherKey.waypointId == waypointId;
		}
		return false;
	}

	public UUID getCreatedPlayer() {
		return createdPlayer;
	}

	public int getWaypointId() {
		return waypointId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(createdPlayer, waypointId);
	}

	public void save(CompoundNBT nbt) {
		nbt.putUUID("CreatedPlayer", createdPlayer);
		nbt.putInt("WaypointID", waypointId);
	}

	public void write(PacketBuffer buf) {
		buf.writeUUID(createdPlayer);
		buf.writeVarInt(waypointId);
	}

	public static AdoptedCustomWaypointKey keyFor(AbstractCustomWaypoint waypoint) {
		return new AdoptedCustomWaypointKey(waypoint.getCreatedPlayer(), waypoint.getCustomId());
	}

	public static AdoptedCustomWaypointKey load(CompoundNBT nbt) {
		UUID createdPlayer = DataUtil.getUniqueIdBackCompat(nbt, "CreatedPlayer");
		int waypointId = nbt.getInt("WaypointID");
		return of(createdPlayer, waypointId);
	}

	public static AdoptedCustomWaypointKey of(UUID createdPlayer, int waypointId) {
		return new AdoptedCustomWaypointKey(createdPlayer, waypointId);
	}

	public static AdoptedCustomWaypointKey read(PacketBuffer buf) {
		UUID createdPlayer = buf.readUUID();
		int waypointId = buf.readVarInt();
		return of(createdPlayer, waypointId);
	}
}
