package lotr.common.time;

import com.google.common.math.IntMath;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class ShireReckoning extends MiddleEarthCalendar {
	public static final ShireReckoning INSTANCE = new ShireReckoning();
	public static final ShireReckoning.ShireDate START_DATE;

	static {
		START_DATE = new ShireReckoning.ShireDate(1401, ShireReckoning.Month.HALIMATH, 22);
	}

	@Override
	protected ShireReckoning.ShireDate computeDateForCache(int day) {
		ShireReckoning.ShireDate date = START_DATE.copy();
		int i;
		if (day < 0) {
			for (i = 0; i < -day; ++i) {
				date = date.decrement();
			}
		} else {
			for (i = 0; i < day; ++i) {
				date = date.increment();
			}
		}

		return date;
	}

	public Season getSeason() {
		return ((ShireReckoning.ShireDate) getCurrentDate()).month.season;
	}

	public static boolean isLeapYear(int year) {
		return year % 4 == 0 && year % 100 != 0;
	}

	public enum Day {
		STERDAY("sterday"), SUNDAY("sunday"), MONDAY("monday"), TREWSDAY("trewsday"), HEVENSDAY("hevensday"), MERSDAY("mersday"), HIGHDAY("highday");

		private String name;

		Day(String s) {
			name = s;
		}

		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("lotr.date.shire.day." + name);
		}
	}

	public enum Month {
		YULE_2("yule2", 1, Season.WINTER), AFTERYULE("afteryule", 30, Season.WINTER), SOLMATH("solmath", 30, Season.WINTER), RETHE("rethe", 30, Season.WINTER), ASTRON("astron", 30, Season.SPRING), THRIMIDGE("thrimidge", 30, Season.SPRING), FORELITHE("forelithe", 30, Season.SPRING), LITHE_1("lithe1", 1, Season.SPRING), MIDYEARSDAY("midyearsday", 1, Season.SUMMER, false, false), OVERLITHE("overlithe", 1, Season.SUMMER, false, true), LITHE_2("lithe2", 1, Season.SUMMER), AFTERLITHE("afterlithe", 30, Season.SUMMER), WEDMATH("wedmath", 30, Season.SUMMER), HALIMATH("halimath", 30, Season.SUMMER), WINTERFILTH("winterfilth", 30, Season.AUTUMN), BLOTMATH("blotmath", 30, Season.AUTUMN), FOREYULE("foreyule", 30, Season.AUTUMN), YULE_1("yule1", 1, Season.AUTUMN);

		private String name;
		public int days;
		public boolean hasWeekdayName;
		public boolean isLeapYear;
		public Season season;

		Month(String s, int i, Season se) {
			this(s, i, se, true, false);
		}

		Month(String s, int i, Season se, boolean flag, boolean flag1) {
			name = s;
			days = i;
			hasWeekdayName = flag;
			isLeapYear = flag1;
			season = se;
		}

		public ITextComponent getDisplayName() {
			return new TranslationTextComponent("lotr.date.shire.month." + name);
		}

		public boolean isSingleDay() {
			return days == 1;
		}
	}

	public static class ShireDate extends MiddleEarthCalendar.AbstractDate {
		public final int year;
		public final ShireReckoning.Month month;
		public final int monthDate;
		private ShireReckoning.Day day;

		public ShireDate(int y, ShireReckoning.Month m, int d) {
			year = y;
			month = m;
			monthDate = d;
		}

		public ShireReckoning.ShireDate copy() {
			return new ShireReckoning.ShireDate(year, month, monthDate);
		}

		public ShireReckoning.ShireDate decrement() {
			int newYear = year;
			ShireReckoning.Month newMonth = month;
			int newDate = monthDate;
			--newDate;
			if (newDate < 0) {
				int monthID = newMonth.ordinal();
				--monthID;
				if (monthID < 0) {
					monthID = ShireReckoning.Month.values().length - 1;
					--newYear;
				}

				newMonth = ShireReckoning.Month.values()[monthID];
				if (newMonth.isLeapYear && !ShireReckoning.isLeapYear(newYear)) {
					--monthID;
					newMonth = ShireReckoning.Month.values()[monthID];
				}

				newDate = newMonth.days;
			}

			return new ShireReckoning.ShireDate(newYear, newMonth, newDate);
		}

		@Override
		protected ITextComponent getDateAndYearName(boolean shortform) {
			return new TranslationTextComponent("lotr.date.shire.format.dateAndYear", getDateName(shortform), getYearName(shortform));
		}

		@Override
		protected ITextComponent getDateName(boolean shortform) {
			ShireReckoning.Day day = getDay();
			if (month.isSingleDay()) {
				return month.hasWeekdayName ? new TranslationTextComponent("lotr.date.shire.format.weekdayOfSingleDayMonth", day.getDisplayName(), month.getDisplayName()) : new TranslationTextComponent("lotr.date.shire.format.singleDayMonth", month.getDisplayName());
			}
			return new TranslationTextComponent("lotr.date.shire.format.weekdayOfMonth", day.getDisplayName(), monthDate, month.getDisplayName());
		}

		public ShireReckoning.Day getDay() {
			if (!month.hasWeekdayName) {
				return null;
			}
			if (day == null) {
				int yearDay = 0;
				int monthID = month.ordinal();

				int dayID;
				for (dayID = 0; dayID < monthID; ++dayID) {
					ShireReckoning.Month m = ShireReckoning.Month.values()[dayID];
					if (m.hasWeekdayName) {
						yearDay += m.days;
					}
				}

				yearDay += monthDate;
				dayID = IntMath.mod(yearDay - 1, ShireReckoning.Day.values().length);
				day = ShireReckoning.Day.values()[dayID];
			}

			return day;
		}

		@Override
		protected ITextComponent getYearName(boolean shortform) {
			ITextComponent yearPrefix = shortform ? new TranslationTextComponent("lotr.date.shire.short") : new TranslationTextComponent("lotr.date.shire.long");
			return new TranslationTextComponent("lotr.date.shire.format.year", yearPrefix, year);
		}

		public ShireReckoning.ShireDate increment() {
			int newYear = year;
			ShireReckoning.Month newMonth = month;
			int newDate = monthDate;
			++newDate;
			if (newDate > newMonth.days) {
				newDate = 1;
				int monthID = newMonth.ordinal();
				++monthID;
				if (monthID >= ShireReckoning.Month.values().length) {
					monthID = 0;
					++newYear;
				}

				newMonth = ShireReckoning.Month.values()[monthID];
				if (newMonth.isLeapYear && !ShireReckoning.isLeapYear(newYear)) {
					++monthID;
					newMonth = ShireReckoning.Month.values()[monthID];
				}
			}

			return new ShireReckoning.ShireDate(newYear, newMonth, newDate);
		}
	}
}
