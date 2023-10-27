package lotr.common.init;

import net.minecraft.item.Food;
import net.minecraft.item.Food.Builder;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;

public class LOTRFoods {
	public static final Food BLUBBER = new Builder().nutrition(2).saturationMod(0.4F).meat().build();
	public static final Food CHERRY = new Builder().nutrition(2).saturationMod(0.2F).build();
	public static final Food CRAM = new Builder().nutrition(8).saturationMod(1.0F).build();
	public static final Food FISH_AND_CHIPS = new Builder().nutrition(12).saturationMod(1.2F).build();
	public static final Food GAMMON = new Builder().nutrition(8).saturationMod(0.8F).meat().build();
	public static final Food LEMBAS = new Builder().nutrition(20).saturationMod(1.5F).build();
	public static final Food LETTUCE = new Builder().nutrition(3).saturationMod(0.4F).build();
	public static final Food MAGGOTY_BREAD = new Builder().nutrition(4).saturationMod(0.5F).build();
	public static final Food MALLORN_NUT = new Builder().nutrition(4).saturationMod(0.4F).build();
	public static final Food MAN_FLESH = new Builder().nutrition(6).saturationMod(0.6F).meat().build();
	public static final Food MAPLE_SYRUP = new Builder().nutrition(2).saturationMod(0.1F).build();
	public static final Food MIRK_SHROOM = new Builder().nutrition(3).saturationMod(0.3F).effect(() -> new EffectInstance(Effects.POISON, 100, 0), 0.3F).build();
	public static final Food MORGUL_SHROOM = new Builder().nutrition(4).saturationMod(0.4F).build();
	public static final Food PEAR = new Builder().nutrition(4).saturationMod(0.3F).build();
	public static final Food SUSPICIOUS_MEAT = new Builder().nutrition(7).saturationMod(0.6F).meat().build();
}
