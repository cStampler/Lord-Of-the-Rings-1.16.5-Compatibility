package lotr.common.item;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRMaterial;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.HoeItem;
import net.minecraft.item.IItemTier;

public class LOTRHoeItem extends HoeItem {
	public LOTRHoeItem(IItemTier tier) {
		this(tier, -1.0F);
	}

	public LOTRHoeItem(IItemTier tier, float speed) {
		super(tier, 0, speed, new Properties().tab(LOTRItemGroups.TOOLS));
	}

	public LOTRHoeItem(LOTRMaterial material) {
		this(material.asTool());
	}

	@Override
	public Multimap getDefaultAttributeModifiers(EquipmentSlotType equipmentSlot) {
		Multimap mapCopy = HashMultimap.create(super.getDefaultAttributeModifiers(equipmentSlot));
		if (mapCopy.containsKey(Attributes.ATTACK_DAMAGE)) {
			Builder builder = ImmutableMultimap.builder();
			builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", 0.0D, Operation.ADDITION));
			mapCopy.removeAll(Attributes.ATTACK_DAMAGE);
			builder.putAll(mapCopy);
			return builder.build();
		}
		return mapCopy;
	}
}
