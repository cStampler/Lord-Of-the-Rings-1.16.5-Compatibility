package lotr.common.entity.npc.inv;

import lotr.common.entity.npc.NPCEntity;
import lotr.common.inv.EntityInventory;

public class NPCInventory extends EntityInventory {
	public NPCInventory(NPCEntity entity, int size, String name) {
		super(entity, size, name);
	}
}
