package lotr.common.fac;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;

public class MapSquare {
	public final int mapX;
	public final int mapZ;
	public final int radius;

	public MapSquare(int x, int z, int r) {
		mapX = x;
		mapZ = z;
		radius = r;
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(mapX);
		buf.writeVarInt(mapZ);
		buf.writeVarInt(radius);
	}

	public static MapSquare read(JsonObject json) {
		int mapX = json.get("x").getAsInt();
		int mapZ = json.get("z").getAsInt();
		int radius = json.get("radius").getAsInt();
		return new MapSquare(mapX, mapZ, radius);
	}

	public static MapSquare read(PacketBuffer buf) {
		int mapX = buf.readVarInt();
		int mapZ = buf.readVarInt();
		int radius = buf.readVarInt();
		return new MapSquare(mapX, mapZ, radius);
	}
}
