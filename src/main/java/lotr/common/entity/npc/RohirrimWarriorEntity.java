package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.StandardAttackModeUpdaters;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.init.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.*;

public class RohirrimWarriorEntity extends RohanManEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.ROHAN_SWORD, LOTRItems.ROHAN_SWORD, LOTRItems.ROHAN_SWORD);
	}

	public RohirrimWarriorEntity(EntityType type, World w) {
		super(type, w);
		getNPCCombatUpdater().setAttackModeUpdater(StandardAttackModeUpdaters.mountableMeleeOnly());
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	@Override
	protected Goal createAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.45D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.ROHAN_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.ROHAN_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.ROHAN_CHESTPLATE.get()));
		setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.ROHAN_HELMET.get()));
		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack((IItemProvider) LOTRItems.ROHAN_SHIELD.get()));
		return spawnData;
	}

	@Override
	protected ITextComponent formatNPCName(ITextComponent npcName, ITextComponent typeName) {
		return formatGenericNPCName(npcName, typeName);
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE;
	}

	public static MutableAttribute regAttrs() {
		return AbstractMannishEntity.regAttrs().add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get(), 0.75D).add((Attribute) LOTRAttributes.NPC_MOUNT_ATTACK_SPEED.get(), 2.0D);
	}
}
