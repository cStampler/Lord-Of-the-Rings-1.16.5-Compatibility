package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class PresuffixBlendNameGenerator implements NPCNameGenerator {
	private final ResourceLocation prefixBank;
	private final ResourceLocation suffixBank;

	public PresuffixBlendNameGenerator(ResourceLocation prefix, ResourceLocation suffix) {
		prefixBank = prefix;
		suffixBank = suffix;
	}

	@Override
	public String generateName(Random rand, boolean male) {
		String prefix = getRandomNameFromBank(prefixBank, rand);
		String suffix = getRandomNameFromBank(suffixBank, rand);
		if (prefix.length() > 0 && suffix.length() > 0 && prefix.charAt(prefix.length() - 1) == suffix.charAt(0)) {
			suffix = suffix.substring(1);
		}

		return prefix + suffix;
	}
}
