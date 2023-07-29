package lotr.common.world.map;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.PlayerEntity;

public class MapPlayerLocation {
	public final GameProfile profile;
	public final double posX;
	public final double posZ;

	public MapPlayerLocation(GameProfile p, double x, double z) {
		profile = p;
		posX = x;
		posZ = z;
	}

	public MapPlayerLocation withFullProfile(GameProfile fullProfile) {
		return new MapPlayerLocation(fullProfile, posX, posZ);
	}

	public static MapPlayerLocation ofPlayer(PlayerEntity player) {
		return new MapPlayerLocation(player.getGameProfile(), player.getX(), player.getZ());
	}
}
