package lotr.common.entity.npc.ai.goal;

import java.util.*;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.fac.Faction;
import lotr.common.init.LOTRAttributes;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.goal.Goal;

public class FriendlyNPCConversationGoal extends Goal {
	private final NPCEntity theEntity;
	private final float chance;
	private NPCEntity talkingTo;

	public FriendlyNPCConversationGoal(NPCEntity entity, float chanceIn) {
		theEntity = entity;
		chance = chanceIn;
		setFlags(EnumSet.of(Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		if (!isAvailableToStartTalking(theEntity)) {
			return false;
		}
		Random rand = theEntity.getRandom();
		if (rand.nextFloat() < chance) {
			Faction entityFac = theEntity.getFaction();
			double maxRange = getConversationRange();
			List friendlyNearbyTalkable = theEntity.level.getLoadedEntitiesOfClass(NPCEntity.class, theEntity.getBoundingBox().inflate(maxRange, 3.0D, maxRange), npc -> {
				if (npc != theEntity && isAvailableToStartTalking(npc)) {
					Faction npcFac = npc.getFaction();
					if (npcFac == entityFac || !npc.getFaction().isBadRelation(entityFac) && rand.nextBoolean()) {
						return theEntity.getSensing().canSee(npc);
					}
				}

				return false;
			});
			if (!friendlyNearbyTalkable.isEmpty()) {
				talkingTo = (NPCEntity) friendlyNearbyTalkable.get(rand.nextInt(friendlyNearbyTalkable.size()));
				return true;
			}
		}

		return false;
	}

	private double getConversationRange() {
		return theEntity.getAttributeValue((Attribute) LOTRAttributes.NPC_CONVERSATION_RANGE.get());
	}

	private boolean isAvailableToStartTalking(NPCEntity npc) {
		return isAvailableForTalking(npc) && npc.getTalkingToEntity() == null;
	}

	@Override
	public void start() {
		Random rand = theEntity.getRandom();
		int time = 100 + rand.nextInt(300);
		if (rand.nextInt(30) == 0) {
			time += 400 + rand.nextInt(400);
		}

		theEntity.setTalkingToEntity(talkingTo, time);
		talkingTo.setTalkingToEntity(theEntity, time);
	}

	@Override
	public void stop() {
		talkingTo = null;
	}

	public static boolean isAvailableForTalking(LivingEntity entity) {
		if (!entity.isAlive()) {
			return false;
		}
		if (entity instanceof MobEntity) {
			return ((MobEntity) entity).getTarget() == null;
		}
		return true;
	}
}
