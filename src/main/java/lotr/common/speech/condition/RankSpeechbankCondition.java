package lotr.common.speech.condition;

import java.util.function.Function;

import io.netty.buffer.ByteBuf;
import lotr.common.LOTRLog;
import lotr.common.fac.*;
import lotr.curuquesta.condition.SpeechbankCondition;
import lotr.curuquesta.condition.predicate.ComplexPredicateParsers;

public class RankSpeechbankCondition extends SpeechbankCondition {
	public RankSpeechbankCondition(String conditionName, Function valueFromContext) {
		super(conditionName, valueFromContext, ComplexPredicateParsers.logicalExpressionOfComparableSubpredicates(RankSpeechbankCondition::parseRank, OptionallyUnderspecifiedFactionRank.asymmetricComparator()));
	}

	@Override
	public boolean isValidValue(Object rank) {
		return rank != null && ((OptionallyUnderspecifiedFactionRank) rank).getRankName() != null;
	}

	@Override
	protected OptionallyUnderspecifiedFactionRank readValue(ByteBuf buf) {
		int facId = buf.readInt();
		int rankId = buf.readInt();
		Faction faction = currentFactions().getFactionByID(facId);
		if (faction == null) {
			LOTRLog.warn("Received faction with ID %d as part of a speechbank context, but no such faction exists!", facId);
			return null;
		}
		FactionRank rank = faction.getRankByID(rankId);
		if (rank == null) {
			LOTRLog.warn("Received rank with ID %d in faction %s as part of a speechbank context, but no such rank exists!", rankId, faction.getName());
			return null;
		}
		return OptionallyUnderspecifiedFactionRank.fullySpecified(rank);
	}

	@Override
	protected void writeValue(Object rank, ByteBuf buf) {
		buf.writeInt(((OptionallyUnderspecifiedFactionRank) rank).getFaction().getAssignedId());
		buf.writeInt(((OptionallyUnderspecifiedFactionRank) rank).resolveRank().getAssignedId());
	}

	private static FactionSettings currentFactions() {
		return FactionSettingsManager.clientInstance().getCurrentLoadedFactions();
	}

	private static OptionallyUnderspecifiedFactionRank parseRank(String s) {
		return OptionallyUnderspecifiedFactionRank.underspecified(s);
	}
}
