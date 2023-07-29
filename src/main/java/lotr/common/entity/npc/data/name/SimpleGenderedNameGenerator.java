package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class SimpleGenderedNameGenerator implements NPCNameGenerator {
	private final ResourceLocation maleNameBank;
	private final ResourceLocation femaleNameBank;

	public SimpleGenderedNameGenerator(ResourceLocation male, ResourceLocation female) {
		maleNameBank = male;
		femaleNameBank = female;
	}

	@Override
	public String generateName(Random rand, boolean male) {
		return male ? getRandomNameFromBank(maleNameBank, rand) : getRandomNameFromBank(femaleNameBank, rand);
	}
}
