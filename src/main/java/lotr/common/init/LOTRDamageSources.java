package lotr.common.init;

import javax.annotation.Nullable;

import net.minecraft.entity.Entity;
import net.minecraft.util.*;

public class LOTRDamageSources {
	public static final DamageSource PLANT = new DamageSource("lotr.plant").bypassArmor();
	public static final DamageSource FROST = new DamageSource("lotr.frost").bypassArmor();
	public static final DamageSource POISON_DRINK = new DamageSource("lotr.poisonDrink").bypassArmor().setMagic();
	public static final DamageSource STALAGMITE = new DamageSource("lotr.stalagmite").bypassArmor();
	public static final DamageSource QUAGMIRE = new DamageSource("lotr.quagmire").bypassArmor();

	public static DamageSource causeThrownSpearDamage(Entity spear, @Nullable Entity shooter) {
		return new IndirectEntityDamageSource("lotr.thrownSpear", spear, shooter).setProjectile();
	}
}
