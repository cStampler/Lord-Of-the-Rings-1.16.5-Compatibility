package lotr.common.world.map;

import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;

import com.google.common.collect.ImmutableList;
import com.google.common.math.IntMath;
import com.google.gson.*;

import lotr.common.*;
import lotr.common.init.LOTRBiomes;
import lotr.common.util.LOTRUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.resources.*;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.LogicalSide;

public class MapSettings {
	public static final ResourceLocation MAP_SETTINGS_PATH = new ResourceLocation("lotr", "map/map.json");
	public static final ResourceLocation DEFAULT_MAP_IMAGE_PATH = new ResourceLocation("lotr", "map/middle_earth.png");
	public static final ResourceLocation MENU_WAYPOINT_ROUTE_PATH = new ResourceLocation("lotr", "map/menu_waypoint_route.json");
	private final MapSettingsManager manager;
	private final ResourceLocation mapImagePath;
	private int imageWidth;
	private int imageHeight;
	private byte[] cachedImageBytes;
	private int[] deferredImageRgb;
	private int[] imageBiomeIds;
	private int fallbackBiomeId;
	private BiomeMapColorTable biomeColorTable;
	private final int originX;
	private final int originZ;
	private final int scalePower;
	private final int scaleFactor;
	private final String title;
	private final boolean translateTitle;
	private final Set lockSides;
	private final boolean proceduralRivers;
	private BothWaterLatitudeSettings waterLatitudes;
	private NorthernLightsSettings northernLights;
	private List waypointRegions;
	private Map waypointRegionsById;
	private Map waypointRegionsByName;
	private Map waypointRegionsForBiome;
	private List waypoints;
	private Map waypointsById;
	private Map waypointsByName;
	private List menuWaypointRoute;
	private List roads;
	private final RoadPointCache roadPointCache = new RoadPointCache();
	private List labels;

	public MapSettings(MapSettingsManager mgr, ResourceLocation map, int origX, int origZ, int scale, String ttl, boolean translate, Set locks, boolean rivers) {
		manager = mgr;
		mapImagePath = map;
		originX = origX;
		originZ = origZ;
		scalePower = scale;
		scaleFactor = IntMath.pow(2, scalePower);
		title = ttl;
		translateTitle = translate;
		lockSides = locks;
		proceduralRivers = rivers;
	}

	public void copyImageBytesFrom(MapSettings other) {
		setImageBytes(other.cachedImageBytes);
	}

	public InputStream createCachedImageInputStream() {
		return new ByteArrayInputStream(cachedImageBytes);
	}

	public BiomeMapColorTable getBiomeColorTable() {
		return biomeColorTable;
	}

	public int getBiomeIdAt(int mapX, int mapZ, IWorld world) {
		if (imageBiomeIds == null) {
			loadImageBiomeIds(world);
		}

		if (mapX >= 0 && mapX < imageWidth && mapZ >= 0 && mapZ < imageHeight) {
			int index = mapZ * imageWidth + mapX;
			return imageBiomeIds[index];
		}
		return fallbackBiomeId;
	}

	public int getHeight() {
		return imageHeight;
	}

	public List getLabels() {
		return labels;
	}

	public ResourceLocation getMapImagePath() {
		return mapImagePath;
	}

	public List getMenuWaypointRoute() {
		return menuWaypointRoute;
	}

	public NorthernLightsSettings getNorthernLights() {
		return northernLights;
	}

	public int getNumBiomesMappedToWaypointRegions() {
		return waypointRegionsForBiome.size();
	}

	public int getOriginX() {
		return originX;
	}

	public int getOriginZ() {
		return originZ;
	}

	public boolean getProceduralRivers() {
		return proceduralRivers;
	}

	public RoadPointCache getRoadPointCache() {
		return roadPointCache;
	}

	public List getRoads() {
		return roads;
	}

	public int getScaleFactor() {
		return scaleFactor;
	}

	public int getScalePower() {
		return scalePower;
	}

	public LogicalSide getSide() {
		return manager.getSide();
	}

	public ITextComponent getTitle() {
		return translateTitle ? new TranslationTextComponent(title) : new StringTextComponent(title);
	}

	public BothWaterLatitudeSettings getWaterLatitudes() {
		return waterLatitudes;
	}

	public MapWaypoint getWaypointByID(int id) {
		return (MapWaypoint) waypointsById.get(id);
	}

	public MapWaypoint getWaypointByName(ResourceLocation name) {
		return (MapWaypoint) waypointsByName.get(name);
	}

	public WaypointRegion getWaypointRegionByID(int id) {
		return (WaypointRegion) waypointRegionsById.get(id);
	}

	public WaypointRegion getWaypointRegionByName(ResourceLocation name) {
		return (WaypointRegion) waypointRegionsByName.get(name);
	}

	public List getWaypointRegionNames() {
		return (List) waypointRegions.stream().map(hummel -> ((WaypointRegion) hummel).getName()).collect(Collectors.toList());
	}

	public List getWaypointRegions() {
		return waypointRegions;
	}

	public List getWaypointRegionsForBiome(Biome biome, IWorld world) {
		ResourceLocation biomeName = LOTRBiomes.getBiomeRegistryName(biome, world);
		return (List) waypointRegionsForBiome.getOrDefault(biomeName, ImmutableList.of());
	}

	public List getWaypoints() {
		return waypoints;
	}

	public int getWidth() {
		return imageWidth;
	}

	public boolean isDefaultImage() {
		return mapImagePath.equals(DEFAULT_MAP_IMAGE_PATH);
	}

	public boolean isScreenSideLocked(Direction dir) {
		return lockSides.contains(dir);
	}

	public boolean loadedImage() {
		return cachedImageBytes != null && imageBiomeIds != null;
	}

	public void loadImage(IResourceManager resMgr) {
		if (!loadedImage()) {
			try {
				if (cachedImageBytes == null) {
					InputStream is;
					if (isDefaultImage()) {
						is = LOTRMod.getDefaultModResourceStream(ResourcePackType.SERVER_DATA, mapImagePath);
					} else {
						is = resMgr.getResource(mapImagePath).getInputStream();
					}

					cachedImageBytes = readInputStreamBytesFully(is);
				}

				BufferedImage biomeImage = ImageIO.read(createCachedImageInputStream());
				if (biomeImage == null) {
					throw new RuntimeException("Fatal error: Could not load LOTR biome map image " + mapImagePath);
				}

				imageWidth = biomeImage.getWidth();
				imageHeight = biomeImage.getHeight();
				deferredImageRgb = biomeImage.getRGB(0, 0, imageWidth, imageHeight, (int[]) null, 0, imageWidth);
			} catch (IOException var3) {
				var3.printStackTrace();
			}
		}

	}

	private void loadImageBiomeIds(IWorld world) {
		if (imageBiomeIds != null) {
			throw new IllegalStateException("Cannot load image biome IDs again - already loaded");
		}
		if (deferredImageRgb == null) {
			throw new IllegalStateException("Cannot load image biome IDs - map image file has not yet been loaded");
		}
		int[] mapRgb = deferredImageRgb;
		deferredImageRgb = null;
		imageBiomeIds = new int[imageWidth * imageHeight];
		fallbackBiomeId = LOTRBiomes.getBiomeID(LOTRBiomes.SEA, world);
		Map cachedBiomeIDs = new HashMap();
		Set unknownColorCache = new HashSet();

		for (int i = 0; i < mapRgb.length; ++i) {
			int color = mapRgb[i] & 16777215;
			Integer biomeId = (Integer) cachedBiomeIDs.get(color);
			if (biomeId == null) {
				Biome biome = biomeColorTable.getBiome(color, world);
				if (biome != null) {
					biomeId = LOTRBiomes.getBiomeID(biome, world);
				} else {
					biomeId = fallbackBiomeId;
					if (!unknownColorCache.contains(color)) {
						unknownColorCache.add(color);
						LOTRLog.error("Found unknown biome color on map: %s, substituting sea", LOTRUtil.toPaddedHexString(color));
					}
				}

				cachedBiomeIDs.put(color, biomeId);
			}

			imageBiomeIds[i] = biomeId;
		}
	}

	public int mapToWorldDistance(double dist) {
		return (int) Math.round(dist * scaleFactor);
	}

	public int mapToWorldX(double x) {
		return (int) Math.round(mapToWorldX_frac(x));
	}

	public double mapToWorldX_frac(double x) {
		return (x - originX) * scaleFactor;
	}

	public int mapToWorldZ(double z) {
		return (int) Math.round(mapToWorldZ_frac(z));
	}

	public double mapToWorldZ_frac(double z) {
		return (z - originZ) * scaleFactor;
	}

	public void postLoadValidateBiomes(World world) {
		waypointRegions.forEach(region -> {
			((WaypointRegion) region).postLoadValidateBiomes(world);
		});
	}

	public void readMenuWaypointRoute(JsonObject json) {
		if (waypoints == null) {
			LOTRLog.error("Cannot load menu waypoint route - waypoints aren't loaded yet!");
		}

		JsonArray wpList = json.get("waypoint_route").getAsJsonArray();
		menuWaypointRoute = new ArrayList();
		for (JsonElement elem : wpList) {
			try {
				String wpName = elem.getAsString();
				ResourceLocation wpRes = new ResourceLocation(wpName);
				MapWaypoint waypoint = (MapWaypoint) waypointsByName.get(wpRes);
				if (waypoint != null) {
					menuWaypointRoute.add(waypoint);
				} else {
					LOTRLog.warn("Tried to add a map waypoint to the menu route that doesn't exist for this map - name %s. Check the route list", wpRes);
				}
			} catch (Exception var8) {
				LOTRLog.warn("Invalid array element '%s' in menu waypoint route json. Must be a list of waypoints by their resource location names. See the mod's default file for an example", elem.toString());
				var8.printStackTrace();
			}
		}

	}

	public void setBiomeColorTable(BiomeMapColorTable table) {
		if (biomeColorTable != null) {
			throw new IllegalArgumentException("Cannot set map's biome color table - already set!");
		}
		biomeColorTable = table;
	}

	public void setImageBytes(byte[] bytes) {
		if (cachedImageBytes != null) {
			throw new IllegalArgumentException("Map's cachedImageBytes are already set, cannot replace them!");
		}
		cachedImageBytes = bytes;
	}

	public void setLabels(List lbls) {
		if (labels != null) {
			throw new IllegalArgumentException("Cannot set map's labels - already set!");
		}
		labels = lbls;
	}

	public void setNorthernLights(NorthernLightsSettings nls) {
		if (northernLights != null) {
			throw new IllegalArgumentException("Cannot set map's northern lights - already set!");
		}
		northernLights = nls;
	}

	private void setRoads(List inputRoads) {
		if (roads != null) {
			throw new IllegalArgumentException("Cannot set map's roads - already set!");
		}
		roads = inputRoads;
	}

	public void setRoadsAndGenerateCurvesInstantly(List inputRoads) {
		setRoads(inputRoads);
		Iterator var2 = roads.iterator();

		while (var2.hasNext()) {
			Road road = (Road) var2.next();
			road.generateCurves();
		}

	}

	public void setRoadsAndGenerateCurvesOnThread(List inputRoads) {
		setRoads(inputRoads);
		Thread roadCurveThread = new Thread(() -> {
			Iterator var1 = roads.iterator();

			while (var1.hasNext()) {
				Road road = (Road) var1.next();
				road.generateCurves();
			}

		}, "Road curve generator clientside thread");
		roadCurveThread.start();
	}

	public void setWaterLatitudes(BothWaterLatitudeSettings water) {
		if (waterLatitudes != null) {
			throw new IllegalArgumentException("Cannot set map's water latitudes - already set!");
		}
		waterLatitudes = water;
	}

	public void setWaypointRegions(List regions) {
		if (waypointRegions != null) {
			throw new IllegalArgumentException("Cannot set map's waypoint regions - already set!");
		}
		waypointRegions = regions;
		waypointRegionsById = (Map) waypointRegions.stream().collect(Collectors.toMap(WaypointRegion::getAssignedId, UnaryOperator.identity()));
		waypointRegionsByName = (Map) waypointRegions.stream().collect(Collectors.toMap(WaypointRegion::getName, UnaryOperator.identity()));
		waypointRegionsForBiome = new HashMap();
		waypointRegions.forEach(region -> {
			((WaypointRegion) region).getBiomeNames().forEach(biomeName -> {
				((List) waypointRegionsForBiome.computeIfAbsent(biomeName, b -> new ArrayList())).add(region);
			});
		});
	}

	public void setWaypoints(List wps) {
		if (waypoints != null) {
			throw new IllegalArgumentException("Cannot set map's waypoints - already set!");
		}
		waypoints = wps;
		waypointsById = (Map) waypoints.stream().collect(Collectors.toMap(MapWaypoint::getAssignedId, UnaryOperator.identity()));
		waypointsByName = (Map) waypoints.stream().collect(Collectors.toMap(MapWaypoint::getName, UnaryOperator.identity()));
	}

	public int worldToMapDistance(double dist) {
		return (int) Math.round(dist / scaleFactor);
	}

	public int worldToMapX(double x) {
		return (int) Math.round(worldToMapX_frac(x));
	}

	public double worldToMapX_frac(double x) {
		return x / scaleFactor + originX;
	}

	public int worldToMapZ(double z) {
		return (int) Math.round(worldToMapZ_frac(z));
	}

	public double worldToMapZ_frac(double z) {
		return z / scaleFactor + originZ;
	}

	public void write(PacketBuffer buf) {
		buf.writeUtf(mapImagePath.toString());
		buf.writeInt(originX);
		buf.writeInt(originZ);
		buf.writeByte(scalePower);
		buf.writeUtf(title);
		buf.writeBoolean(translateTitle);
		buf.writeByte(lockSides.size());
		lockSides.forEach(dir -> {
			buf.writeByte(((Direction) dir).get3DDataValue());
		});
		buf.writeBoolean(proceduralRivers);
		if (!isDefaultImage()) {
			buf.writeByteArray(cachedImageBytes);
		}

		biomeColorTable.write(buf);
		waterLatitudes.write(buf);
		northernLights.write(buf);
		buf.writeInt(waypointRegions.size());
		waypointRegions.forEach(region -> {
			((WaypointRegion) region).write(buf);
		});
		buf.writeInt(waypoints.size());
		waypoints.forEach(waypoint -> {
			((MapWaypoint) waypoint).write(buf);
		});
		buf.writeInt(menuWaypointRoute.size());
		menuWaypointRoute.forEach(waypoint -> {
			buf.writeVarInt(((MapWaypoint) waypoint).getAssignedId());
		});
		buf.writeInt(roads.size());
		roads.forEach(road -> {
			((Road) road).write(buf);
		});
		buf.writeInt(labels.size());
		labels.forEach(label -> {
			((MapLabel) label).write(buf);
		});
	}

	public static MapSettings read(MapSettingsManager manager, JsonObject json) {
		String imagePath = json.get("image").getAsString();
		int originX = json.get("origin_x").getAsInt();
		int originZ = json.get("origin_z").getAsInt();
		int scale = json.get("scale_power").getAsInt();
		int clampedScale = MathHelper.clamp(scale, 2, 10);
		if (clampedScale != scale) {
			scale = clampedScale;
			LOTRLog.warn("Map scale power must be between %d and %d - clamping value provided in map.json to %d", 2, 10, clampedScale);
		}

		String title = json.get("title").getAsString();
		boolean translateTitle = json.get("translate_title").getAsBoolean();
		Set lockSides = new HashSet();
		json.get("lock_sides").getAsJsonArray().forEach(jsonElem -> {
			String dirName = jsonElem.getAsString();
			Direction dir = Direction.byName(dirName);
			if (dir != null && dir.getAxis().isHorizontal()) {
				lockSides.add(dir);
			} else {
				LOTRLog.warn("Invalid direction '%s' for map locked sides", dirName);
			}

		});
		boolean proceduralRivers = true;
		if (json.has("procedural_rivers")) {
			proceduralRivers = json.get("procedural_rivers").getAsBoolean();
		}

		return new MapSettings(manager, new ResourceLocation(imagePath), originX, originZ, scale, title, translateTitle, lockSides, proceduralRivers);
	}

	public static MapSettings read(MapSettingsManager manager, PacketBuffer buf) {
		String imagePath = buf.readUtf();
		int originX = buf.readInt();
		int originZ = buf.readInt();
		int scale = buf.readByte();
		String title = buf.readUtf();
		boolean translateTitle = buf.readBoolean();
		Set lockSides = new HashSet();
		int numLockSides = buf.readByte();

		for (int i = 0; i < numLockSides; ++i) {
			int dirIndex = buf.readByte();
			Direction dir = Direction.from3DDataValue(dirIndex);
			if (dir != null && dir.getAxis().isHorizontal()) {
				lockSides.add(dir);
			} else {
				LOTRLog.warn("Invalid direction index %d for map locked sides", Integer.valueOf(dirIndex));
			}
		}

		boolean proceduralRivers = buf.readBoolean();
		MapSettings mapSettings = new MapSettings(manager, new ResourceLocation(imagePath), originX, originZ, scale, title, translateTitle, lockSides, proceduralRivers);
		if (!mapSettings.isDefaultImage()) {
			byte[] imgBytes = buf.readByteArray();
			mapSettings.setImageBytes(imgBytes);
		}

		mapSettings.setBiomeColorTable(BiomeMapColorTable.read(buf));
		mapSettings.setWaterLatitudes(BothWaterLatitudeSettings.read(mapSettings, buf));
		mapSettings.setNorthernLights(NorthernLightsSettings.read(mapSettings, buf));
		List waypointRegions = new ArrayList();
		int numWaypointRegions = buf.readInt();

		for (int i = 0; i < numWaypointRegions; ++i) {
			try {
				WaypointRegion region = WaypointRegion.read(buf);
				waypointRegions.add(region);
			} catch (Exception var27) {
				LOTRLog.warn("Error loading a waypoint region from server");
				var27.printStackTrace();
			}
		}

		mapSettings.setWaypointRegions(waypointRegions);
		List waypoints = new ArrayList();
		int numWaypoints = buf.readInt();

		for (int i = 0; i < numWaypoints; ++i) {
			try {
				MapWaypoint waypoint = MapWaypoint.read(mapSettings, buf);
				waypoints.add(waypoint);
			} catch (Exception var26) {
				LOTRLog.warn("Error loading a map waypoint from server");
				var26.printStackTrace();
			}
		}

		mapSettings.setWaypoints(waypoints);
		List menuWaypointRoute = new ArrayList();
		int numMenuWaypoints = buf.readInt();

		int numRoads;
		for (int i = 0; i < numMenuWaypoints; ++i) {
			numRoads = buf.readVarInt();
			MapWaypoint waypoint = (MapWaypoint) mapSettings.waypointsById.get(numRoads);
			if (waypoint != null) {
				menuWaypointRoute.add(waypoint);
			} else {
				LOTRLog.error("Tried to add a map waypoint to the menu route that doesn't exist - assigned ID %d. Something has broken!", numRoads);
			}
		}

		mapSettings.menuWaypointRoute = menuWaypointRoute;
		List roads = new ArrayList();
		numRoads = buf.readInt();

		for (int i = 0; i < numRoads; ++i) {
			try {
				Road road = Road.read(mapSettings, buf);
				roads.add(road);
			} catch (Exception var25) {
				LOTRLog.warn("Error loading a map road from server");
				var25.printStackTrace();
			}
		}

		mapSettings.setRoadsAndGenerateCurvesOnThread(roads);
		List labels = new ArrayList();
		int numLabels = buf.readInt();

		for (int i = 0; i < numLabels; ++i) {
			try {
				MapLabel label = MapLabel.read(mapSettings, buf);
				labels.add(label);
			} catch (Exception var24) {
				LOTRLog.warn("Error loading a map label from server");
				var24.printStackTrace();
			}
		}

		mapSettings.setLabels(labels);
		return mapSettings;
	}

	private static byte[] readInputStreamBytesFully(InputStream is) throws IOException {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		byte[] data = new byte[16384];

		int nRead;
		while ((nRead = is.read(data, 0, data.length)) != -1) {
			buffer.write(data, 0, nRead);
		}

		return buffer.toByteArray();
	}
}
