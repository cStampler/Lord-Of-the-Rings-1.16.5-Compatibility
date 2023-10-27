package lotr.common.item;

import lotr.common.init.LOTRItemGroups;
import lotr.common.init.LOTRMaterial;
import net.minecraft.item.IItemTier;
import net.minecraft.item.PickaxeItem;

public class LOTRPickaxeItem extends PickaxeItem {
	public LOTRPickaxeItem(IItemTier tier) {
		this(tier, 1, -2.8F);
	}

	public LOTRPickaxeItem(IItemTier tier, int atk, float speed) {
		super(tier, atk, speed, new Properties().tab(LOTRItemGroups.TOOLS));
	}

	public LOTRPickaxeItem(LOTRMaterial material) {
		this(material.asTool());
	}
}
