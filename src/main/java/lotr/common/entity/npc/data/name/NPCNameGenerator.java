package lotr.common.entity.npc.data.name;

import java.util.Random;

import net.minecraft.util.ResourceLocation;

@FunctionalInterface
public interface NPCNameGenerator {
	String generateName(Random var1, boolean var2);

	default String getRandomNameFromBank(ResourceLocation bankName, Random rand) {
		NameBank bank = NameBankManager.INSTANCE.fetchLoadedNameBank(bankName);
		return bank.getRandomName(rand);
	}
}
