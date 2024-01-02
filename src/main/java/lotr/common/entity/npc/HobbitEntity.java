package lotr.common.entity.npc;

import lotr.common.config.LOTRConfig;
import lotr.common.entity.npc.ai.goal.FriendlyNPCConversationGoal;
import lotr.common.entity.npc.ai.goal.HobbitSmokeGoal;
import lotr.common.entity.npc.ai.goal.NPCDrinkGoal;
import lotr.common.entity.npc.ai.goal.NPCEatGoal;
import lotr.common.entity.npc.ai.goal.TalkToCurrentGoal;
import lotr.common.entity.npc.ai.goal.WatchSunriseSunsetGoal;
import lotr.common.entity.npc.data.NPCFoodPool;
import lotr.common.entity.npc.data.NPCFoodPools;
import lotr.common.entity.npc.data.name.NPCNameGenerator;
import lotr.common.entity.npc.data.name.NPCNameGenerators;
import lotr.common.init.LOTRAttributes;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.OpenDoorGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.world.World;

public class HobbitEntity extends AbstractMannishEntity {
	public HobbitEntity(EntityType<? extends HobbitEntity> type, World w) {
		super(type, w);
	}

	@Override
	protected void addNPCAI() {
		super.addNPCAI();
		goalSelector.addGoal(0, new SwimGoal(this));
		goalSelector.addGoal(1, new AvoidEntityGoal(this, OrcEntity.class, 12.0F, 1.5D, 1.8D));
		goalSelector.addGoal(2, new PanicGoal(this, 1.6D));
		goalSelector.addGoal(9, new OpenDoorGoal(this, true));
		goalSelector.addGoal(10, new TalkToCurrentGoal(this));
		goalSelector.addGoal(11, new FriendlyNPCConversationGoal(this, 0.001F));
		goalSelector.addGoal(12, new WatchSunriseSunsetGoal(this, 0.01F));
		goalSelector.addGoal(13, new WaterAvoidingRandomWalkingGoal(this, 1.1D));
		goalSelector.addGoal(14, new NPCEatGoal(this, getEatPool(), 3000));
		goalSelector.addGoal(14, new NPCDrinkGoal(this, getDrinkPool(), 3000));
		goalSelector.addGoal(14, new HobbitSmokeGoal(this, 4000));
		goalSelector.addGoal(15, new LookAtGoal(this, PlayerEntity.class, 8.0F, 0.05F));
		goalSelector.addGoal(15, new LookAtGoal(this, NPCEntity.class, 5.0F, 0.05F));
		goalSelector.addGoal(16, new LookAtGoal(this, LivingEntity.class, 8.0F, 0.02F));
		goalSelector.addGoal(17, new LookRandomlyGoal(this));
	}

	protected NPCFoodPool getDrinkPool() {
		return NPCFoodPools.HOBBIT_DRINK;
	}

	protected NPCFoodPool getEatPool() {
		return NPCFoodPools.HOBBIT;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.HOBBIT;
	}

	@Override
	public boolean useSmallArmsModel() {
		return (Boolean) LOTRConfig.CLIENT.hobbitWomenUseAlexModelStyle.get() && personalInfo.isFemale();
	}

	public static MutableAttribute regAttrs() {
		return NPCEntity.registerBaseNPCAttributes().add(Attributes.MAX_HEALTH, 16.0D).add(Attributes.MOVEMENT_SPEED, 0.2D).add((Attribute) LOTRAttributes.NPC_CONVERSATION_RANGE.get(), 6.0D);
	}
}
