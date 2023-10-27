package lotr.common.item;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import javax.annotation.Nullable;

import lotr.common.LOTRLog;
import lotr.common.init.LOTREntities;
import lotr.common.init.LOTRItemGroups;
import net.minecraft.block.DispenserBlock;
import net.minecraft.dispenser.IDispenseItemBehavior;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class LOTRSpawnEggItem extends SpawnEggItem {
	public static final Set ALL_MOD_SPAWN_EGGS = new HashSet();
	private static final Map POST_ENTITY_REGISTRY_SPAWN_EGGS = new HashMap();
	private final Supplier entityTypeSup;

	public LOTRSpawnEggItem(LOTREntities.SpawnEggInfo egg) {
		super((EntityType) null, egg.colors.primaryColor, egg.colors.secondaryColor, new Properties().tab(LOTRItemGroups.SPAWNERS));
		entityTypeSup = egg.regType;
		ALL_MOD_SPAWN_EGGS.add(this);
	}

	@Override
	public EntityType getType(@Nullable CompoundNBT nbt) {
		EntityType superResult = super.getType(nbt);
		return superResult == null ? (EntityType) entityTypeSup.get() : superResult;
	}

	public static void afterEntityRegistry() {
		ALL_MOD_SPAWN_EGGS.forEach(spawnEgg -> {
			EntityType entityType = (EntityType) ((LOTRSpawnEggItem) spawnEgg).entityTypeSup.get();
			POST_ENTITY_REGISTRY_SPAWN_EGGS.put(entityType, spawnEgg);
		});

		Map dispenseBehaviorRegistry;
		try {
			dispenseBehaviorRegistry = (Map) ObfuscationReflectionHelper.getPrivateValue(SpawnEggItem.class, null, "BY_ID");
			dispenseBehaviorRegistry.putAll(POST_ENTITY_REGISTRY_SPAWN_EGGS);
		} catch (Exception var3) {
			LOTRLog.error("Exception adding mod spawn eggs to the vanilla map");
			var3.printStackTrace();
		}

		try {
			dispenseBehaviorRegistry = (Map) ObfuscationReflectionHelper.getPrivateValue(DispenserBlock.class, null, "DISPENSER_REGISTRY");
			IDispenseItemBehavior vanillaSpawnEggDispense = (IDispenseItemBehavior) dispenseBehaviorRegistry.get(Items.COW_SPAWN_EGG);
			final Map amogus = dispenseBehaviorRegistry;
			ALL_MOD_SPAWN_EGGS.forEach(spawnEgg -> {
				amogus.put(spawnEgg, vanillaSpawnEggDispense);
			});
		} catch (Exception var2) {
			LOTRLog.error("Exception adding mod spawn eggs' dispenser behaviour");
			var2.printStackTrace();
		}

	}

	public static LOTRSpawnEggItem getModSpawnEgg(EntityType type) {
		return (LOTRSpawnEggItem) POST_ENTITY_REGISTRY_SPAWN_EGGS.get(type);
	}
}
