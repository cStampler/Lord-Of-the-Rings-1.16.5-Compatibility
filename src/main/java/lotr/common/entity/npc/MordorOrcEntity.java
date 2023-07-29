package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.*;

public class MordorOrcEntity extends OrcEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.MORDOR_SCIMITAR, LOTRItems.MORDOR_DAGGER, LOTRItems.MORDOR_DAGGER, LOTRItems.MORDOR_SCIMITAR, LOTRItems.MORDOR_SCIMITAR, LOTRItems.MORDOR_PICKAXE, LOTRItems.MORDOR_AXE, LOTRItems.MORDOR_SCIMITAR);
	}

	public MordorOrcEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected Goal createOrcAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.4D);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.MORDOR_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.MORDOR_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.MORDOR_CHESTPLATE.get()));
		if (random.nextInt(5) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.MORDOR_HELMET.get()));
		}

		if (random.nextFloat() < 0.5F) {
			setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack((IItemProvider) LOTRItems.MORDOR_SHIELD.get()));
		}

		return spawnData;
	}

	public static MutableAttribute regAttrs() {
		return OrcEntity.regAttrs().add(Attributes.MAX_HEALTH, 20.0D);
	}
}
