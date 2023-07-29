package lotr.common.world.map;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.MathHelper;

public class WaterLatitudeSettings {
	private final float coldWaterZ;
	private final float warmWaterZ;
	private final int coldWaterZ_world;
	private final int warmWaterZ_world;
	private final float iceStartZ;
	private final float iceFullZ;
	private final int iceStartZ_world;
	private final int iceFullZ_world;
	private final float sandyStartZ;
	private final float sandyFullZ;
	private final int sandyStartZ_world;
	private final int sandyFullZ_world;
	private final float coralStartZ;
	private final float coralFullZ;
	private final int coralStartZ_world;
	private final int coralFullZ_world;

	private WaterLatitudeSettings(MapSettings map, float coldWaterZ, float warmWaterZ, float iceStartZ, float iceFullZ, float sandyStartZ, float sandyFullZ, float coralStartZ, float coralFullZ) {
		this.coldWaterZ = coldWaterZ;
		this.warmWaterZ = warmWaterZ;
		coldWaterZ_world = map.mapToWorldZ(coldWaterZ);
		warmWaterZ_world = map.mapToWorldZ(warmWaterZ);
		this.iceStartZ = iceStartZ;
		this.iceFullZ = iceFullZ;
		iceStartZ_world = map.mapToWorldZ(iceStartZ);
		iceFullZ_world = map.mapToWorldZ(iceFullZ);
		this.sandyStartZ = sandyStartZ;
		this.sandyFullZ = sandyFullZ;
		sandyStartZ_world = map.mapToWorldZ(sandyStartZ);
		sandyFullZ_world = map.mapToWorldZ(sandyFullZ);
		this.coralStartZ = coralStartZ;
		this.coralFullZ = coralFullZ;
		coralStartZ_world = map.mapToWorldZ(coralStartZ);
		coralFullZ_world = map.mapToWorldZ(coralFullZ);
	}

	public int getColdWaterZ_world() {
		return coldWaterZ_world;
	}

	public float getCoralForLatitude(int z) {
		return getStartToFullProgress(z, coralStartZ_world, coralFullZ_world);
	}

	public int getCoralFullZ_world() {
		return coralFullZ_world;
	}

	public int getCoralStartZ_world() {
		return coralStartZ_world;
	}

	public float getIceCoverageForLatitude(int z) {
		return getStartToFullProgress(z, iceStartZ_world, iceFullZ_world);
	}

	public int getIceFullZ_world() {
		return iceFullZ_world;
	}

	public int getIceStartZ_world() {
		return iceStartZ_world;
	}

	public float getSandCoverageForLatitude(int z) {
		return getStartToFullProgress(z, sandyStartZ_world, sandyFullZ_world);
	}

	public int getSandyFullZ_world() {
		return sandyFullZ_world;
	}

	public int getSandyStartZ_world() {
		return sandyStartZ_world;
	}

	private float getStartToFullProgress(int curZ, int startZ, int fullZ) {
		int latitudeTransitionLength = Math.abs(fullZ - startZ);
		int latitudeProgress = Integer.signum(fullZ - startZ) * (curZ - startZ);
		float progressF = (float) latitudeProgress / (float) latitudeTransitionLength;
		return MathHelper.clamp(progressF, 0.0F, 1.0F);
	}

	public int getWarmWaterZ_world() {
		return warmWaterZ_world;
	}

	public float getWaterTemperatureForLatitude(int z) {
		return getStartToFullProgress(z, coldWaterZ_world, warmWaterZ_world);
	}

	protected void write(PacketBuffer buf) {
		buf.writeFloat(coldWaterZ);
		buf.writeFloat(warmWaterZ);
		buf.writeFloat(iceStartZ);
		buf.writeFloat(iceFullZ);
		buf.writeFloat(sandyStartZ);
		buf.writeFloat(sandyFullZ);
		buf.writeFloat(coralStartZ);
		buf.writeFloat(coralFullZ);
	}

	protected static WaterLatitudeSettings read(MapSettings map, JsonObject json) {
		float coldWater = json.get("cold_water_z").getAsFloat();
		float warmWater = json.get("warm_water_z").getAsFloat();
		float iceStart = json.get("ice_start_z").getAsFloat();
		float iceFull = json.get("ice_full_z").getAsFloat();
		float sandyStart = json.get("sandy_start_z").getAsFloat();
		float sandyFull = json.get("sandy_full_z").getAsFloat();
		float coralStart = json.get("coral_start_z").getAsFloat();
		float coralFull = json.get("coral_full_z").getAsFloat();
		return new WaterLatitudeSettings(map, coldWater, warmWater, iceStart, iceFull, sandyStart, sandyFull, coralStart, coralFull);
	}

	protected static WaterLatitudeSettings read(MapSettings map, PacketBuffer buf) {
		float coldWater = buf.readFloat();
		float warmWater = buf.readFloat();
		float iceStart = buf.readFloat();
		float iceFull = buf.readFloat();
		float sandyStart = buf.readFloat();
		float sandyFull = buf.readFloat();
		float coralStart = buf.readFloat();
		float coralFull = buf.readFloat();
		return new WaterLatitudeSettings(map, coldWater, warmWater, iceStart, iceFull, sandyStart, sandyFull, coralStart, coralFull);
	}
}
