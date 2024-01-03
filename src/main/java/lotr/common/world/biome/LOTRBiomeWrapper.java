package lotr.common.world.biome;

import java.util.List;
import java.util.Random;

import lotr.common.world.map.BridgeBlockProvider;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.IWorld;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.biome.Biome.RainType;

public interface LOTRBiomeWrapper {
	default Vector3d alterCloudColor(Vector3d clouds) {
		return clouds;
	}

	default boolean doesSnowGenerate(boolean defaultDoesSnowGenerate, IWorldReader world, BlockPos pos) {
		return defaultDoesSnowGenerate;
	}

	default boolean doesWaterFreeze(boolean defaultDoesWaterFreeze, IWorldReader world, BlockPos pos, boolean mustBeAtEdge) {
		return defaultDoesWaterFreeze;
	}

	Biome getActualBiome();

	ResourceLocation getBiomeRegistryName();

	default float getBiomeScaleSignificanceForChunkGen() {
		return 0.9F;
	}

	default BridgeBlockProvider getBridgeBlockProvider() {
		return BridgeBlockProvider.OAK;
	}

	default float getCloudCoverage() {
		return 1.0F;
	}

	default ExtendedWeatherType getExtendedWeatherVisually() {
		return ExtendedWeatherType.NONE;
	}

	default BlockState getGrassForBonemeal(Random rand, BlockPos plantPos) {
		return Blocks.GRASS.defaultBlockState();
	}

	default double getHorizontalNoiseScale() {
		return 400.0D;
	}

	RainType getPrecipitationVisually();

	Biome getRiver(IWorld var1);

	default RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.PATH;
	}

	LOTRBiomeBase getShore();

	List<MobSpawnInfo.Spawners> getSpawnsAtLocation(EntityClassification var1, BlockPos var2);

	default float getStrengthOfAddedDepthNoise() {
		return 1.0F;
	}

	float getTemperatureForSnowWeatherRendering(IWorld var1, BlockPos var2);

	default float getTemperatureRaw(float defaultTemperatureAtPos, BlockPos pos) {
		return defaultTemperatureAtPos;
	}

	default boolean hasBreakMallornResponse() {
		return false;
	}

	default boolean hasMountainsMist() {
		return false;
	}

	default boolean hasSkyFeatures() {
		return true;
	}

	default boolean isFoggy() {
		return false;
	}

	boolean isRiver();

	boolean isSurfaceBlockForNPCSpawn(BlockState var1);

	default void onGeographicalWaterColorUpdate(int waterColor, Biome biomeObjectInClientRegistry) {
	}

	static boolean isSnowBlockBelow(IWorldReader world, BlockPos pos) {
		return world.getBlockState(pos.below()).getBlock() == Blocks.SNOW_BLOCK;
	}
}
