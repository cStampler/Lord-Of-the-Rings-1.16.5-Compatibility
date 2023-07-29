package lotr.common.item;

import lotr.common.init.*;
import net.minecraft.item.*;

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
