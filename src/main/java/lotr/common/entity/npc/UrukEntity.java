package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
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

public class UrukEntity extends OrcEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.URUK_CLEAVER, LOTRItems.URUK_CLEAVER, LOTRItems.URUK_CLEAVER, LOTRItems.URUK_CLEAVER, LOTRItems.URUK_CLEAVER, LOTRItems.URUK_DAGGER, LOTRItems.URUK_DAGGER, LOTRItems.URUK_CLEAVER, LOTRItems.URUK_SPEAR);
	}

	public UrukEntity(EntityType type, World w) {
		super(type, w);
		isOrcWeakInSun = false;
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
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.URUK_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.URUK_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.URUK_CHESTPLATE.get()));
		if (random.nextInt(10) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.URUK_HELMET.get()));
		}

		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
		return spawnData;
	}

	@Override
	protected float getVoicePitch() {
		return super.getVoicePitch() * 0.75F;
	}

	public static MutableAttribute regAttrs() {
		return OrcEntity.regAttrs().add(Attributes.MAX_HEALTH, 26.0D).add(Attributes.MOVEMENT_SPEED, 0.22D);
	}
}
