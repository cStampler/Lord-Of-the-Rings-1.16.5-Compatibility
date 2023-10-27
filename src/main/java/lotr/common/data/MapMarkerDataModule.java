package lotr.common.data;

import java.util.List;

import lotr.common.network.CPacketToggle;
import lotr.common.network.LOTRPacketHandler;
import lotr.common.network.SPacketCreateMapMarker;
import lotr.common.network.SPacketDeleteMapMarker;
import lotr.common.network.SPacketToggle;
import lotr.common.network.SPacketUpdateMapMarker;
import lotr.common.network.SidedTogglePacket;
import lotr.common.util.LookupList;
import lotr.common.world.map.MapMarker;
import lotr.common.world.map.MapMarkerIcon;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;

public class MapMarkerDataModule extends PlayerDataModule {
	public static final int MAX_MARKERS = 64;
	private LookupList markers = new LookupList(hummel -> ((MapMarker) hummel).getId());
	private int nextMarkerId = 0;
	private boolean showMarkers = true;

	protected MapMarkerDataModule(LOTRPlayerData pd) {
		super(pd);
	}

	public void addCreatedMarkerFromServer(MapMarker marker) {
		markers.add(marker);
	}

	public boolean canCreateNewMarker() {
		return markers.size() < 64;
	}

	public void createNewMarker(int worldX, int worldZ, String name) {
		MapMarker marker = new MapMarker(currentMapSettings(), nextMarkerId, worldX, worldZ, name, MapMarkerIcon.CROSS);
		++nextMarkerId;
		markers.add(marker);
		markDirty();
		sendPacketToClient(new SPacketCreateMapMarker(marker));
	}

	public MapMarker getMarkerById(int id) {
		return (MapMarker) markers.lookup(id);
	}

	public List getMarkers() {
		return markers;
	}

	public boolean getShowMarkers() {
		return showMarkers;
	}

	@Override
	public void load(CompoundNBT playerNBT) {
		DataUtil.loadCollectionFromCompoundListNBT(markers, playerNBT.getList("MapMarkers", 10), nbt -> MapMarker.load(currentMapSettings(), (CompoundNBT) nbt));
		nextMarkerId = (Integer) DataUtil.getIfNBTContains(nextMarkerId, playerNBT, "NextMapMarkerId", (h1, h2) -> ((CompoundNBT) h1).getInt((String) h2));
		showMarkers = (Boolean) DataUtil.getIfNBTContains(showMarkers, playerNBT, "ShowMapMarkers", (h1, h2) -> ((CompoundNBT) h1).getBoolean((String) h2));
	}

	@Override
	protected void receiveLoginData(PacketBuffer buf) {
		DataUtil.fillCollectionFromBuffer(buf, markers, () -> MapMarker.read(currentMapSettings(), buf));
		nextMarkerId = buf.readVarInt();
		showMarkers = buf.readBoolean();
	}

	public void removeMarker(MapMarker marker) {
		markers.remove(marker);
		markDirty();
		sendPacketToClient(new SPacketDeleteMapMarker(marker));
	}

	@Override
	public void save(CompoundNBT playerNBT) {
		if (!markers.isEmpty()) {
			playerNBT.put("MapMarkers", DataUtil.saveCollectionAsCompoundListNBT(markers, (nbt, marker) -> {
				((MapMarker) marker).save((CompoundNBT) nbt);
			}));
		}

		playerNBT.putInt("NextMapMarkerId", nextMarkerId);
		playerNBT.putBoolean("ShowMapMarkers", showMarkers);
	}

	@Override
	protected void sendLoginData(PacketBuffer buf) {
		DataUtil.writeCollectionToBuffer(buf, markers, marker -> {
			((MapMarker) marker).write(buf);
		});
		buf.writeVarInt(nextMarkerId);
		buf.writeBoolean(showMarkers);
	}

	private void sendShowMarkersToClient() {
		sendPacketToClient(new SPacketToggle(SidedTogglePacket.ToggleType.SHOW_MAP_MARKERS, showMarkers));
	}

	private void sendShowMarkersToServer() {
		LOTRPacketHandler.sendToServer(new CPacketToggle(SidedTogglePacket.ToggleType.SHOW_MAP_MARKERS, showMarkers));
	}

	public void setShowMarkers(boolean flag) {
		if (showMarkers != flag) {
			showMarkers = flag;
			markDirty();
			sendShowMarkersToClient();
		}

	}

	public void toggleShowMarkersAndSendToServer() {
		showMarkers = !showMarkers;
		sendShowMarkersToServer();
	}

	public void updateMarker(MapMarker marker, String name, MapMarkerIcon icon) {
		marker.update(name, icon);
		markDirty();
		sendPacketToClient(new SPacketUpdateMapMarker(marker));
	}
}
