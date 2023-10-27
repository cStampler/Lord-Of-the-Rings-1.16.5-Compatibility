package lotr.common.entity.npc;

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
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class HarnedhrimEntity extends ManEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.HARAD_DAGGER);
	}

	public HarnedhrimEntity(EntityType type, World w) {
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
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected ITextComponent formatNPCName(ITextComponent npcName, ITextComponent typeName) {
		return new TranslationTextComponent("entityname.lotr.harnedhrim", npcName, typeName);
	}

	@Override
	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.HARNEDHRIM_DRINK;
	}

	@Override
	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.HARNEDHRIM;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.HARNENNOR;
	}
}
