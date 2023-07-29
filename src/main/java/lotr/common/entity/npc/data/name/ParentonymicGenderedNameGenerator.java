package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class ParentonymicGenderedNameGenerator implements NPCNameGenerator {
	private final ResourceLocation maleNameBank;
	private final ResourceLocation femaleNameBank;

	public ParentonymicGenderedNameGenerator(ResourceLocation male, ResourceLocation female) {
		maleNameBank = male;
		femaleNameBank = female;
	}

	@Override
	public String generateName(Random rand, boolean male) {
		return male ? String.format("%s son of %s", getRandomNameFromBank(maleNameBank, rand), getRandomNameFromBank(maleNameBank, rand)) : String.format("%s daughter of %s", getRandomNameFromBank(femaleNameBank, rand), getRandomNameFromBank(femaleNameBank, rand));
	}
}
