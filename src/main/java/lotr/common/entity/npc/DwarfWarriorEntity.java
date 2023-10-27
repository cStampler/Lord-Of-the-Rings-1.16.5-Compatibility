package lotr.common.entity.npc;

import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.init.LOTRAttributes;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifierMap.MutableAttribute;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class DwarfWarriorEntity extends DwarfEntity {
	private static final SpawnEquipmentTable WEAPONS;

	static {
		WEAPONS = SpawnEquipmentTable.of(LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SWORD, LOTRItems.DWARVEN_SPEAR);
	}

	public DwarfWarriorEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, new ItemStack((IItemProvider) LOTRItems.DWARVEN_BOOTS.get()));
		setItemSlot(EquipmentSlotType.LEGS, new ItemStack((IItemProvider) LOTRItems.DWARVEN_LEGGINGS.get()));
		setItemSlot(EquipmentSlotType.CHEST, new ItemStack((IItemProvider) LOTRItems.DWARVEN_CHESTPLATE.get()));
		if (random.nextInt(10) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, new ItemStack((IItemProvider) LOTRItems.DWARVEN_HELMET.get()));
		}

		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
		return spawnData;
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE;
	}

	public static MutableAttribute regAttrs() {
		return DwarfEntity.regAttrs().add((Attribute) LOTRAttributes.NPC_RANGED_INACCURACY.get(), 0.75D);
	}
}
