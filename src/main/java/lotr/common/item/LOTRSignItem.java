package lotr.common.item;

import java.util.function.Supplier;

import lotr.common.init.LOTRItemGroups;
import net.minecraft.block.Block;
import net.minecraft.item.SignItem;

public class LOTRSignItem extends SignItem {
	public LOTRSignItem(Supplier standingSign, Supplier wallSign) {
		super(new Properties().stacksTo(16).tab(LOTRItemGroups.DECO), (Block) standingSign.get(), (Block) wallSign.get());
	}
}
