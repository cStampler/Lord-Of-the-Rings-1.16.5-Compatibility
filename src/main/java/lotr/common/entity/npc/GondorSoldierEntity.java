package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.StandardAttackModeUpdaters;
import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.init.LOTRAttributes;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class GondorSoldierEntity extends GondorManEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SWORD, LOTRItems.GONDOR_SPEAR);
	}

	public GondorSoldierEntity(EntityType type, World w) {
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
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.GONDOR_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.GONDOR_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.GONDOR_CHESTPLATE.get()));
		setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.GONDOR_HELMET.get()));
		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack((IItemProvider) LOTRItems.GONDOR_SHIELD.get()));
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
		return AbstractMannishEntity.regAttrs().add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get(), 0.75D);
	}
}
