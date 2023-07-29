package lotr.common.item;

import lotr.common.init.*;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.*;

public class LOTRShieldItem extends ShieldItem {
	public LOTRShieldItem(LOTRMaterial material) {
		this(new Properties().defaultDurability(calculateShieldDurability(material)).tab(LOTRItemGroups.COMBAT));
	}

	public LOTRShieldItem(Properties properties) {
		super(properties);
	}

	@Override
	public boolean isShield(ItemStack stack, LivingEntity entity) {
		return true;
	}

	private static int calculateShieldDurability(LOTRMaterial material) {
		int standardShieldDura = new ItemStack(Items.SHIELD).getMaxDamage();
		int materialDura = material.asTool().getUses();
		return Math.max(materialDura, standardShieldDura);
	}
}
