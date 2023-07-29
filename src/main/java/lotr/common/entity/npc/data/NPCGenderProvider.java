package lotr.common.entity.npc.data;

import java.util.Random;

@FunctionalInterface
public interface NPCGenderProvider {
	NPCGenderProvider MALE_OR_FEMALE = Random::nextBoolean;
	NPCGenderProvider FEMALE = rand -> false;
	NPCGenderProvider MALE = rand -> true;
	NPCGenderProvider DWARF = rand -> (rand.nextInt(3) != 0);

	boolean isMale(Random var1);
}
