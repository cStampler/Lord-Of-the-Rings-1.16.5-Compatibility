package lotr.common.item;

import lotr.common.init.*;
import net.minecraft.item.*;

public class LOTRAxeItem extends AxeItem {
	public LOTRAxeItem(IItemTier tier) {
		this(tier, 6.0F, -3.1F);
	}

	public LOTRAxeItem(IItemTier tier, float atk, float speed) {
		super(tier, atk, speed, new Properties().tab(LOTRItemGroups.TOOLS));
	}

	public LOTRAxeItem(LOTRMaterial material) {
		this(material.asTool());
	}
}
