package lotr.common.entity.npc;

import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.StandardAttackModeUpdaters;
import lotr.common.entity.npc.ai.goal.FriendlyNPCConversationGoal;
import lotr.common.entity.npc.ai.goal.NPCDrinkGoal;
import lotr.common.entity.npc.ai.goal.NPCEatGoal;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.ai.goal.NPCRangedAttackGoal;
import lotr.common.entity.npc.ai.goal.StargazingGoal;
import lotr.common.entity.npc.ai.goal.TalkToCurrentGoal;
import lotr.common.entity.npc.ai.goal.WatchSunriseSunsetGoal;
import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.entity.npc.data.NPCFoodPools;
import lotr.common.init.LOTRAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class ElfEntity extends NPCEntity {
	public ElfEntity(EntityType type, World w) {
		super(type, w);
		getNPCCombatUpdater().setAttackModeUpdater(StandardAttackModeUpdaters.meleeRangedSwitching());
		spawnRequiresSurfaceBlock = true;
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		addAttackGoal(2);
		goalSelector.addGoal(4, new OpenDoorGoal(this, true));
		goalSelector.addGoal(5, new TalkToCurrentGoal(this));
		goalSelector.addGoal(6, new FriendlyNPCConversationGoal(this, 0.001F));
		goalSelector.addGoal(7, new WatchSunriseSunsetGoal(this, 0.01F));
		goalSelector.addGoal(8, new StargazingGoal(this, 4.0E-4F));
		goalSelector.addGoal(9, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(10, new NPCEatGoal(this, getEatPool(), 12000));
		goalSelector.addGoal(10, new NPCDrinkGoal(this, getDrinkPool(), 8000));
		goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(11, new LookAtGoal(this, NPCEntity.class, 5.0F, 0.02F));
		goalSelector.addGoal(12, new LookAtGoal(this, LivingEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(13, new LookRandomlyGoal(this));
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	protected Goal createElfMeleeAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.4D);
	}

	protected Goal createElfRangedAttackGoal() {
		return new NPCRangedAttackGoal(this, 1.25D, 20, 16.0F);
	}

	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.ELF_DRINK;
	}

	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.ELF;
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		holder.setMeleeAttackGoal(createElfMeleeAttackGoal());
		holder.setRangedAttackGoal(createElfRangedAttackGoal());
	}

	@Override
	public boolean useSmallArmsModel() {
		return (Boolean) LOTRConfig.CLIENT.elfWomenUseAlexModelStyle.get() && personalInfo.isFemale();
	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.MAX_HEALTH, 30.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get(), 0.5D);
	}
}
