package lotr.common;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LOTRLog {
	private static Logger LOGGER;

	public static void debug(String s) {
		LOGGER.debug("lotr: " + s);
	}

	public static void debug(String s, Object... params) {
		LOGGER.debug(String.format("lotr: " + s, params));
	}

	public static void error(String s) {
		LOGGER.error("lotr: " + s);
	}

	public static void error(String s, Object... params) {
		LOGGER.error(String.format("lotr: " + s, params));
	}

	public static void find() {
		LOGGER = LogManager.getLogger();
	}

	public static void info(String s) {
		LOGGER.info("lotr: " + s);
	}

	public static void info(String s, Object... params) {
		LOGGER.info(String.format("lotr: " + s, params));
	}

	public static void warn(String s) {
		LOGGER.warn("lotr: " + s);
	}

	public static void warn(String s, Object... params) {
		LOGGER.warn(String.format("lotr: " + s, params));
	}
}
