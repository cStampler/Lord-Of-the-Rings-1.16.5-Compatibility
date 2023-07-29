package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.*;
import lotr.common.entity.npc.data.name.*;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.*;

public class DunlendingEntity extends ManEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.DUNLENDING_CLUB, LOTRItems.DUNLENDING_CLUB, LOTRItems.DUNLENDING_CLUB, Items.STONE_SWORD, Items.STONE_AXE, LOTRItems.STONE_DAGGER, LOTRItems.STONE_SPEAR, LOTRItems.BRONZE_AXE, LOTRItems.BRONZE_DAGGER, LOTRItems.BRONZE_SPEAR, Items.IRON_AXE, LOTRItems.IRON_DAGGER, LOTRItems.IRON_SPEAR);
	}

	public DunlendingEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	@Override
	protected Goal createAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.5D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		if (random.nextInt(4) == 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.FUR_HELMET.get()));
		}

		return spawnData;
	}

	@Override
	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.DUNLENDING_DRINK;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.DUNLENDING;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.DUNLENDING;
	}
}
