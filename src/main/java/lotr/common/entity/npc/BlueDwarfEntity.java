package lotr.common.entity.npc;

import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.entity.npc.data.NPCFoodPools;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class BlueDwarfEntity extends DwarfEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(Items.IRON_AXE, Items.IRON_PICKAXE, LOTRItems.IRON_DAGGER, LOTRItems.BLUE_DWARVEN_AXE, LOTRItems.BLUE_DWARVEN_PICKAXE, LOTRItems.BLUE_DWARVEN_DAGGER);
	}

	public BlueDwarfEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.BLUE_DWARF;
	}
}
