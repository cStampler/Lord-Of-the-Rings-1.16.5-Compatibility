package lotr.common.speech.condition;

import java.util.OptionalInt;

import lotr.common.LOTRLog;
import lotr.common.fac.Faction;
import lotr.common.fac.FactionRank;
import lotr.curuquesta.condition.predicate.AsymmetricComparator;

public class OptionallyUnderspecifiedFactionRank {
	private final Faction faction;
	private final String rankName;

	private OptionallyUnderspecifiedFactionRank(Faction faction, String rankName) {
		this.faction = faction;
		this.rankName = rankName;
	}

	public Faction getFaction() {
		return faction;
	}

	public String getRankName() {
		return rankName;
	}

	public boolean isUnderspecified() {
		return faction == null;
	}

	public FactionRank resolveRank() {
		if (isUnderspecified()) {
			throw new IllegalStateException("Cannot resolve the rank when it's underspecified! Development error.");
		}
		FactionRank rank = faction.getRankByName(rankName);
		if (rank == null) {
			throw new IllegalArgumentException("Could not resolve rank name " + rankName + " in faction " + faction.getName() + " - no such rank!");
		}
		return rank;
	}

	@Override
	public String toString() {
		return isUnderspecified() ? String.format("%s (faction unspecified)", rankName) : resolveRank().toString();
	}

	public static AsymmetricComparator asymmetricComparator() {
		return (rankInContext, rankInPredicate) -> {
			if (((OptionallyUnderspecifiedFactionRank) rankInContext).isUnderspecified()) {
				throw new IllegalArgumentException("The rank-in-context must be fully specified for this to work! Development error.");
			}
			Faction factionInContext = ((OptionallyUnderspecifiedFactionRank) rankInContext).getFaction();
			String rankInPredicateName = ((OptionallyUnderspecifiedFactionRank) rankInPredicate).rankName;
			if (factionInContext.getRankByName(rankInPredicateName) == null) {
				LOTRLog.debug("Speechbank entry refers to a rank '%s', but there is no such rank in the faction-in-context (%s). SpeechbankOverride is probably responsible here. This speechbank entry will be skipped.", rankInPredicateName, factionInContext.getName());
				return OptionalInt.empty();
			}
			OptionallyUnderspecifiedFactionRank fullySpecifiedRankInPredicate = fullySpecified(factionInContext, rankInPredicateName);
			int rankComparison = ((OptionallyUnderspecifiedFactionRank) rankInContext).resolveRank().compareTo(fullySpecifiedRankInPredicate.resolveRank());
			return OptionalInt.of(rankComparison);
		};
	}

	public static OptionallyUnderspecifiedFactionRank fullySpecified(Faction faction, String rankName) {
		if (faction == null) {
			throw new IllegalArgumentException("OptionallyUnderspecifiedFactionRank error - constructing a fully specified one, but faction is null!");
		}
		if (rankName == null) {
			throw new IllegalArgumentException("OptionallyUnderspecifiedFactionRank error - constructing a fully specified one, but rankName is null!");
		}
		return new OptionallyUnderspecifiedFactionRank(faction, rankName);
	}

	public static OptionallyUnderspecifiedFactionRank fullySpecified(FactionRank rank) {
		return new OptionallyUnderspecifiedFactionRank(rank.getFaction(), rank.getBaseName());
	}

	public static OptionallyUnderspecifiedFactionRank underspecified(String rankName) {
		if (rankName == null) {
			throw new IllegalArgumentException("OptionallyUnderspecifiedFactionRank error - constructing an underspecified one, but the rankName at least must be specified!");
		}
		return new OptionallyUnderspecifiedFactionRank((Faction) null, rankName);
	}
}
