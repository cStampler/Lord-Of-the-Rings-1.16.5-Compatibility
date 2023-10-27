package lotr.client.render.model;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;

public class BlockModelUtil {
	public static boolean validateTextureString(String s) {
		if (s.startsWith("#")) {
			return true;
		}
		try {
			new ResourceLocation(s);
			return true;
		} catch (ResourceLocationException var2) {
			return false;
		}
	}
}
