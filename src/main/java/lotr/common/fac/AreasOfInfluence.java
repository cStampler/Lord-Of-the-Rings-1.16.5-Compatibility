package lotr.common.fac;

import java.util.*;

import com.google.common.collect.ImmutableList;
import com.google.gson.*;

import lotr.common.config.*;
import lotr.common.entity.npc.NPCPredicates;
import lotr.common.init.LOTRWorldTypes;
import lotr.common.world.map.MapSettings;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.*;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

public class AreasOfInfluence {
	public static final int DEFAULT_REDUCED_INFLUENCE_RANGE = 50;
	private final MapSettings mapSettings;
	private final Faction theFaction;
	private final boolean isolationist;
	private final List areas;
	private AreaBorders calculatedAreaBorders = null;

	public AreasOfInfluence(MapSettings map, Faction theFaction, boolean isolationist, List areas) {
		mapSettings = map;
		this.theFaction = theFaction;
		this.isolationist = isolationist;
		this.areas = areas;
	}

	public AreaBorders calculateAreaOfInfluenceBordersIncludingReduced() {
		if (calculatedAreaBorders == null) {
			double xMin = 0.0D;
			double xMax = 0.0D;
			double zMin = 0.0D;
			double zMax = 0.0D;
			boolean first = true;
			Iterator var10 = areas.iterator();

			while (var10.hasNext()) {
				AreaOfInfluence area = (AreaOfInfluence) var10.next();
				double cxMin = area.getWorldX() - area.getWorldRadius();
				double cxMax = area.getWorldX() + area.getWorldRadius();
				double czMin = area.getWorldZ() - area.getWorldRadius();
				double czMax = area.getWorldZ() + area.getWorldRadius();
				if (first) {
					xMin = cxMin;
					xMax = cxMax;
					zMin = czMin;
					zMax = czMax;
					first = false;
				} else {
					xMin = Math.min(xMin, cxMin);
					xMax = Math.max(xMax, cxMax);
					zMin = Math.min(zMin, czMin);
					zMax = Math.max(zMax, czMax);
				}
			}

			int reducedWorldRange = mapSettings.mapToWorldDistance(getReducedInfluenceRange());
			xMin -= reducedWorldRange;
			xMax += reducedWorldRange;
			zMin -= reducedWorldRange;
			zMax += reducedWorldRange;
			calculatedAreaBorders = new AreaBorders(xMin, xMax, zMin, zMax);
		}

		return calculatedAreaBorders;
	}

	public double distanceToNearestAreaInRange(World world, double x, double y, double z, int mapRange) {
		double closestDist = -1.0D;
		if (!isFactionDimension(world)) {
			return closestDist;
		}
		int coordRange = mapSettings.mapToWorldDistance(mapRange);
		Iterator var12 = areas.iterator();

		while (true) {
			double dToEdge;
			do {
				do {
					if (!var12.hasNext()) {
						return closestDist;
					}

					AreaOfInfluence area = (AreaOfInfluence) var12.next();
					double dx = x - area.getWorldX();
					double dz = z - area.getWorldZ();
					double dSq = dx * dx + dz * dz;
					dToEdge = Math.sqrt(dSq) - area.getWorldRadius();
				} while (dToEdge > coordRange);
			} while (closestDist >= 0.0D && dToEdge >= closestDist);

			closestDist = dToEdge;
		}
	}

	public float getAlignmentMultiplier(PlayerEntity player) {
		if (this.isInArea(player)) {
			return 1.0F;
		}
		if (isFactionDimension(player.level)) {
			int reducedRange = getReducedInfluenceRange();
			double dist = distanceToNearestAreaInRange(player.level, player.getX(), player.getY(), player.getZ(), reducedRange);
			if (dist >= 0.0D) {
				double mapDist = mapSettings.worldToMapDistance(dist);
				float frac = (float) mapDist / reducedRange;
				float mplier = 1.0F - frac;
				return MathHelper.clamp(mplier, 0.0F, 1.0F);
			}
		}

		return 0.0F;
	}

	public List getAreas() {
		return areas;
	}

	public int getReducedInfluenceRange() {
		return isolationist ? 0 : 50;
	}

	private boolean isFactionDimension(World world) {
		return world.dimension().location().equals(theFaction.getDimension().location());
	}

	public boolean isInArea(PlayerEntity player) {
		return this.isInArea(player.level, player.getX(), player.getY(), player.getZ());
	}

	public boolean isInArea(World world, double x, double y, double z) {
		if (this.isInDefinedArea(world, x, y, z)) {
			return true;
		}
		if (theFaction.isPlayableAlignmentFaction()) {
			AxisAlignedBB aabb = AxisAlignedBB.unitCubeFromLowerCorner(new Vector3d(x, y, z)).inflate(24.0D);
			List nearbyNPCs = world.getLoadedEntitiesOfClass(LivingEntity.class, aabb, NPCPredicates.selectForLocalAreaOfInfluence(theFaction));
			if (!nearbyNPCs.isEmpty()) {
				return true;
			}
		}

		return false;
	}

	public boolean isInAreaToRecordBountyKill(PlayerEntity player) {
		return this.isInDefinedArea(player, Math.max(getReducedInfluenceRange(), 50));
	}

	public boolean isInDefinedArea(PlayerEntity player) {
		return this.isInDefinedArea(player, 0);
	}

	public boolean isInDefinedArea(PlayerEntity player, int extraMapRange) {
		return this.isInDefinedArea(player.level, player.getX(), player.getY(), player.getZ(), extraMapRange);
	}

	public boolean isInDefinedArea(World world, double x, double y, double z) {
		return this.isInDefinedArea(world, x, y, z, 0);
	}

	public boolean isInDefinedArea(World world, double x, double y, double z, int extraMapRange) {
		if (!isFactionDimension(world)) {
			return false;
		}
		return !areAreasOfInfluenceEnabled(world) ? true : areas.stream().anyMatch(area -> ((AreaOfInfluence) area).isInArea(x, y, z, extraMapRange));
	}

	public boolean isIsolationist() {
		return isolationist;
	}

	public boolean sharesAreaWith(Faction other) {
		return this.sharesAreaWith(other, 0);
	}

	public boolean sharesAreaWith(Faction other, int extraMapRadius) {
		if (!theFaction.isSameDimension(other)) {
			return false;
		}
		List otherAreas = other.getAreasOfInfluence().getAreas();
		return areas.stream().anyMatch(area -> otherAreas.stream().anyMatch(otherArea -> ((AreaOfInfluence) area).intersectsWith((AreaOfInfluence) otherArea, extraMapRadius)));
	}

	public void write(PacketBuffer buf) {
		buf.writeBoolean(isolationist);
		buf.writeVarInt(areas.size());
		areas.forEach(area -> {
			((AreaOfInfluence) area).write(buf);
		});
	}

	public static boolean areAreasOfInfluenceEnabled(World world) {
		if (world instanceof ServerWorld) {
			ServerWorld sWorld = (ServerWorld) world;
			return (Boolean) LOTRConfig.COMMON.areasOfInfluence.get() && LOTRWorldTypes.hasMapFeatures(sWorld);
		}
		return ClientsideCurrentServerConfigSettings.INSTANCE.areasOfInfluence && LOTRWorldTypes.hasMapFeaturesClientside();
	}

	public static final AreasOfInfluence makeEmptyAreas(MapSettings map, Faction fac) {
		return new AreasOfInfluence(map, fac, false, ImmutableList.of());
	}

	public static AreasOfInfluence read(Faction theFaction, JsonObject json, MapSettings mapSettings) {
		boolean isolationist = json.get("isolationist").getAsBoolean();
		JsonArray areasArray = json.get("areas").getAsJsonArray();
		List areas = new ArrayList();
		for (JsonElement areaElement : areasArray) {
			AreaOfInfluence area = AreaOfInfluence.read(mapSettings, theFaction.getName(), areaElement.getAsJsonObject());
			if (area != null) {
				areas.add(area);
			}
		}

		return new AreasOfInfluence(mapSettings, theFaction, isolationist, areas);
	}

	public static AreasOfInfluence read(Faction theFaction, PacketBuffer buf, MapSettings mapSettings) {
		boolean isolationist = buf.readBoolean();
		List areas = new ArrayList();
		int numAreasOfInfluence = buf.readVarInt();

		for (int i = 0; i < numAreasOfInfluence; ++i) {
			AreaOfInfluence area = AreaOfInfluence.read(mapSettings, theFaction.getName(), buf);
			if (area != null) {
				areas.add(area);
			}
		}

		return new AreasOfInfluence(mapSettings, theFaction, isolationist, areas);
	}
}
