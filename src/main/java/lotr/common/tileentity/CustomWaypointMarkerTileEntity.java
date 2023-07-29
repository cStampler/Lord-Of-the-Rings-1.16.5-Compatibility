package lotr.common.tileentity;

import java.util.UUID;

import lotr.common.block.CustomWaypointMarkerBlock;
import lotr.common.data.DataUtil;
import lotr.common.init.LOTRTileEntities;
import lotr.common.world.map.*;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemFrameEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;

public class CustomWaypointMarkerTileEntity extends TileEntity {
	private UUID playerUuid;
	private int waypointId;
	private String savedWaypointName;
	private boolean savedWaypointPublic;
	private CompoundNBT itemFrameNBT;

	public CustomWaypointMarkerTileEntity() {
		super((TileEntityType) LOTRTileEntities.CUSTOM_WAYPOINT_MARKER.get());
	}

	public void absorbItemFrame(ItemFrameEntity itemFrame) {
		itemFrameNBT = itemFrame.saveWithoutId(new CompoundNBT());
		itemFrame.remove();
	}

	public Direction getFacingDirection() {
		return getBlockState().getValue(CustomWaypointMarkerBlock.FACING);
	}

	@Override
	public SUpdateTileEntityPacket getUpdatePacket() {
		CompoundNBT nbt = new CompoundNBT();
		writeWaypointNameForClient(nbt);
		return new SUpdateTileEntityPacket(worldPosition, 0, nbt);
	}

	@Override
	public CompoundNBT getUpdateTag() {
		CompoundNBT nbt = super.getUpdateTag();
		writeWaypointNameForClient(nbt);
		return nbt;
	}

	public int getWaypointId() {
		return waypointId;
	}

	public String getWaypointName() {
		return savedWaypointName;
	}

	public UUID getWaypointPlayer() {
		return playerUuid;
	}

	public boolean isWaypointPublic() {
		return savedWaypointPublic;
	}

	@Override
	public void load(BlockState state, CompoundNBT nbt) {
		super.load(state, nbt);
		if (DataUtil.hasUniqueIdBackCompat(nbt, "WaypointCreator")) {
			playerUuid = DataUtil.getUniqueIdBackCompat(nbt, "WaypointCreator");
			waypointId = nbt.getInt("WaypointID");
		}

		if (nbt.contains("WaypointNameSaved")) {
			savedWaypointName = nbt.getString("WaypointNameSaved");
		}

		savedWaypointPublic = nbt.getBoolean("WaypointPublicSaved");
		itemFrameNBT = nbt.getCompound("ItemFrameData");
	}

	public boolean matchesWaypointReference(AbstractCustomWaypoint waypoint) {
		return waypoint.getCreatedPlayer().equals(playerUuid) && waypoint.getCustomId() == waypointId;
	}

	@Override
	public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
		CompoundNBT nbt = pkt.getTag();
		if (nbt.contains("WaypointNameSaved")) {
			savedWaypointName = nbt.getString("WaypointNameSaved");
		} else {
			savedWaypointName = null;
		}

	}

	public void recreateAndDropItemFrame(BlockState oldState) {
		Direction facing = oldState.getValue(CustomWaypointMarkerBlock.FACING);
		ItemFrameEntity itemFrame = new ItemFrameEntity(level, worldPosition, facing);
		itemFrame.load(itemFrameNBT);
		itemFrame.dropItem((Entity) null);
		itemFrameNBT = null;
	}

	@Override
	public CompoundNBT save(CompoundNBT nbt) {
		super.save(nbt);
		if (playerUuid != null) {
			nbt.putUUID("WaypointCreator", playerUuid);
			nbt.putInt("WaypointID", waypointId);
		}

		if (savedWaypointName != null) {
			nbt.putString("WaypointNameSaved", savedWaypointName);
		}

		nbt.putBoolean("WaypointPublicSaved", savedWaypointPublic);
		if (itemFrameNBT != null) {
			nbt.put("ItemFrameData", itemFrameNBT.copy());
		}

		return nbt;
	}

	public void setWaypointReference(CustomWaypoint waypoint) {
		playerUuid = waypoint.getCreatedPlayer();
		waypointId = waypoint.getCustomId();
		savedWaypointName = waypoint.getRawName();
		savedWaypointPublic = waypoint.isPublic();
	}

	public void updateWaypointReference(CustomWaypoint waypoint) {
		savedWaypointName = waypoint.getRawName();
		savedWaypointPublic = waypoint.isPublic();
		getLevel().sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
	}

	private void writeWaypointNameForClient(CompoundNBT nbt) {
		if (savedWaypointName != null) {
			nbt.putString("WaypointNameSaved", savedWaypointName);
		}

	}
}
