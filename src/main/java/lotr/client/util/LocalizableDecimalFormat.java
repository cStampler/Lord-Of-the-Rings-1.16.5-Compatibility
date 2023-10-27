package lotr.client.util;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;

import net.minecraft.client.resources.I18n;

public class LocalizableDecimalFormat {
	private final DecimalFormat format;
	private final String keyDecimalSeparatorChar;
	private final String keyGroupSeparatorChar;
	private final DecimalFormatSymbols formatSymbols = new DecimalFormatSymbols();

	public LocalizableDecimalFormat(DecimalFormat format, String keyDecimalSeparatorChar, String keyGroupSeparatorChar) {
		this.format = format;
		this.keyDecimalSeparatorChar = keyDecimalSeparatorChar;
		this.keyGroupSeparatorChar = keyGroupSeparatorChar;
	}

	public String format(double number) {
		localizeFormat();
		return format.format(number);
	}

	public String format(long number) {
		localizeFormat();
		return format.format(number);
	}

	private void localizeFormat() {
		char decimalSeparatorChar = '.';
		char groupSeparatorChar = ',';
		String decimalSeparator = I18n.get(keyDecimalSeparatorChar);
		if (decimalSeparator.length() == 1) {
			decimalSeparatorChar = decimalSeparator.charAt(0);
		}

		String groupSeparator = I18n.get(keyGroupSeparatorChar);
		if (groupSeparator.length() == 1) {
			groupSeparatorChar = groupSeparator.charAt(0);
		}

		formatSymbols.setDecimalSeparator(decimalSeparatorChar);
		formatSymbols.setGroupingSeparator(groupSeparatorChar);
		format.setDecimalFormatSymbols(formatSymbols);
	}

	public Number parse(String source) throws ParseException {
		localizeFormat();
		return format.parse(source);
	}
}
