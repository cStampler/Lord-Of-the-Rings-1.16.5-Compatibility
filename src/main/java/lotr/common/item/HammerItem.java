package lotr.common.item;

import lotr.common.init.LOTRMaterial;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.IItemTier;
import net.minecraft.item.ItemStack;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class HammerItem extends LOTRSwordItem {
	private boolean metallicSound;

	public HammerItem(IItemTier tier) {
		super(tier, 6, -3.3F);
		metallicSound = true;
	}

	public HammerItem(LOTRMaterial material) {
		this(material.asTool());
	}

	@Override
	public boolean hurtEnemy(ItemStack stack, LivingEntity target, LivingEntity attacker) {
		if (!super.hurtEnemy(stack, target, attacker)) {
			return false;
		}
		int extraKb = 1;
		target.knockback(extraKb * 0.5F, MathHelper.sin(attacker.yRot * 0.017453292F), -MathHelper.cos(attacker.yRot * 0.017453292F));
		if (metallicSound) {
			attacker.level.playSound((PlayerEntity) null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.ANVIL_PLACE, attacker.getSoundSource(), 1.0F, 0.75F);
		}

		return true;
	}

	public HammerItem noClink() {
		metallicSound = false;
		return this;
	}
}
