package lotr.common.world.map;

import java.util.function.BiFunction;
import java.util.function.Function;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;

public class BothWaterLatitudeSettings {
	public static final ResourceLocation WATER_SETTINGS_PATH = new ResourceLocation("lotr", "map/water_latitude.json");
	private final WaterLatitudeSettings northernWaterLatitudes;
	private final WaterLatitudeSettings southernWaterLatitudes;

	private BothWaterLatitudeSettings(WaterLatitudeSettings north, WaterLatitudeSettings south) {
		northernWaterLatitudes = north;
		southernWaterLatitudes = south;
	}

	public float getCoralForLatitude(int z) {
		return getFromNorthOrSouth(z, hummel -> ((WaterLatitudeSettings) hummel).getCoralFullZ_world(), (hummel, h2) -> ((WaterLatitudeSettings) hummel).getCoralForLatitude((int) h2));
	}

	private float getFromNorthOrSouth(int z, Function southSettingsNorthernmostPoint, BiFunction settingsLatitudeGetter) {
		return z >= (Integer) southSettingsNorthernmostPoint.apply(southernWaterLatitudes) ? (Float) settingsLatitudeGetter.apply(southernWaterLatitudes, z) : (Float) settingsLatitudeGetter.apply(northernWaterLatitudes, z);
	}

	public float getIceCoverageForLatitude(int z) {
		return getFromNorthOrSouth(z, hummel -> ((WaterLatitudeSettings) hummel).getIceStartZ_world(), (hummel, h2) -> ((WaterLatitudeSettings) hummel).getIceCoverageForLatitude((int) h2));
	}

	public WaterLatitudeSettings getNorthern() {
		return northernWaterLatitudes;
	}

	public float getSandCoverageForLatitude(int z) {
		return getFromNorthOrSouth(z, hummel -> ((WaterLatitudeSettings) hummel).getSandyFullZ_world(), (hummel, h2) -> ((WaterLatitudeSettings) hummel).getSandCoverageForLatitude((int) h2));
	}

	public WaterLatitudeSettings getSouthern() {
		return southernWaterLatitudes;
	}

	public float getWaterTemperatureForLatitude(int z) {
		return getFromNorthOrSouth(z, hummel -> ((WaterLatitudeSettings) hummel).getWarmWaterZ_world(), (hummel, h2) -> ((WaterLatitudeSettings) hummel).getWaterTemperatureForLatitude((int) h2));
	}

	protected void write(PacketBuffer buf) {
		northernWaterLatitudes.write(buf);
		southernWaterLatitudes.write(buf);
	}

	public static boolean isNorthOfSouthernIceSheet(IWorld world, BlockPos pos) {
		return pos.getZ() < MapSettingsManager.sidedInstance(world).getCurrentLoadedMap().getWaterLatitudes().getSouthern().getIceStartZ_world();
	}

	public static BothWaterLatitudeSettings read(MapSettings map, JsonObject rootJson) {
		WaterLatitudeSettings north = WaterLatitudeSettings.read(map, rootJson.getAsJsonObject("northern"));
		WaterLatitudeSettings south = WaterLatitudeSettings.read(map, rootJson.getAsJsonObject("southern"));
		return new BothWaterLatitudeSettings(north, south);
	}

	protected static BothWaterLatitudeSettings read(MapSettings map, PacketBuffer buf) {
		WaterLatitudeSettings north = WaterLatitudeSettings.read(map, buf);
		WaterLatitudeSettings south = WaterLatitudeSettings.read(map, buf);
		return new BothWaterLatitudeSettings(north, south);
	}
}
