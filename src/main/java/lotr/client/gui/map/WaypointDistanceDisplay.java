package lotr.client.gui.map;

import java.text.DecimalFormat;

import lotr.client.util.LocalizableDecimalFormat;
import lotr.common.config.LOTRConfig;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

public class WaypointDistanceDisplay {
	private static final LocalizableDecimalFormat DECIMAL_FORMAT = new LocalizableDecimalFormat(new DecimalFormat(",##0.#"), "gui.lotr.map.distance.decimal_separator_char", "gui.lotr.map.distance.group_separator_char");

	public static ITextComponent getDistanceText(double dist) {
		return (Boolean) LOTRConfig.CLIENT.imperialWaypointDistances.get() ? getImperialDistance(dist) : getMetricDistance(dist);
	}

	private static ITextComponent getImperialDistance(double dist) {
		int yards = (int) Math.round(dist / 0.9144D);
		if (yards > 1760.0D) {
			double miles = yards / 1760.0D;
			double leagues = miles / 3.0D;
			return leagues > 10.0D ? new TranslationTextComponent("gui.lotr.map.distance.leagues", DECIMAL_FORMAT.format(leagues)) : new TranslationTextComponent("gui.lotr.map.distance.miles", DECIMAL_FORMAT.format(miles));
		}
		return new TranslationTextComponent("gui.lotr.map.distance.yards", DECIMAL_FORMAT.format(yards));
	}

	private static ITextComponent getMetricDistance(double dist) {
		int m = (int) Math.round(dist);
		if (m > 1000) {
			double km = m / 1000.0D;
			return new TranslationTextComponent("gui.lotr.map.distance.km", DECIMAL_FORMAT.format(km));
		}
		return new TranslationTextComponent("gui.lotr.map.distance.m", DECIMAL_FORMAT.format(m));
	}
}
