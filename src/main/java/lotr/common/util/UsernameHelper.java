package lotr.common.util;

import java.util.UUID;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.UsernameCache;

public class UsernameHelper {
	public static String getLastKnownUsernameOrFallback(UUID player) {
		String username = UsernameCache.getLastKnownUsername(player);
		return username != null ? username : player.toString();
	}

	public static String getRawUsername(PlayerEntity player) {
		return player.getName().getContents();
	}
}
