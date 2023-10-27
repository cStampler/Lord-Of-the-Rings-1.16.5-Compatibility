package lotr.common.entity.npc;

import lotr.common.entity.npc.data.name.NPCNameGenerator;
import lotr.common.entity.npc.data.name.NPCNameGenerators;
import lotr.common.init.LOTRItems;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.SpawnReason;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IServerWorld;
import net.minecraft.world.World;

public class GaladhrimElfEntity extends ElfEntity {
	public GaladhrimElfEntity(EntityType type, World w) {
		super(type, w);
	}

	@Override
	public ILivingEntityData finalizeSpawn(IServerWorld sw, DifficultyInstance diff, SpawnReason reason, ILivingEntityData spawnData, CompoundNBT dataTag) {
		spawnData = super.finalizeSpawn(sw, diff, reason, spawnData, dataTag);
		npcItemsInv.setMeleeWeapon(new ItemStack((IItemProvider) LOTRItems.GALADHRIM_DAGGER.get()));
		npcItemsInv.setRangedWeapon(new ItemStack(Items.BOW));
		npcItemsInv.clearIdleItem();
		return spawnData;
	}

	@Override
	protected NPCNameGenerator getNameGenerator() {
		return NPCNameGenerators.SINDARIN;
	}
}
