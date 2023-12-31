package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.HobbitSmokeGoal;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.entity.npc.data.NPCFoodPools;
import lotr.common.entity.npc.data.name.NPCNameGenerator;
import lotr.common.entity.npc.data.name.NPCNameGenerators;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class BreeManEntity extends ManEntity {
	private static final SpawnEquipmentTable WEAPONS;
	public static final String CARROT_EATER_NAME = "Peter Jackson";

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.IRON_DAGGER, LOTRItems.BRONZE_DAGGER, Items.IRON_AXE, LOTRItems.BRONZE_AXE, Items.STONE_AXE);
	}

	public BreeManEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected void addConsumingGoals(int prio) {
		super.addConsumingGoals(prio);
		goalSelector.addGoal(prio, new HobbitSmokeGoal(this, 12000));
	}

	@Override
	protected void addNPCTargetingAI() {
		addNonAggressiveTargetingGoals();
	}

	@Override
	protected Goal createAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.3D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.clearIdleItem();
		if (personalInfo.isMale() && random.nextInt(2000) == 0) {
			personalInfo.setName("Peter Jackson");
			npcItemsInv.setIdleItem(new ItemStack(Items.CARROT));
		}

		return spawnData;
	}

	@Override
	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.BREE_DRINK;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.BREE;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.BREE;
	}
}
