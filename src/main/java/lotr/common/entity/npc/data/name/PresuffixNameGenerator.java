package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class PresuffixNameGenerator implements NPCNameGenerator {
	private final ResourceLocation prefixBank;
	private final ResourceLocation suffixBank;

	public PresuffixNameGenerator(ResourceLocation prefix, ResourceLocation suffix) {
		prefixBank = prefix;
		suffixBank = suffix;
	}

	@Override
	public String generateName(Random rand, boolean male) {
		return getRandomNameFromBank(prefixBank, rand) + getRandomNameFromBank(suffixBank, rand);
	}
}
