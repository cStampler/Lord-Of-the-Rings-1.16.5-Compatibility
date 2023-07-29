package lotr.common.entity.npc;

import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.ai.*;
import lotr.common.entity.npc.ai.goal.*;
import lotr.common.entity.npc.data.*;
import lotr.common.entity.npc.data.name.*;
import lotr.common.init.LOTRSoundEvents;
import lotr.common.world.spawning.NPCSpawnSettingsManager;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.*;
import net.minecraft.util.*;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public abstract class OrcEntity extends NPCEntity {
	protected boolean isOrcWeakInSun = true;

	public OrcEntity(EntityType type, World w) {
		super(type, w);
		getNPCCombatUpdater().setAttackModeUpdater(StandardAttackModeUpdaters.meleeOnlyOrcWithBomb());
		spawnRequiresDarkness = true;
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		addAttackGoal(4);
		goalSelector.addGoal(6, new OpenDoorGoal(this, true));
		goalSelector.addGoal(7, new TalkToCurrentGoal(this));
		goalSelector.addGoal(8, new FriendlyNPCConversationGoal(this, 2.0E-4F));
		goalSelector.addGoal(9, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		goalSelector.addGoal(10, new NPCEatGoal(this, NPCFoodPools.ORC, 6000));
		goalSelector.addGoal(10, new NPCDrinkGoal(this, NPCFoodPools.ORC_DRINK, 6000));
		goalSelector.addGoal(11, new LookAtGoal(this, PlayerEntity.class, 8.0F, 0.05F));
		goalSelector.addGoal(11, new LookAtGoal(this, NPCEntity.class, 5.0F, 0.05F));
		goalSelector.addGoal(12, new LookAtGoal(this, LivingEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(13, new LookRandomlyGoal(this));
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	protected abstract Goal createOrcAttackGoal();

	@Override
	protected SoundEvent getAmbientSound() {
		return LOTRSoundEvents.ORC_AEUGH;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return LOTRSoundEvents.ORC_DEATH;
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return LOTRSoundEvents.ORC_HURT;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.ORC;
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		holder.setMeleeAttackGoal(createOrcAttackGoal());
	}

	private boolean isOrcExposedToSun() {
		BlockPos pos = blockPosition();
		Biome biome = level.getBiome(pos);
		return level.isDay() && level.canSeeSkyFromBelowWater(pos) && !NPCSpawnSettingsManager.getSpawnsForBiome(biome, level).allowsDarknessSpawnsInDaytime();
	}

	@Override
	public void tick() {
		super.tick();
		if (!level.isClientSide && isOrcWeakInSun && isOrcExposedToSun() && tickCount % 20 == 0) {
			addEffect(new EffectInstance(Effects.DAMAGE_RESISTANCE, 200, -2));
			addEffect(new EffectInstance(Effects.MOVEMENT_SLOWDOWN, 200));
		}

	}

	@Override
	public boolean useSmallArmsModel() {
		return (Boolean) LOTRConfig.CLIENT.orcWomenUseAlexModelStyle.get() && personalInfo.isFemale();
	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.MAX_HEALTH, 18.0D).add(Attributes.MOVEMENT_SPEED, 0.2D);
	}
}
