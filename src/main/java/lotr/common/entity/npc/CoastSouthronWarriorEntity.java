package lotr.common.entity.npc;

import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.init.*;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.*;

public class CoastSouthronWarriorEntity extends CoastSouthronEntity {
	private static final SpawnEquipmentTable IRON_WEAPONS;
	private static final SpawnEquipmentTable BRONZE_WEAPONS;
	static {
		IRON_WEAPONS = SpawnEquipmentTable.of(LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_DAGGER, LOTRItems.UMBAR_DAGGER, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SCIMITAR, LOTRItems.UMBAR_SPEAR);
		BRONZE_WEAPONS = SpawnEquipmentTable.of(LOTRItems.HARAD_SWORD, LOTRItems.HARAD_SWORD, LOTRItems.HARAD_SWORD, LOTRItems.HARAD_DAGGER, LOTRItems.HARAD_DAGGER, LOTRItems.HARAD_SWORD, LOTRItems.HARAD_SPEAR);
	}

	public CoastSouthronWarriorEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected void addNPCTargetingAI() {
		addAggressiveTargetingGoals();
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		if (random.nextInt(3) == 0) {
			npcItemsInv.setMeleeWeapon(BRONZE_WEAPONS.getRandomItem(random));
		} else {
			npcItemsInv.setMeleeWeapon(IRON_WEAPONS.getRandomItem(random));
		}

		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.HARAD_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.HARAD_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.HARAD_CHESTPLATE.get()));
		if (random.nextInt(10) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.HARAD_HELMET.get()));
		}

		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack((IItemProvider) LOTRItems.COAST_SOUTHRON_SHIELD.get()));
		return spawnData;
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE;
	}

	public static MutableAttribute regAttrs() {
		return AbstractMannishEntity.regAttrs().add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get(), 0.75D);
	}
}
