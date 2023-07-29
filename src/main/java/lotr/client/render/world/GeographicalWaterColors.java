package lotr.client.render.world;

import java.awt.Color;
import java.util.Iterator;
import java.util.Map.Entry;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import lotr.common.world.map.*;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.profiler.IProfiler;
import net.minecraft.util.math.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

public class GeographicalWaterColors {
	private static final Color WATER_COLOR_COLD = new Color(602979);
	private static final Color WATER_COLOR_WARM = new Color(4445678);
	private static BlockPos lastViewerPos;

	private static int calcGeographicalWaterColor(Minecraft mc, BlockPos pos) {
		MapSettings loadedMapSettings = MapSettingsManager.clientInstance().getLoadedMapOrLoadDefault(mc.getResourceManager());
		if (loadedMapSettings == null) {
			LOTRLog.error("No MapSettings instance is loaded on the client! This should not happen and is very bad!");
		}

		BothWaterLatitudeSettings waterLatitudes = loadedMapSettings.getWaterLatitudes();
		float latitude = waterLatitudes.getWaterTemperatureForLatitude(pos.getZ());
		float[] coldColors = WATER_COLOR_COLD.getColorComponents((float[]) null);
		float[] warmColors = WATER_COLOR_WARM.getColorComponents((float[]) null);
		float r = MathHelper.lerp(latitude, coldColors[0], warmColors[0]);
		float g = MathHelper.lerp(latitude, coldColors[1], warmColors[1]);
		float b = MathHelper.lerp(latitude, coldColors[2], warmColors[2]);
		Color water = new Color(r, g, b);
		return water.getRGB();
	}

	public static void resetInMenu() {
		lastViewerPos = null;
	}

	public static void updateGeographicalWaterColorInBiomes(Minecraft mc) {
		IProfiler profiler = mc.getProfiler();
		profiler.push("lotrGeographicalWaterColors");
		Entity viewer = mc.cameraEntity;
		BlockPos newViewerPos = null;
		if (viewer != null) {
			newViewerPos = viewer.blockPosition();
		}

		if (newViewerPos != null && (lastViewerPos == null || !lastViewerPos.closerThan(newViewerPos, 16.0D))) {
			lastViewerPos = newViewerPos;
			profiler.push("calculate");
			int viewerPosWaterColor = calcGeographicalWaterColor(mc, newViewerPos);
			profiler.popPush("iterateBiomes");
			World world = mc.level;
			MutableRegistry biomeRegistry = world.registryAccess().registryOrThrow(Registry.BIOME_REGISTRY);
			for (Iterator var7 = biomeRegistry.entrySet().iterator(); var7.hasNext();) {
				Entry e = (Entry) var7.next();
				Biome biome = (Biome) e.getValue();
				LOTRBiomes.getWrapperFor(biome, world).onGeographicalWaterColorUpdate(viewerPosWaterColor, biome);
			}

			profiler.pop();
		}

		profiler.pop();
	}
}
