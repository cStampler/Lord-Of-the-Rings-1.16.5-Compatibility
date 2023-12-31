package lotr.common.time;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.util.text.ITextComponent;

public abstract class MiddleEarthCalendar<D extends MiddleEarthCalendar.AbstractDate> {
	public static final int SECOND_AGE_LENGTH = 3441;
	public static final int THIRD_AGE_LENGTH = 3021;
	public static final int SHIRE_RECKONING_OFFSET_FROM_THIRD_AGE = -1600;
	public static final int THIRD_AGE_CURRENT;
	public static int currentDay;
	static {
		THIRD_AGE_CURRENT = ShireReckoning.START_DATE.year - -1600;
		currentDay = 0;
	}

	private Map<Integer, D> cachedDates = new HashMap<>();

	protected abstract D computeDateForCache(int var1);

	public final D getCurrentDate() {
		return getDate(currentDay);
	}

	public final ITextComponent getCurrentDateAndYearLongform() {
		return getCurrentDate().getDateAndYearName(false);
	}

	public final ITextComponent getCurrentDateAndYearShortform() {
		return getCurrentDate().getDateAndYearName(true);
	}

	public final D getDate(int day) {
		D date = cachedDates.get(day);
		if (date == null) {
			date = computeDateForCache(day);
			cachedDates.put(day, date);
		}

		return date;
	}

	public abstract static class AbstractDate {
		protected abstract ITextComponent getDateAndYearName(boolean var1);

		protected abstract ITextComponent getDateName(boolean var1);

		protected abstract ITextComponent getYearName(boolean var1);
	}
}
