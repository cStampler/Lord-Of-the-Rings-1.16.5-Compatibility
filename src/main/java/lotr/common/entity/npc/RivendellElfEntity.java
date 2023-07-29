package lotr.common.entity.npc;

import lotr.common.entity.npc.data.name.*;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.*;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.*;

public class RivendellElfEntity extends ElfEntity {
	public RivendellElfEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(new ItemStack((IItemProvider) LOTRItems.RIVENDELL_DAGGER.get()));
		npcItemsInv.setRangedWeapon(new ItemStack(Items.BOW));
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.SINDARIN;
	}
}
