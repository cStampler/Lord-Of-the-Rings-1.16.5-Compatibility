package lotr.common.network;

import java.util.*;
import java.util.function.Supplier;

import com.mojang.authlib.GameProfile;

import lotr.common.LOTRMod;
import lotr.common.world.map.MapPlayerLocation;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent.Context;

public class SPacketMapPlayerLocations {
	private final List playerLocations;

	public SPacketMapPlayerLocations(List players) {
		playerLocations = players;
	}

	public static SPacketMapPlayerLocations decode(PacketBuffer buf) {
		List playerLocations = new ArrayList();
		int players = buf.readInt();

		for (int i = 0; i < players; ++i) {
			UUID playerID = buf.readUUID();
			double posX = buf.readDouble();
			double posZ = buf.readDouble();
			playerLocations.add(new MapPlayerLocation(new GameProfile(playerID, (String) null), posX, posZ));
		}

		return new SPacketMapPlayerLocations(playerLocations);
	}

	public static void encode(SPacketMapPlayerLocations packet, PacketBuffer buf) {
		List playerLocations = packet.playerLocations;
		int players = playerLocations.size();
		buf.writeInt(players);
		Iterator var4 = playerLocations.iterator();

		while (var4.hasNext()) {
			MapPlayerLocation loc = (MapPlayerLocation) var4.next();
			buf.writeUUID(loc.profile.getId());
			buf.writeDouble(loc.posX);
			buf.writeDouble(loc.posZ);
		}

	}

	public static void handle(SPacketMapPlayerLocations packet, Supplier context) {
		LOTRMod.PROXY.mapHandlePlayerLocations(packet.playerLocations);
		((Context) context.get()).setPacketHandled(true);
	}
}
