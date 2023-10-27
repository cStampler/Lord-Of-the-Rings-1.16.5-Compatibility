package lotr.client.align;

import java.text.DecimalFormat;
import java.text.ParseException;

import lotr.client.util.LocalizableDecimalFormat;
import lotr.common.LOTRLog;

public class AlignmentFormatter {
	private static final LocalizableDecimalFormat ALIGNMENT_FORMAT = new LocalizableDecimalFormat(new DecimalFormat(",##0.0"), "gui.lotr.alignment.decimal_separator_char", "gui.lotr.alignment.group_separator_char");
	private static final LocalizableDecimalFormat CONQUEST_FORMAT = new LocalizableDecimalFormat(new DecimalFormat(",##0.00"), "gui.lotr.alignment.decimal_separator_char", "gui.lotr.alignment.group_separator_char");

	public static String formatAlignForDisplay(float alignment) {
		return formatAlignForDisplay(alignment, ALIGNMENT_FORMAT, true);
	}

	private static String formatAlignForDisplay(float alignment, LocalizableDecimalFormat dFormat, boolean prefixPlus) {
		String s = dFormat.format(alignment);
		if (prefixPlus && !s.startsWith("-")) {
			s = "+" + s;
		}

		return s;
	}

	public static String formatConqForDisplay(float conq, boolean prefixPlus) {
		return formatAlignForDisplay(conq, CONQUEST_FORMAT, prefixPlus);
	}

	public static float parseDisplayedAlign(String alignmentText) {
		LocalizableDecimalFormat dFormat = ALIGNMENT_FORMAT;
		if (alignmentText.startsWith("+")) {
			alignmentText = alignmentText.substring("+".length());
		}

		try {
			return dFormat.parse(alignmentText).floatValue();
		} catch (ParseException var3) {
			LOTRLog.error("Could not parse alignment value from display string %s", alignmentText);
			var3.printStackTrace();
			return 0.0F;
		}
	}
}
