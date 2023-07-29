package lotr.common.entity.npc;

import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.goal.*;
import lotr.common.entity.npc.data.*;
import lotr.common.entity.npc.data.name.*;
import lotr.common.init.*;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.*;

public class DwarfEntity extends NPCEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(Items.IRON_AXE, Items.IRON_PICKAXE, LOTRItems.IRON_DAGGER, LOTRItems.DWARVEN_AXE, LOTRItems.DWARVEN_PICKAXE, LOTRItems.DWARVEN_DAGGER);
	}

	public DwarfEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		addAttackGoal(3);
		goalSelector.addGoal(9, new OpenDoorGoal(this, true));
		goalSelector.addGoal(10, new TalkToCurrentGoal(this));
		goalSelector.addGoal(11, new FriendlyNPCConversationGoal(this, 5.0E-4F));
		goalSelector.addGoal(12, new WatchSunriseSunsetGoal(this, 0.001F));
		goalSelector.addGoal(13, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(14, new NPCEatGoal(this, getEatPool(), 6000));
		goalSelector.addGoal(14, new NPCDrinkGoal(this, getDrinkPool(), 6000));
		goalSelector.addGoal(15, new LookAtGoal(this, PlayerEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(15, new LookAtGoal(this, NPCEntity.class, 5.0F, 0.02F));
		goalSelector.addGoal(16, new LookAtGoal(this, LivingEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(17, new LookRandomlyGoal(this));
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	protected boolean canDwarfSpawnAboveGround() {
		return true;
	}

	protected boolean canDwarfSpawnHere() {
		if (random.nextInt(20) == 0) {
			return canDwarfSpawnAboveGround();
		}
		BlockPos pos = blockPosition();
		return level.getBlockState(pos.below()).getMaterial() == Material.STONE && !level.canSeeSkyFromBelowWater(pos);
	}

	@Override
	public boolean checkSpawnRules(IWorld iworld, SpawnReason reason) {
		return super.checkSpawnRules(iworld, reason) && (reason != SpawnReason.NATURAL || canDwarfSpawnHere());
	}

	protected Goal createDwarfAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.3D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected SoundEvent getAttackSound() {
		return LOTRSoundEvents.DWARF_ATTACK;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return LOTRSoundEvents.DWARF_DEATH;
	}

	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.DWARF_DRINK;
	}

	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.DWARF;
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.DWARF;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return LOTRSoundEvents.DWARF_HURT;
	}

	@Override
	protected SoundEvent getKillSound() {
		return LOTRSoundEvents.DWARF_KILL;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.DWARF;
	}

	@Override
	protected float getVoicePitch() {
		float f = super.getVoicePitch();
		if (personalInfo.isFemale()) {
			f *= 1.2F;
		}

		return f;
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		holder.setMeleeAttackGoal(createDwarfAttackGoal());
	}

	@Override
	public boolean useSmallArmsModel() {
		return (Boolean) LOTRConfig.CLIENT.dwarfWomenUseAlexModelStyle.get() && personalInfo.isFemale();
	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.MAX_HEALTH, 26.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add((Attribute) LOTRAttributes.NPC_CONVERSATION_RANGE.get(), 6.0D).add(Attributes.KNOCKBACK_RESISTANCE, 0.4D);
	}
}
