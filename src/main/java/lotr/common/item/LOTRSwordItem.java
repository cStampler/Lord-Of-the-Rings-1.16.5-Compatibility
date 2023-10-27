package lotr.common.item;

import java.util.UUID;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableMultimap.Builder;
import com.google.common.collect.Multimap;

import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRMaterial;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.IItemTier;
import net.minecraft.item.SwordItem;
import net.minecraftforge.common.ForgeMod;

public class LOTRSwordItem extends SwordItem {
	protected static final UUID ATTACK_REACH_MODIFIER = UUID.fromString("8e3d7974-9a16-47d5-a6b1-02f248a5aa32");
	private Multimap extendedAttributeModifiers;

	public LOTRSwordItem(IItemTier tier) {
		this(tier, 3, -2.4F);
	}

	public LOTRSwordItem(IItemTier tier, int atk, float speed) {
		super(tier, atk, speed, new Properties().tab(LOTRItemGroups.COMBAT));
	}

	public LOTRSwordItem(LOTRMaterial material) {
		this(material.asTool());
	}

	public LOTRSwordItem(LOTRMaterial material, int extraAtk) {
		this(material.asTool(), 3 + extraAtk, -2.4F);
	}

	protected final void addReachModifier(Builder builder, double reach) {
		builder.put(ForgeMod.REACH_DISTANCE.get(), new AttributeModifier(ATTACK_REACH_MODIFIER, "Weapon modifier", reach, Operation.ADDITION));
	}

	private Multimap buildExtendedAttributeModifiers() {
		Builder builder = ImmutableMultimap.builder();
		Multimap baseSwordModifiers = super.getDefaultAttributeModifiers(EquipmentSlotType.MAINHAND);
		builder.putAll(baseSwordModifiers);
		setupExtendedMeleeAttributes(builder);
		return builder.build();
	}

	@Override
	public Multimap getDefaultAttributeModifiers(EquipmentSlotType slot) {
		if (extendedAttributeModifiers == null) {
			extendedAttributeModifiers = buildExtendedAttributeModifiers();
		}

		return slot == EquipmentSlotType.MAINHAND ? extendedAttributeModifiers : super.getDefaultAttributeModifiers(slot);
	}

	protected void setupExtendedMeleeAttributes(Builder builder) {
		addReachModifier(builder, 0.0D);
	}
}
