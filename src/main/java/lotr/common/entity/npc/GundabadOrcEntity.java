package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.goal.NPCMeleeAttackGoal;
import lotr.common.init.LOTRItems;
import net.minecraft.block.Blocks;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.DamageSource;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.*;

public class GundabadOrcEntity extends OrcEntity {
	private static final SpawnEquipmentTable WEAPONS;
	private static final SpawnEquipmentTable HELMETS;
	private static final SpawnEquipmentTable CHESTPLATES;
	private static final SpawnEquipmentTable LEGGINGS;
	private static final SpawnEquipmentTable BOOTS;

	static {
		WEAPONS = SpawnEquipmentTable.of(Items.STONE_SWORD, Items.STONE_AXE, Items.STONE_PICKAXE, LOTRItems.STONE_DAGGER, LOTRItems.STONE_SPEAR, Items.IRON_SWORD, Items.IRON_AXE, Items.IRON_PICKAXE, LOTRItems.IRON_DAGGER, LOTRItems.IRON_SPEAR, LOTRItems.BRONZE_SWORD, LOTRItems.BRONZE_AXE, LOTRItems.BRONZE_PICKAXE, LOTRItems.BRONZE_DAGGER, LOTRItems.BRONZE_SPEAR, LOTRItems.MORDOR_SCIMITAR, LOTRItems.MORDOR_AXE, LOTRItems.MORDOR_PICKAXE, LOTRItems.MORDOR_DAGGER, LOTRItems.MORDOR_SPEAR);
		HELMETS = SpawnEquipmentTable.of(Items.LEATHER_HELMET, LOTRItems.BRONZE_HELMET, LOTRItems.FUR_HELMET, LOTRItems.BONE_HELMET, LOTRItems.MORDOR_HELMET);
		CHESTPLATES = SpawnEquipmentTable.of(Items.LEATHER_CHESTPLATE, LOTRItems.BRONZE_CHESTPLATE, LOTRItems.FUR_CHESTPLATE, LOTRItems.BONE_CHESTPLATE, LOTRItems.MORDOR_CHESTPLATE);
		LEGGINGS = SpawnEquipmentTable.of(Items.LEATHER_LEGGINGS, LOTRItems.BRONZE_LEGGINGS, LOTRItems.FUR_LEGGINGS, LOTRItems.BONE_LEGGINGS, LOTRItems.MORDOR_LEGGINGS);
		BOOTS = SpawnEquipmentTable.of(Items.LEATHER_BOOTS, LOTRItems.BRONZE_BOOTS, LOTRItems.FUR_BOOTS, LOTRItems.BONE_BOOTS, LOTRItems.MORDOR_BOOTS);
	}

	public GundabadOrcEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	protected Goal createOrcAttackGoal() {
		return new NPCMeleeAttackGoal(this, 1.4D);
	}

	@Override
	protected void dropCustomDeathLoot(DamageSource source, int looting, boolean playerHit) {
		super.dropCustomDeathLoot(source, looting, playerHit);
		if (random.nextInt(4000) == 0) {
			this.spawnAtLocation(new ItemStack(Blocks.DIRT).setHoverName(new StringTextComponent("Such Wealth")));
		}

	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(WEAPONS.getRandomItem(random));
		npcItemsInv.setIdleItemsFromMeleeWeapons();
		setItemSlot(EquipmentSlotType.FEET, BOOTS.getRandomItem(random));
		setItemSlot(EquipmentSlotType.LEGS, LEGGINGS.getRandomItem(random));
		setItemSlot(EquipmentSlotType.CHEST, CHESTPLATES.getRandomItem(random));
		if (random.nextInt(3) != 0) {
			setItemSlot(EquipmentSlotType.HEAD, HELMETS.getRandomItem(random));
		}

		if (random.nextFloat() < 0.2F) {
			setItemSlot(EquipmentSlotType.OFFHAND, new ItemStack(Items.SHIELD));
		}

		return spawnData;
	}
}
