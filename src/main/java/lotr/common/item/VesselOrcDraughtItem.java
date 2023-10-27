package lotr.common.item;

import java.util.Arrays;

import lotr.common.fac.FactionType;
import lotr.common.fac.FoodAlignmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.math.MathHelper;

public class VesselOrcDraughtItem extends VesselDrinkItem {
	public VesselOrcDraughtItem(int food, float sat, float dmg, EffectInstance... effs) {
		super(0.0F, food, sat, true, dmg, Arrays.asList(effs));
	}

	@Override
	protected float getBenefitEffectivenessFor(LivingEntity entity) {
		float alignProp = FoodAlignmentHelper.getHighestAlignmentProportion(entity, 100.0F, FactionType.ORC);
		return MathHelper.lerp(alignProp, 0.5F, 1.0F);
	}
}
