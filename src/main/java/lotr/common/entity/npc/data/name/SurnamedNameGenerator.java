package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

public class SurnamedNameGenerator implements NPCNameGenerator {
	private final NPCNameGenerator forenameGenerator;
	private final NPCNameGenerator surnameGenerator;
	private final float surnameChance;

	public SurnamedNameGenerator(NPCNameGenerator forenameGenerator, NPCNameGenerator surnameGenerator) {
		this(forenameGenerator, surnameGenerator, 1.0F);
	}

	public SurnamedNameGenerator(NPCNameGenerator forenameGenerator, NPCNameGenerator surnameGenerator, float surnameChance) {
		this.forenameGenerator = forenameGenerator;
		this.surnameGenerator = surnameGenerator;
		this.surnameChance = surnameChance;
	}

	public SurnamedNameGenerator(ResourceLocation maleForename, ResourceLocation femaleForename, ResourceLocation surname) {
		this(maleForename, femaleForename, surname, 1.0F);
	}

	public SurnamedNameGenerator(ResourceLocation maleForename, ResourceLocation femaleForename, ResourceLocation surname, float surnameChance) {
		this(new SimpleGenderedNameGenerator(maleForename, femaleForename), new SimpleNameGenerator(surname), surnameChance);
	}

	@Override
	public String generateName(Random rand, boolean male) {
		String forename = forenameGenerator.generateName(rand, male);
		return rand.nextFloat() < surnameChance ? String.format("%s %s", forename, surnameGenerator.generateName(rand, male)) : forename;
	}
}
