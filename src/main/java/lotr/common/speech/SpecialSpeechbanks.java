package lotr.common.speech;

import java.util.Optional;
import java.util.Random;

import lotr.common.util.CalendarUtil;
import net.minecraft.util.ResourceLocation;

public class SpecialSpeechbanks {
	public static final ResourceLocation CHRISTMAS = new ResourceLocation("lotr", "special/christmas");
	public static final ResourceLocation NEW_YEAR = new ResourceLocation("lotr", "special/new_year");
	public static final ResourceLocation APRIL_FOOL = new ResourceLocation("lotr", "special/april_fool");
	public static final ResourceLocation HALLOWEEN = new ResourceLocation("lotr", "special/halloween");
	public static final ResourceLocation SMILEBC = new ResourceLocation("lotr", "special/smilebc");

	public static Optional getSpecialSpeechbank(Random rand) {
		if (rand.nextInt(8) == 0) {
			if (CalendarUtil.isChristmas()) {
				return Optional.of(CHRISTMAS);
			}

			if (CalendarUtil.isNewYearsDay()) {
				return Optional.of(NEW_YEAR);
			}

			if (CalendarUtil.isAprilFools()) {
				return Optional.of(APRIL_FOOL);
			}

			if (CalendarUtil.isHalloween()) {
				return Optional.of(HALLOWEEN);
			}
		}

		return rand.nextInt(10000) == 0 ? Optional.of(SMILEBC) : Optional.empty();
	}
}
