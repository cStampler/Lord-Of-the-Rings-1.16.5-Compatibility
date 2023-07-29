package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.entity.npc.data.NPCGenderProvider;
import lotr.common.entity.npc.util.LeatherDyeUtil;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.*;

public class BreeGuardEntity extends BreeManEntity {
	private static final SpawnEquipmentTable WEAPONS;
	private static final int[] SUIT_COLORS;

	static {
		WEAPONS = SpawnEquipmentTable.of(Items.IRON_SWORD, Items.IRON_SWORD, Items.IRON_SWORD);
		SUIT_COLORS = new int[] { 11373426, 7823440, 5983041, 9535090 };
	}

	public BreeGuardEntity(EntityType type, World w) {
		super(type, w);
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
		if (random.nextInt(3) == 0) {
			setItemSlot(EquipmentSlotType.FEET, new ItemStack(Items.CHAINMAIL_BOOTS));
		} else {
			setItemSlot(EquipmentSlotType.FEET, LeatherDyeUtil.dyeLeather(Items.LEATHER_BOOTS, 3354152));
		}

		if (random.nextInt(3) == 0) {
			setItemSlot(EquipmentSlotType.LEGS, new ItemStack(Items.CHAINMAIL_LEGGINGS));
		} else {
			setItemSlot(EquipmentSlotType.LEGS, LeatherDyeUtil.dyeLeather(Items.LEATHER_LEGGINGS, SUIT_COLORS, random));
		}

		if (random.nextInt(3) == 0) {
			setItemSlot(EquipmentSlotType.CHEST, new ItemStack(Items.CHAINMAIL_CHESTPLATE));
		} else {
			setItemSlot(EquipmentSlotType.CHEST, LeatherDyeUtil.dyeLeather(Items.LEATHER_CHESTPLATE, SUIT_COLORS, random));
		}

		setItemSlot(EquipmentSlotType.HEAD, new ItemStack(Items.IRON_HELMET));
		setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
		return spawnData;
	}

	@Override
	protected NPCGenderProvider getGenderProvider() {
		return NPCGenderProvider.MALE;
	}
}
