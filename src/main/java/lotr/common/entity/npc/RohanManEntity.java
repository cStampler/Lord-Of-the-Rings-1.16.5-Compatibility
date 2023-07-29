package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.*;
import lotr.common.entity.npc.data.name.*;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.*;
import net.minecraft.world.*;

public class RohanManEntity extends ManEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.ROHAN_DAGGER, LOTRItems.IRON_DAGGER, LOTRItems.BRONZE_DAGGER, Items.IRON_AXE, LOTRItems.BRONZE_AXE, Items.STONE_AXE);
	}

	public RohanManEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected void addNPCTargetingAI() {
		addNonAggressiveTargetingGoals();
	}

	@Override
	protected Goal createAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.4D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected ITextComponent formatNPCName(ITextComponent npcName, ITextComponent typeName) {
		return new TranslationTextComponent("entityname.lotr.rohan_man", npcName, typeName);
	}

	@Override
	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.ROHAN_DRINK;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.ROHAN;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.ROHAN;
	}
}
