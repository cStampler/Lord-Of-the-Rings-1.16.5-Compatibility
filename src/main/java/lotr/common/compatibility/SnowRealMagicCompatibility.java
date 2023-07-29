package lotr.common.compatibility;

import java.lang.reflect.Field;

import lotr.common.LOTRLog;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.fml.ModList;

public class SnowRealMagicCompatibility {
	private static Feature FREEZE_TOP_LAYER;

	public static Feature getFreezeTopLayerFeature() {
		if (FREEZE_TOP_LAYER == null) {
			if (ModList.get().isLoaded("snowrealmagic")) {
				try {
					Class cls = Class.forName("snownee.snow.world.gen.feature.WorldModule");
					Field f = cls.getDeclaredField("FEATURE");
					FREEZE_TOP_LAYER = (Feature) f.get((Object) null);
					LOTRLog.info("Established compatibility with SnowRealMagic mod");
				} catch (NoSuchFieldException | ClassNotFoundException var2) {
					LOTRLog.error("Warning - SnowRealMagic compatibility is out of date - they must have changed their class or field names. You will likely experience a crash.");
					var2.printStackTrace();
				} catch (IllegalAccessException | IllegalArgumentException var3) {
					LOTRLog.error("Error establishing compatibility with SnowRealMagic.");
					var3.printStackTrace();
				}
			} else {
				FREEZE_TOP_LAYER = Feature.FREEZE_TOP_LAYER;
			}
		}

		return FREEZE_TOP_LAYER;
	}
}
