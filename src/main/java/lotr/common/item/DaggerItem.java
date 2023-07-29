package lotr.common.item;

import com.google.common.collect.ImmutableMultimap.Builder;

import lotr.common.init.LOTRMaterial;
import net.minecraft.item.IItemTier;

public class DaggerItem extends LOTRSwordItem {
	public DaggerItem(IItemTier tier) {
		super(tier, 0, -1.6F);
	}

	public DaggerItem(LOTRMaterial material) {
		this(material.asTool());
	}

	@Override
	protected void setupExtendedMeleeAttributes(Builder builder) {
		addReachModifier(builder, -0.5D);
	}
}
