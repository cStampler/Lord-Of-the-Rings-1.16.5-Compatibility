package lotr.common.fac;

import java.util.List;

public class DummyFactionRanks {
	public static final String NEUTRAL = "neutral";
	public static final String ENEMY = "enemy";

	private static FactionRank createDummyRank(Faction faction, String name, int id, float dummyAlign) {
		return new FactionRank(faction, name, id, true, dummyAlign, false);
	}

	public static int registerCommonRanks(Faction faction, List<FactionRank> ranks, int nextRankId) {
		ranks.add(createDummyRank(faction, "neutral", nextRankId, 0.0F));
		nextRankId++;
		ranks.add(createDummyRank(faction, "enemy", nextRankId, Float.MIN_VALUE));
		nextRankId++;
		return nextRankId;
	}
}
