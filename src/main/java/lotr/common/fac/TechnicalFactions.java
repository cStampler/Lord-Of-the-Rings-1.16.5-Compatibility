package lotr.common.fac;

import java.util.List;
import java.util.Set;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import lotr.common.world.map.MapSettings;
import net.minecraft.util.ResourceLocation;

public class TechnicalFactions {
	private static Faction createTechnicalFaction(FactionSettings facSettings, MapSettings mapSettings, FactionPointer facPointer, int id) {
		ResourceLocation res = facPointer.getName();
		String name = String.format("faction.%s.%s", res.getNamespace(), res.getPath());
		boolean translateName = false;
		String subtitle = "";
		boolean translateSubtitle = false;
		FactionRegion region = null;
		int ordering = 0;
		int color = 0;
		MapSquare mapSquare = null;
		boolean isPlayableAlignmentFaction = false;
		Set types = ImmutableSet.of();
		boolean civilianKills = false;
		Faction faction = new Faction(facSettings, res, id, name, translateName, subtitle, translateSubtitle, region, ordering, color, mapSquare, isPlayableAlignmentFaction, types, civilianKills);
		faction.setAreasOfInfluence(AreasOfInfluence.makeEmptyAreas(mapSettings, faction));
		faction.setSpeechbankHomeBiomes(ImmutableList.of());
		return faction;
	}

	public static int registerTechnicalFactions(FactionSettings facSettings, MapSettings mapSettings, List factions, int nextFactionId) {
		factions.add(createTechnicalFaction(facSettings, mapSettings, FactionPointers.UNALIGNED, nextFactionId));
		nextFactionId++;
		factions.add(createTechnicalFaction(facSettings, mapSettings, FactionPointers.HOSTILE, nextFactionId));
		nextFactionId++;
		return nextFactionId;
	}
}
