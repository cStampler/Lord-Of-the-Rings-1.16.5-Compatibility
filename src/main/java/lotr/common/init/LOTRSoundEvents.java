package lotr.common.init;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.registries.ForgeRegistries;

public class LOTRSoundEvents {
	private static final List<SoundEvent> toRegister = new ArrayList<SoundEvent>();
	public static final SoundEvent AMBIENCE_WIND = preRegSound("ambience.weather.wind");
	public static final SoundEvent NEW_RAIN = preRegSound("ambience.weather.rain");
	public static final SoundEvent NEW_THUNDER = preRegSound("ambience.weather.thunder");
	public static final SoundEvent CERAMIC_BREAK = preRegSound("block.ceramic.break");
	public static final SoundEvent GATE_CLOSE = preRegSound("block.gate.close");
	public static final SoundEvent GATE_OPEN = preRegSound("block.gate.open");
	public static final SoundEvent STONE_GATE_CLOSE = preRegSound("block.gate.stone_close");
	public static final SoundEvent STONE_GATE_OPEN = preRegSound("block.gate.stone_open");
	public static final SoundEvent PALANTIR_PONDER = preRegSound("block.palantir.ponder");
	public static final SoundEvent TREASURE_BREAK = preRegSound("block.treasure.break");
	public static final SoundEvent TREASURE_FALL = preRegSound("block.treasure.fall");
	public static final SoundEvent TREASURE_HIT = preRegSound("block.treasure.hit");
	public static final SoundEvent TREASURE_PLACE = preRegSound("block.treasure.place");
	public static final SoundEvent TREASURE_STEP = preRegSound("block.treasure.step");
	public static final SoundEvent DWARF_ATTACK = preRegSound("entity.dwarf.attack");
	public static final SoundEvent DWARF_DEATH = preRegSound("entity.dwarf.death");
	public static final SoundEvent DWARF_HURT = preRegSound("entity.dwarf.hurt");
	public static final SoundEvent DWARF_KILL = preRegSound("entity.dwarf.kill");
	public static final SoundEvent ORC_AEUGH = preRegSound("entity.orc.aeugh");
	public static final SoundEvent ORC_DEATH = preRegSound("entity.orc.death");
	public static final SoundEvent ORC_GHASH = preRegSound("entity.orc.ghash");
	public static final SoundEvent ORC_HURT = preRegSound("entity.orc.hurt");
	public static final SoundEvent WARG_AMBIENT = preRegSound("entity.warg.ambient");
	public static final SoundEvent WARG_ATTACK = preRegSound("entity.warg.attack");
	public static final SoundEvent WARG_DEATH = preRegSound("entity.warg.death");
	public static final SoundEvent WARG_HURT = preRegSound("entity.warg.hurt");
	public static final SoundEvent FAST_TRAVEL = preRegSound("event.fast_travel");
	public static final SoundEvent PLEDGE = preRegSound("event.pledge");
	public static final SoundEvent TRADE = preRegSound("event.trade");
	public static final SoundEvent UNPLEDGE = preRegSound("event.unpledge");
	public static final SoundEvent MUG_FILL = preRegSound("item.mug_fill");
	public static final SoundEvent LARGE_POUCH_CLOSE = preRegSound("item.pouch.large.close");
	public static final SoundEvent LARGE_POUCH_OPEN = preRegSound("item.pouch.large.open");
	public static final SoundEvent MEDIUM_POUCH_CLOSE = preRegSound("item.pouch.medium.close");
	public static final SoundEvent MEDIUM_POUCH_OPEN = preRegSound("item.pouch.medium.open");
	public static final SoundEvent RESTOCK_POUCHES = preRegSound("item.pouch.restock");
	public static final SoundEvent SMALL_POUCH_CLOSE = preRegSound("item.pouch.small.close");
	public static final SoundEvent SMALL_POUCH_OPEN = preRegSound("item.pouch.small.open");
	public static final SoundEvent SMOKE_PUFF = preRegSound("item.smoke_puff");

	private static SoundEvent preRegSound(String key) {
		ResourceLocation res = new ResourceLocation("lotr", key);
		SoundEvent snd = new SoundEvent(res).setRegistryName(res);
		toRegister.add(snd);
		return snd;
	}

	public static void register() {
		toRegister.forEach(snd -> {
			ForgeRegistries.SOUND_EVENTS.register((SoundEvent) snd);
		});
	}
}
