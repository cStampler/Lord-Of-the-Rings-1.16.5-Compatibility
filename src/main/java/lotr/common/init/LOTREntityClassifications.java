package lotr.common.init;

import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;

public class LOTREntityClassifications {
	public static final EntityClassification NPC = createExtendedEnum("npc", -1, false, false, 128);

	private static final EntityClassification createExtendedEnum(String nameWithoutNamespace, int maxNumber, boolean isPeaceful, boolean isAnimal, int despawnDistance) {
		String enumId = new ResourceLocation("lotr", nameWithoutNamespace.toLowerCase()).toString().toLowerCase();
		String enumName = enumId.toUpperCase();
		return EntityClassification.create(enumName, enumId, maxNumber, isPeaceful, isAnimal, despawnDistance);
	}
}
