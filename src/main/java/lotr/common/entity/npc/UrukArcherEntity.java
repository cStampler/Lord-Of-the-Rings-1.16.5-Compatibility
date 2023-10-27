package lotr.common.entity.npc;

import lotr.common.entity.npc.ai.AttackGoalsHolder;
import lotr.common.entity.npc.ai.StandardAttackModeUpdaters;
import lotr.common.entity.npc.ai.goal.NPCRangedAttackGoal;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class UrukArcherEntity extends UrukEntity {
	public UrukArcherEntity(EntityType type, World w) {
		super(type, w);
		getNPCCombatUpdater().setAttackModeUpdater(StandardAttackModeUpdaters.meleeRangedSwitching());
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setRangedWeapon(new ItemStack(Items.BOW));
		npcItemsInv.setMeleeWeapon(new ItemStack((IItemProvider) LOTRItems.URUK_DAGGER.get()));
		npcItemsInv.setIdleItemsFromRangedWeapons();
		setItemSlot(EquipmentSlotType.OFFHAND, ItemStack.EMPTY);
		return spawnData;
	}

	@Override
	protected void initialiseAttackGoals(AttackGoalsHolder holder) {
		super.initialiseAttackGoals(holder);
		holder.setRangedAttackGoal(new NPCRangedAttackGoal(this, 1.3D, 20, 16.0F));
	}
}
