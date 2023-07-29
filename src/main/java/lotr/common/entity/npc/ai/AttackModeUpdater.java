package lotr.common.entity.npc.ai;

import lotr.common.entity.npc.NPCEntity;

@FunctionalInterface
public interface AttackModeUpdater {
	void onAttackModeChange(NPCEntity var1, AttackMode var2, boolean var3);
}
