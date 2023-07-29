package lotr.common.world.map;

import lotr.common.network.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.World;

public class MapMarker implements SelectableMapObject {
	public static final int MAX_NAME_LENGTH = 32;
	public static final int ICON_SIZE = 10;
	private final MapSettings mapSettings;
	private final int id;
	private final int worldX;
	private final int worldZ;
	private final double mapX;
	private final double mapZ;
	private String name;
	private MapMarkerIcon icon;

	public MapMarker(MapSettings map, int id, int worldX, int worldZ, String name, MapMarkerIcon icon) {
		mapSettings = map;
		this.id = id;
		this.worldX = worldX;
		this.worldZ = worldZ;
		mapX = mapSettings.worldToMapX_frac(worldX);
		mapZ = mapSettings.worldToMapZ_frac(worldZ);
		this.name = name;
		this.icon = icon;
	}

	public void changeIconAndSendToServer(MapMarkerIcon newIcon) {
		icon = newIcon;
		LOTRPacketHandler.sendToServer(new CPacketUpdateMapMarker(this));
	}

	public MapMarkerIcon getIcon() {
		return icon;
	}

	public int getId() {
		return id;
	}

	@Override
	public int getMapIconWidth() {
		return 10;
	}

	public double getMapX() {
		return mapX;
	}

	public double getMapZ() {
		return mapZ;
	}

	public String getName() {
		return name;
	}

	@Override
	public int getWorldX() {
		return worldX;
	}

	@Override
	public int getWorldZ() {
		return worldZ;
	}

	public void renameAndSendToServer(String newName) {
		name = newName;
		LOTRPacketHandler.sendToServer(new CPacketUpdateMapMarker(this));
	}

	public void save(CompoundNBT nbt) {
		nbt.putInt("ID", id);
		nbt.putInt("X", worldX);
		nbt.putInt("Z", worldZ);
		nbt.putString("Name", name);
		nbt.putString("Icon", icon.name);
	}

	public void update(String newName, MapMarkerIcon newIcon) {
		name = newName;
		icon = newIcon;
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(id);
		buf.writeInt(worldX);
		buf.writeInt(worldZ);
		buf.writeUtf(name);
		buf.writeVarInt(icon.networkId);
	}

	public static boolean isValidMapMarkerPosition(MapSettings currentMap, int worldX, int worldZ) {
		double mapX = currentMap.worldToMapX_frac(worldX);
		double mapZ = currentMap.worldToMapZ_frac(worldZ);
		return mapX >= 0.0D && mapX < currentMap.getWidth() && mapZ >= 0.0D && mapZ < currentMap.getHeight();
	}

	public static boolean isValidMapMarkerPosition(World world, int worldX, int worldZ) {
		MapSettings currentMap = MapSettingsManager.sidedInstance(world).getCurrentLoadedMap();
		return isValidMapMarkerPosition(currentMap, worldX, worldZ);
	}

	public static MapMarker load(MapSettings map, CompoundNBT nbt) {
		int id = nbt.getInt("ID");
		int worldX = nbt.getInt("X");
		int worldZ = nbt.getInt("Z");
		String name = nbt.getString("Name");
		MapMarkerIcon icon = MapMarkerIcon.forNameOrDefault(nbt.getString("Icon"));
		return new MapMarker(map, id, worldX, worldZ, name, icon);
	}

	public static MapMarker read(MapSettings map, PacketBuffer buf) {
		int id = buf.readVarInt();
		int worldX = buf.readInt();
		int worldZ = buf.readInt();
		String name = buf.readUtf();
		MapMarkerIcon icon = MapMarkerIcon.forNetworkIdOrDefault(buf.readVarInt());
		return new MapMarker(map, id, worldX, worldZ, name, icon);
	}
}
