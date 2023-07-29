package lotr.common.fac;

import java.awt.Color;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import com.google.gson.*;

import lotr.common.LOTRLog;
import lotr.common.data.*;
import lotr.common.init.LOTRBiomes;
import lotr.common.world.map.MapSettings;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.*;
import net.minecraft.util.text.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class Faction {
	private final FactionSettings factionSettings;
	private final ResourceLocation resourceName;
	private final int assignedId;
	private final String name;
	private final boolean translateName;
	private final String subtitle;
	private final boolean translateSubtitle;
	private final FactionRegion region;
	private final int ordering;
	private final int color;
	private final float[] colorComponents;
	private final MapSquare mapSquare;
	private final boolean isPlayableAlignmentFaction;
	private final Set types;
	private final boolean approvesCivilianKills;
	private List ranks = new ArrayList();
	private Map ranksById = new HashMap();
	private Map ranksByName = new HashMap();
	private List trueRanksSortedAscending = new ArrayList();
	private List trueRanksSortedDescending = new ArrayList();
	private Optional pledgeRank;
	private AreasOfInfluence areasOfInfluence;
	private List speechbankHomeBiomes;

	public Faction(FactionSettings facSettings, ResourceLocation res, int id, String name, boolean translateName, String subtitle, boolean translateSubtitle, FactionRegion region, int ordering, int color, MapSquare mapSquare, boolean isPlayableAlignmentFaction, Set types, boolean civilianKills) {
		factionSettings = facSettings;
		resourceName = res;
		assignedId = id;
		this.name = name;
		this.translateName = translateName;
		this.subtitle = subtitle;
		this.translateSubtitle = translateSubtitle;
		this.region = region;
		this.ordering = ordering;
		this.color = color;
		colorComponents = new Color(color).getColorComponents((float[]) null);
		this.mapSquare = mapSquare;
		this.isPlayableAlignmentFaction = isPlayableAlignmentFaction;
		this.types = types;
		approvesCivilianKills = civilianKills;
	}

	public boolean approvesCivilianKills() {
		return approvesCivilianKills;
	}

	public AreasOfInfluence getAreasOfInfluence() {
		return areasOfInfluence;
	}

	public int getAssignedId() {
		return assignedId;
	}

	public List getBonusesForKilling() {
		return (List) factionSettings.streamFactionsExcept(this).filter(hummel -> isBadRelation((Faction) hummel)).collect(Collectors.toList());
	}

	public int getColor() {
		return color;
	}

	public float[] getColorComponents() {
		return colorComponents;
	}

	public IFormattableTextComponent getColoredDisplayName() {
		IFormattableTextComponent text = getDisplayName();
		text.withStyle(Style.EMPTY.withColor(net.minecraft.util.text.Color.fromRgb(getColor())));
		return text;
	}

	public List getConquestBoostRelations() {
		return (List) factionSettings.streamFactionsExcept(this).filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).filter(hummel -> isAlly((Faction) hummel)).collect(Collectors.toList());
	}

	public RegistryKey getDimension() {
		return region != null ? region.getDimension() : null;
	}

	public IFormattableTextComponent getDisplayName() {
		return translateName ? new TranslationTextComponent(name) : new StringTextComponent(name);
	}

	public ITextComponent getDisplaySubtitle() {
		return translateSubtitle ? new TranslationTextComponent(subtitle) : new StringTextComponent(subtitle);
	}

	public FactionRank getEnemyRank() {
		return getRankByName("enemy");
	}

	public FactionRank getFirstRank() {
		return !trueRanksSortedAscending.isEmpty() ? (FactionRank) trueRanksSortedAscending.get(0) : getNeutralRank();
	}

	public MapSquare getMapSquare() {
		return mapSquare;
	}

	public ResourceLocation getName() {
		return resourceName;
	}

	public FactionRank getNeutralRank() {
		return getRankByName("neutral");
	}

	public int getNullableRegionOrdering() {
		return Optional.ofNullable(region).map(FactionRegion::getOrdering).orElse(-1);
	}

	public int getOrdering() {
		return ordering;
	}

	public List getOthersOfRelation(FactionRelation relation) {
		return (List) factionSettings.streamFactionsExcept(this).filter(hummel -> ((Faction) hummel).isPlayableAlignmentFaction()).filter(faction -> (getRelation((Faction) faction) == relation)).collect(Collectors.toList());
	}

	public List getPenaltiesForKilling() {
		return (List) factionSettings.streamFactions().filter(f -> (f == this || isGoodRelation((Faction) f))).collect(Collectors.toList());
	}

	public float getPledgeAlignment() {
		return (Float) pledgeRank.map(hummel -> ((FactionRank) hummel).getAlignment()).orElse(0.0F);
	}

	public Optional getPledgeRank() {
		return pledgeRank;
	}

	public FactionRank getRankAbove(FactionRank curRank) {
		return getRankNAbove(curRank, 1);
	}

	public FactionRank getRankBelow(FactionRank curRank) {
		return getRankNBelow(curRank, 1);
	}

	public FactionRank getRankByID(int id) {
		return (FactionRank) ranksById.get(id);
	}

	public FactionRank getRankByName(String name) {
		return (FactionRank) ranksByName.get(name);
	}

	public FactionRank getRankFor(float alignment) {
		return (FactionRank) trueRanksSortedDescending.stream().filter(rank -> (!((FactionRank) rank).isDummyRank() && alignment >= ((FactionRank) rank).getAlignment())).findFirst().orElse(alignment >= 0.0F ? getNeutralRank() : getEnemyRank());
	}

	public FactionRank getRankFor(LOTRPlayerData playerData) {
		return this.getRankFor(playerData.getAlignmentData().getAlignment(this));
	}

	public FactionRank getRankFor(PlayerEntity player) {
		return this.getRankFor(LOTRLevelData.sidedInstance(player.level).getData(player));
	}

	public FactionRank getRankNAbove(FactionRank curRank, int n) {
		if (trueRanksSortedDescending.isEmpty() || curRank == null) {
			return getNeutralRank();
		}
		int index = -1;
		if (curRank.isDummyRank()) {
			index = trueRanksSortedDescending.size();
		} else if (trueRanksSortedDescending.contains(curRank)) {
			index = trueRanksSortedDescending.indexOf(curRank);
		}

		if (index < 0) {
			return getNeutralRank();
		}
		index -= n;
		if (index < 0) {
			return (FactionRank) trueRanksSortedDescending.get(0);
		}
		return index > trueRanksSortedDescending.size() - 1 ? getNeutralRank() : (FactionRank) trueRanksSortedDescending.get(index);
	}

	public FactionRank getRankNBelow(FactionRank curRank, int n) {
		return getRankNAbove(curRank, -n);
	}

	public List getRanks() {
		return ranks;
	}

	public FactionRegion getRegion() {
		return region;
	}

	public FactionRelation getRelation(Faction other) {
		return factionSettings.getRelations().getRelation(this, other);
	}

	public List getSpeechbankHomeBiomes() {
		return speechbankHomeBiomes;
	}

	public Set getTypes() {
		return types;
	}

	public boolean isAlly(Faction other) {
		return getRelation(other) == FactionRelation.ALLY;
	}

	public boolean isBadRelation(Faction other) {
		FactionRelation relation = getRelation(other);
		return relation == FactionRelation.ENEMY || relation == FactionRelation.MORTAL_ENEMY;
	}

	public boolean isGoodRelation(Faction other) {
		FactionRelation relation = getRelation(other);
		return relation == FactionRelation.ALLY || relation == FactionRelation.FRIEND;
	}

	public boolean isMortalEnemy(Faction other) {
		return getRelation(other) == FactionRelation.MORTAL_ENEMY;
	}

	public boolean isNeutral(Faction other) {
		return getRelation(other) == FactionRelation.NEUTRAL;
	}

	public boolean isOfAnyType(FactionType... checkTypes) {
		Stream var10000 = Stream.of(checkTypes);
		Set var10001 = types;
		var10001.getClass();
		return var10000.anyMatch(var10001::contains);
	}

	public boolean isPlayableAlignmentFaction() {
		return isPlayableAlignmentFaction;
	}

	public boolean isSameDimension(Faction other) {
		RegistryKey dim = getDimension();
		if (dim != null) {
			return dim.equals(other.getDimension());
		}
		return other.getRegion() == null;
	}

	public boolean isSpeechbankHomeBiome(ResourceLocation biomeName) {
		return speechbankHomeBiomes.contains(biomeName);
	}

	protected void postLoadValidateBiomes(World world) {
		speechbankHomeBiomes.forEach(biomeName -> {
			Biome foundBiome = LOTRBiomes.getBiomeByRegistryName((ResourceLocation) biomeName, world);
			if (foundBiome == null) {
				LOTRLog.warn("Faction %s specifies a biome '%s' in speechbank_home_biomes which does not exist in the biome registry!", resourceName, biomeName);
			}

		});
	}

	public void setAreasOfInfluence(AreasOfInfluence aoi) {
		if (areasOfInfluence != null) {
			throw new IllegalArgumentException("Cannot set " + name + " areas of influence - already set!");
		}
		areasOfInfluence = aoi;
	}

	public void setRanks(List ranksToSet) {
		if (ranks != null && !ranks.isEmpty()) {
			throw new IllegalArgumentException("Cannot set " + name + " ranks - already set!");
		}
		ranks = ranksToSet;
		ranksById = (Map) ranks.stream().collect(Collectors.toMap(FactionRank::getAssignedId, UnaryOperator.identity()));
		ranksByName = (Map) ranks.stream().collect(Collectors.toMap(FactionRank::getBaseName, UnaryOperator.identity()));
		List trueRanks = (List) ranks.stream().filter(rank -> !((FactionRank) rank).isDummyRank()).collect(Collectors.toList());
		trueRanksSortedAscending = new ArrayList(trueRanks);
		trueRanksSortedDescending = new ArrayList(trueRanks);
		Collections.sort(trueRanksSortedAscending);
		Collections.sort(trueRanksSortedDescending, Comparator.reverseOrder());
		pledgeRank = trueRanks.stream().filter(hummel -> ((FactionRank) hummel).isPledgeRank()).findFirst();
	}

	public void setSpeechbankHomeBiomes(List biomes) {
		if (speechbankHomeBiomes != null) {
			throw new IllegalArgumentException("Cannot set " + name + " speechbank home biomes - already set!");
		}
		speechbankHomeBiomes = biomes;
	}

	public void write(PacketBuffer buf) {
		buf.writeResourceLocation(resourceName);
		buf.writeVarInt(assignedId);
		buf.writeUtf(name);
		buf.writeBoolean(translateName);
		buf.writeUtf(subtitle);
		buf.writeBoolean(translateSubtitle);
		DataUtil.writeNullableToBuffer(buf, region, (Runnable) () -> {
			buf.writeVarInt(region.getAssignedId());
		});
		buf.writeVarInt(ordering);
		buf.writeInt(color);
		DataUtil.writeNullableToBuffer(buf, mapSquare, (BiConsumer) (hummel1, hummel2) -> ((MapSquare) hummel1).write((PacketBuffer) hummel2));
		buf.writeBoolean(isPlayableAlignmentFaction);
		DataUtil.writeCollectionToBuffer(buf, types, type -> {
			buf.writeVarInt(((FactionType) type).networkID);
		});
		buf.writeBoolean(approvesCivilianKills);
		DataUtil.writeCollectionToBuffer(buf, ranks, rank -> {
			((FactionRank) rank).write(buf);
		});
		areasOfInfluence.write(buf);
		DataUtil.writeCollectionToBuffer(buf, speechbankHomeBiomes, hummel -> buf.writeResourceLocation((ResourceLocation) hummel));
	}

	public static ITextComponent getFactionOrUnknownDisplayName(Faction faction) {
		return faction != null ? faction.getDisplayName() : new TranslationTextComponent("faction.lotr.unknown");
	}

	public static Faction read(FactionSettings factionSettings, MapSettings mapSettings, PacketBuffer buf) {
		ResourceLocation resourceName = buf.readResourceLocation();
		int assignedId = buf.readVarInt();
		String name = buf.readUtf();
		boolean translateName = buf.readBoolean();
		String subtitle = buf.readUtf();
		boolean translateSubtitle = buf.readBoolean();
		FactionRegion region = (FactionRegion) DataUtil.readNullableFromBuffer(buf, () -> {
			int regionId = buf.readVarInt();
			FactionRegion readRegion = factionSettings.getRegionByID(regionId);
			if (readRegion == null) {
				LOTRLog.warn("Received faction %s from server with a nonexistent region ID (%d) - faction will not be loaded correctly clientside", resourceName, regionId);
			}

			return readRegion;
		});
		int ordering = buf.readVarInt();
		int color = buf.readInt();
		MapSquare mapSquare = (MapSquare) DataUtil.readNullableFromBuffer(buf, () -> MapSquare.read(buf));
		boolean isPlayableAlignmentFaction = buf.readBoolean();
		Set types = (Set) DataUtil.readNewCollectionFromBuffer(buf, HashSet::new, () -> {
			int typeId = buf.readVarInt();
			FactionType type = FactionType.forNetworkID(typeId);
			if (type == null) {
				LOTRLog.warn("Received faction %s from server with a nonexistent faction type ID (%d)", resourceName, typeId);
			}

			return type;
		});
		boolean approvesCivilianKills = buf.readBoolean();
		Faction faction = new Faction(factionSettings, resourceName, assignedId, name, translateName, subtitle, translateSubtitle, region, ordering, color, mapSquare, isPlayableAlignmentFaction, types, approvesCivilianKills);
		List ranks = (List) DataUtil.readNewCollectionFromBuffer(buf, ArrayList::new, () -> {
			try {
				return FactionRank.read(faction, buf);
			} catch (Exception var4) {
				LOTRLog.warn("Error loading a rank in faction %s from server", resourceName);
				var4.printStackTrace();
				return null;
			}
		});
		faction.setRanks(ranks);
		AreasOfInfluence areasOfInfluence = AreasOfInfluence.read(faction, buf, mapSettings);
		faction.setAreasOfInfluence(areasOfInfluence);
		Supplier var10002 = ArrayList::new;
		buf.getClass();
		faction.setSpeechbankHomeBiomes((List) DataUtil.readNewCollectionFromBuffer(buf, var10002, buf::readResourceLocation));
		return faction;
	}

	public static Faction read(FactionSettings factionSettings, ResourceLocation resourceName, JsonObject json, int assignedId, MapSettings mapSettings) {
		if (json.size() == 0) {
			LOTRLog.info("Faction %s has an empty file - not loading it in this world", resourceName);
			return null;
		}
		JsonObject nameObj = json.get("name").getAsJsonObject();
		String name = nameObj.get("text").getAsString();
		boolean translateName = nameObj.get("translate").getAsBoolean();
		JsonObject subtitleObj = json.get("subtitle").getAsJsonObject();
		String subtitle = subtitleObj.get("text").getAsString();
		boolean translateSubtitle = subtitleObj.get("translate").getAsBoolean();
		String regionName = json.get("region").getAsString();
		FactionRegion region = factionSettings.getRegionByName(new ResourceLocation(regionName));
		if (region == null) {
			LOTRLog.warn("Faction %s has invalid region name %s - no such region exists", resourceName, regionName);
			return null;
		}
		int ordering = json.get("ordering").getAsInt();
		String hexColor = json.get("color").getAsString();
		int color = 0;

		try {
			color = Integer.parseInt(hexColor, 16);
		} catch (NumberFormatException var33) {
			LOTRLog.warn("Faction %s has invalid color code %s - must be in hex color format (e.g. FFAA33)", resourceName, hexColor);
		}

		JsonObject mapSquareObj = json.get("map_square").getAsJsonObject();
		MapSquare mapSquare = MapSquare.read(mapSquareObj);
		boolean isPlayableAlignmentFaction = true;
		JsonArray typesArray = json.get("types").getAsJsonArray();
		Set types = new HashSet();
		for (JsonElement typeElement : typesArray) {
			String typeName = typeElement.getAsString();
			FactionType type = FactionType.forName(new ResourceLocation(typeName));
			if (type != null) {
				types.add(type);
			} else {
				LOTRLog.warn("Faction %s includes invalid faction type name %s - no such type exists", resourceName, typeName);
			}
		}

		boolean approvesCivilianKills = json.get("approves_civilian_kills").getAsBoolean();
		Faction faction = new Faction(factionSettings, resourceName, assignedId, name, translateName, subtitle, translateSubtitle, region, ordering, color, mapSquare, isPlayableAlignmentFaction, types, approvesCivilianKills);
		List ranks = new ArrayList();
		int nextRankId = 0;
		nextRankId = DummyFactionRanks.registerCommonRanks(faction, ranks, nextRankId);
		if (json.has("ranks")) {
			JsonArray ranksArray = json.get("ranks").getAsJsonArray();
			for (JsonElement rankElement : ranksArray) {
				try {
					JsonObject rankObj = rankElement.getAsJsonObject();
					FactionRank rank = FactionRank.read(faction, rankObj, nextRankId);
					if (rank != null) {
						ranks.add(rank);
					}

					++nextRankId;
				} catch (Exception var32) {
					LOTRLog.warn("Failed to load a rank in faction %s from file", faction.getName());
					var32.printStackTrace();
				}
			}

			if (ranks.stream().filter(hummel -> ((FactionRank) hummel).isPledgeRank()).count() > 1L) {
				LOTRLog.warn("Faction %s declares more than one pledge rank (%s) - only one is allowed. Ranks will not be loaded until this is fixed", faction.getName(), String.join(",", (Iterable) ranks.stream().filter(hummel -> ((FactionRank) hummel).isPledgeRank()).map(hummel -> ((FactionRank) hummel).getBaseName()).collect(Collectors.toList())));
			} else {
				faction.setRanks(ranks);
			}
		}

		JsonObject aoiObj = json.get("areas_of_influence").getAsJsonObject();
		AreasOfInfluence areasOfInfluence = AreasOfInfluence.read(faction, aoiObj, mapSettings);
		faction.setAreasOfInfluence(areasOfInfluence);
		List speechbankHomeBiomes = new ArrayList();
		if (json.has("speechbank_home_biomes")) {
			JsonArray biomeArray = json.get("speechbank_home_biomes").getAsJsonArray();
			for (JsonElement elem : biomeArray) {
				ResourceLocation biomeName = new ResourceLocation(elem.getAsString());
				speechbankHomeBiomes.add(biomeName);
			}
		}

		faction.setSpeechbankHomeBiomes(speechbankHomeBiomes);
		return faction;
	}
}
