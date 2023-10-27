package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.ai.goal.NPCRangedAttackGoal;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class GaladhrimWarriorEntity extends GaladhrimElfEntity {
	private static final SpawnEquipmentTable WEAPONS;
	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SWORD, LOTRItems.GALADHRIM_SPEAR);
	}

	private boolean isDefendingTree;

	public GaladhrimWarriorEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	public void addAdditionalSaveData(CompoundNBT nbt) {
		super.addAdditionalSaveData(nbt);
		nbt.putBoolean("DefendingTree", isDefendingTree);
	}

	@Override
	protected Goal createElfMeleeAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.4D);
	}

	@Override
	protected Goal createElfRangedAttackGoal() {
		return new NPCRangedAttackGoal(this, 1.25D, 20, 24.0F);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setRangedWeapon(new ItemStack(Items.BOW));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.GALADHRIM_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.GALADHRIM_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.GALADHRIM_CHESTPLATE.get()));
		if (random.nextInt(10) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.GALADHRIM_HELMET.get()));
		}

		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
		return spawnData;
	}

	@Override
	public void readAdditionalSaveData(CompoundNBT nbt) {
		super.readAdditionalSaveData(nbt);
		isDefendingTree = nbt.getBoolean("DefendingTree");
	}

	public void setDefendingTree() {
		isDefendingTree = true;
	}

	public static MutableAttribute regAttrs() {
		return ElfEntity.regAttrs().add(Attributes.FOLLOW_RANGE, 24.0D);
	}
}
