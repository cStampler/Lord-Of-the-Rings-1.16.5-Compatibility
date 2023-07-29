package lotr.common.entity.npc.ai;

import java.util.*;

import lotr.common.entity.npc.NPCEntity;
import net.minecraft.entity.Entity;

public class NPCTargetSorter implements Comparator {
	private final NPCEntity theNPC;

	public NPCTargetSorter(NPCEntity npc) {
		theNPC = npc;
	}

	@Override
	public int compare(Object e1, Object e2) {
		double d1 = distanceMetricSq((Entity) e1);
		double d2 = distanceMetricSq((Entity) e2);
		if (d1 < d2) {
			return -1;
		}
		return d1 > d2 ? 1 : 0;
	}

	private double distanceMetricSq(Entity target) {
		double dSq = theNPC.distanceToSqr(target);
		double avg = 12.0D;
		double avgSq = avg * avg;
		dSq /= avgSq;
		double nearRange = 8.0D;
		List nearbyWithSameTarget = theNPC.level.getLoadedEntitiesOfClass(NPCEntity.class, theNPC.getBoundingBox().inflate(nearRange), npc -> (npc != theNPC && npc.isAlive() && npc.getTarget() == target));
		int dupes = nearbyWithSameTarget.size();
		int dupesSq = dupes * dupes;
		return dSq + dupesSq;
	}
}
