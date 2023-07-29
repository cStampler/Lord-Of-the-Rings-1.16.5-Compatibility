package lotr.common.entity.npc;

import lotr.common.entity.npc.data.SuppliableItemTable;

public class SpawnEquipmentTable extends SuppliableItemTable {
	private SpawnEquipmentTable(Object... items) {
		super(items);
	}

	public static SpawnEquipmentTable of(Object... items) {
		return new SpawnEquipmentTable(items);
	}
}
