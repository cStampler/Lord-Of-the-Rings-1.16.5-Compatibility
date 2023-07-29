package lotr.common.util;

import java.util.Calendar;

public class CalendarUtil {
	private static Calendar getCalendar() {
		return Calendar.getInstance();
	}

	private static int getDate() {
		return getCalendar().get(5);
	}

	private static int getMonth() {
		return getCalendar().get(2);
	}

	public static boolean isAprilFools() {
		return getMonth() == 3 && getDate() == 1;
	}

	public static boolean isChristmas() {
		if (getMonth() != 11) {
			return false;
		}
		int date = getDate();
		return date == 24 || date == 25 || date == 26;
	}

	public static boolean isHalloween() {
		return getMonth() == 9 && getDate() == 31;
	}

	public static boolean isNewYearsDay() {
		return getMonth() == 0 && getDate() == 1;
	}
}
