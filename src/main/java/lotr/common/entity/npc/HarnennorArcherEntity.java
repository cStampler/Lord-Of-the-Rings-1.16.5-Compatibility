package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.*;
import lotr.common.entity.npc.ai.goal.NPCRangedAttackGoal;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.*;

public class HarnennorArcherEntity extends HarnennorWarriorEntity {
	public HarnennorArcherEntity(EntityType type, World w) {
		super(type, w);
		getNPCCombatUpdater().setAttackModeUpdater(StandardAttackModeUpdaters.meleeRangedSwitching());
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setRangedWeapon(new ItemStack(Items.BOW));
		npcItemsInv.setMeleeWeapon(new ItemStack((IItemProvider) LOTRItems.HARAD_DAGGER.get()));
		npcItemsInv.setIdleItemsFromRangedWeapons();
		setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
		return spawnData;
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		super.initialiseAttackGoals(holder);
		holder.setRangedAttackGoal(new NPCRangedAttackGoal(this, 1.25D, 20, 16.0F));
	}
}
