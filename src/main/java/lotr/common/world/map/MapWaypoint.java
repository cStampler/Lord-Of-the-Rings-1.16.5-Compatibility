package lotr.common.world.map;

import javax.annotation.Nullable;

import com.google.gson.JsonObject;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.fac.*;
import lotr.common.util.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.*;
import net.minecraft.world.server.ServerWorld;

public class MapWaypoint implements Waypoint {
	private final ResourceLocation resourceName;
	private final int assignedId;
	private final String name;
	private final boolean translateName;
	private final String lore;
	private final boolean translateLore;
	private final double mapX;
	private final double mapZ;
	private final int worldX;
	private final int worldZ;
	private final WaypointRegion region;
	private final LazyReference travelFaction;

	public MapWaypoint(MapSettings map, ResourceLocation res, int id, String name, boolean translateName, String lore, boolean translateLore, double x, double z, WaypointRegion region, ResourceLocation facName) {
		resourceName = res;
		assignedId = id;
		this.name = name;
		this.translateName = translateName;
		this.lore = lore;
		this.translateLore = translateLore;
		mapX = x;
		mapZ = z;
		worldX = map.mapToWorldX(mapX);
		worldZ = map.mapToWorldZ(mapZ);
		this.region = region;
		travelFaction = facName == null ? null : LazyReference.of(facName, ref -> FactionSettingsManager.sidedInstance(map.getSide()).getCurrentLoadedFactions().getFactionByName((ResourceLocation) ref), unresolvedRef -> {
			LOTRLog.warn("Cannot resolve faction name %s in map waypoint %s - ensure the JSON file is correct", unresolvedRef, resourceName);
		});
	}

	public int getAssignedId() {
		return assignedId;
	}

	@Override
	public ITextComponent getDisplayLore() {
		return translateLore ? new TranslationTextComponent(lore) : new StringTextComponent(lore);
	}

	@Override
	public ITextComponent getDisplayName() {
		return translateName ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	@Override
	@Nullable
	public ITextComponent getDisplayOwnership() {
		return null;
	}

	@Override
	public Waypoint.WaypointDisplayState getDisplayState(@Nullable PlayerEntity player) {
		if (player == null || hasPlayerUnlocked(player)) {
			return Waypoint.WaypointDisplayState.STANDARD;
		}
		return !isCompatibleAlignment(player) ? Waypoint.WaypointDisplayState.STANDARD_LOCKED_TO_ENEMIES : Waypoint.WaypointDisplayState.STANDARD_LOCKED;
	}

	@Override
	public double getMapX() {
		return mapX;
	}

	@Override
	public double getMapZ() {
		return mapZ;
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	@Override
	public WaypointNetworkType getNetworkType() {
		return WaypointNetworkType.MAP;
	}

	@Override
	public ITextComponent getNotUnlockedMessage(PlayerEntity player) {
		return !isCompatibleAlignment(player) ? new TranslationTextComponent("gui.lotr.map.locked.enemy") : new TranslationTextComponent("gui.lotr.map.locked.region");
	}

	@Override
	public String getRawName() {
		return resourceName.toString();
	}

	private Faction getTravelFaction() {
		return travelFaction != null ? (Faction) travelFaction.resolveReference() : null;
	}

	@Override
	@Nullable
	public BlockPos getTravelPosition(ServerWorld world, PlayerEntity player) {
		int worldY = LOTRUtil.forceLoadChunkAndGetTopBlock(world, worldX, worldZ);
		return new BlockPos(worldX, worldY, worldZ);
	}

	public WaypointRegion getTravelRegion() {
		return region;
	}

	@Override
	public int getWorldX() {
		return worldX;
	}

	@Override
	public int getWorldZ() {
		return worldZ;
	}

	@Override
	public boolean hasPlayerUnlocked(PlayerEntity player) {
		LOTRLevelData levelData = LOTRLevelData.sidedInstance(player.level);
		LOTRPlayerData pd = levelData.getData(player);
		return pd.getFastTravelData().isWaypointRegionUnlocked(region) && isCompatibleAlignment(player);
	}

	private boolean isCompatibleAlignment(PlayerEntity player) {
		Faction fac = getTravelFaction();
		if (fac != null) {
			LOTRPlayerData pd = LOTRLevelData.sidedInstance(player.level).getData(player);
			return pd.getAlignmentData().getAlignment(fac) >= 0.0F;
		}
		return true;
	}

	@Override
	public boolean isCustom() {
		return false;
	}

	@Override
	public boolean isSharedCustom() {
		return false;
	}

	@Override
	public boolean isSharedHidden() {
		return false;
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeVarInt(assignedId);
		buf.writeUtf(name);
		buf.writeBoolean(translateName);
		buf.writeUtf(lore);
		buf.writeBoolean(translateLore);
		buf.writeDouble(mapX);
		buf.writeDouble(mapZ);
		buf.writeVarInt(region.getAssignedId());
		boolean hasFaction = travelFaction != null;
		buf.writeBoolean(hasFaction);
		if (hasFaction) {
			buf.writeResourceLocation(travelFaction.getReferenceName());
		}

	}

	public static MapWaypoint read(MapSettings map, PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		int assignedID = buf.readVarInt();
		String name = buf.readUtf();
		boolean translateName = buf.readBoolean();
		String lore = buf.readUtf();
		boolean translateLore = buf.readBoolean();
		double mapX = buf.readDouble();
		double mapZ = buf.readDouble();
		int regionId = buf.readVarInt();
		WaypointRegion region = map.getWaypointRegionByID(regionId);
		if (region == null) {
			LOTRLog.warn("Received waypoint %s from server with a nonexistent waypoint region ID (%d)", resourceName, regionId);
		}

		boolean hasFaction = buf.readBoolean();
		ResourceLocation factionReference = hasFaction ? buf.readResourceLocation() : null;
		return new MapWaypoint(map, resourceName, assignedID, name, translateName, lore, translateLore, mapX, mapZ, region, factionReference);
	}

	public static MapWaypoint read(MapSettings map, ResourceLocation resourceName, JsonObject json, int assignedId) {
		if (json.size() == 0) {
			LOTRLog.info("Map waypoint %s has an empty file - not loading it in this world", resourceName);
			return null;
		}
		JsonObject nameObj = json.get("name").getAsJsonObject();
		String name = nameObj.get("text").getAsString();
		boolean translateName = nameObj.get("translate").getAsBoolean();
		JsonObject loreObj = json.get("lore").getAsJsonObject();
		String lore = loreObj.get("text").getAsString();
		boolean translateLore = loreObj.get("translate").getAsBoolean();
		double mapX = json.get("x").getAsDouble() + 0.5D;
		double mapZ = json.get("z").getAsDouble() + 0.5D;
		String regionName = json.get("travel_region").getAsString();
		WaypointRegion region = map.getWaypointRegionByName(new ResourceLocation(regionName));
		if (region == null) {
			LOTRLog.warn("Map waypoint %s declares unknown region name %s - no such waypoint region exists in this map", resourceName, regionName);
			return null;
		}
		ResourceLocation factionReference = null;
		if (json.has("faction")) {
			String facName = json.get("faction").getAsString();
			if (!facName.isEmpty()) {
				factionReference = new ResourceLocation(facName);
			}
		}

		return new MapWaypoint(map, resourceName, assignedId, name, translateName, lore, translateLore, mapX, mapZ, region, factionReference);
	}

	public static MapWaypoint readFromIdentification(PacketBuffer buf, LOTRPlayerData pd) {
		MapSettings map = MapSettingsManager.sidedInstance(pd.getLogicalSide()).getCurrentLoadedMap();
		int wpId = buf.readVarInt();
		MapWaypoint wp = map.getWaypointByID(wpId);
		if (wp == null) {
			LOTRLog.warn("Received nonexistent map waypoint ID %d from %s", wpId, pd.getLogicalSide());
		}

		return wp;
	}

	public static void writeIdentification(PacketBuffer buf, MapWaypoint wp) {
		buf.writeVarInt(wp.assignedId);
	}
}
