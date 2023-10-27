package lotr.common.entity.npc;

import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.goal.FriendlyNPCConversationGoal;
import lotr.common.entity.npc.ai.goal.NPCDrinkGoal;
import lotr.common.entity.npc.ai.goal.NPCEatGoal;
import lotr.common.entity.npc.ai.goal.TalkToCurrentGoal;
import lotr.common.entity.npc.ai.goal.WatchSunriseSunsetGoal;
import lotr.common.entity.npc.data.NPCFoodPool;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public abstract class ManEntity extends AbstractMannishEntity {
	public ManEntity(EntityType type, World w) {
		super(type, w);
	}

	protected void addConsumingGoals(int prio) {
		goalSelector.addGoal(prio, new NPCEatGoal(this, getEatPool(), 8000));
		goalSelector.addGoal(prio, new NPCDrinkGoal(this, getDrinkPool(), 8000));
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		addAttackGoal(2);
		goalSelector.addGoal(4, new OpenDoorGoal(this, true));
		goalSelector.addGoal(5, new TalkToCurrentGoal(this));
		goalSelector.addGoal(6, new FriendlyNPCConversationGoal(this, 5.0E-4F));
		goalSelector.addGoal(7, new WatchSunriseSunsetGoal(this, 0.005F));
		goalSelector.addGoal(8, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
		addConsumingGoals(9);
		goalSelector.addGoal(10, new LookAtGoal(this, PlayerEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(10, new LookAtGoal(this, NPCEntity.class, 5.0F, 0.02F));
		goalSelector.addGoal(11, new LookAtGoal(this, LivingEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(12, new LookRandomlyGoal(this));
	}

	protected abstract Goal createAttackGoal();

	protected abstract NPCFoodPool getDrinkPool();

	protected abstract NPCFoodPool getEatPool();

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		holder.setMeleeAttackGoal(createAttackGoal());
	}

	@Override
	public boolean useSmallArmsModel() {
		return (Boolean) LOTRConfig.CLIENT.mannishWomenUseAlexModelStyle.get() && personalInfo.isFemale();
	}
}
