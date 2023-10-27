package lotr.common.item;

import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ShovelItem;

public class LOTRShovelItem extends ShovelItem {
	public LOTRShovelItem(IItemTier tier) {
		this(tier, 1.5F, -3.0F);
	}

	public LOTRShovelItem(IItemTier tier, float atk, float speed) {
		super(tier, atk, speed, new Properties().tab(LOTRItemGroups.TOOLS));
	}

	public LOTRShovelItem(LOTRMaterial material) {
		this(material.asTool());
	}
}
