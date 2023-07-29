package lotr.common.coremod;

import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import com.mojang.datafixers.DataFixerBuilder;
import com.mojang.serialization.*;

import lotr.common.*;
import lotr.common.datafix.LOTRDataFixes;
import lotr.common.dim.AddModDimensionToOldWorlds;
import lotr.common.event.MiddleEarthRespawning;
import lotr.common.init.*;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.*;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import net.minecraft.world.storage.ServerWorldInfo;

public class InjectMethods {
	public static class Biomes {
		public static boolean doesSnowGenerate(boolean defaultDoesSnowGenerate, Biome biome, IWorldReader world, BlockPos pos) {
			return LOTRBiomes.getWrapperFor(biome, LOTRBiomes.getServerBiomeContextWorld()).doesSnowGenerate(defaultDoesSnowGenerate, world, pos);
		}

		public static boolean doesWaterFreeze(boolean defaultDoesWaterFreeze, Biome biome, IWorldReader world, BlockPos pos, boolean mustBeAtEdge) {
			return LOTRBiomes.getWrapperFor(biome, LOTRBiomes.getServerBiomeContextWorld()).doesWaterFreeze(defaultDoesWaterFreeze, world, pos, mustBeAtEdge);
		}

		public static float getTemperatureRaw(float defaultTemperatureAtPos, Biome biome, BlockPos pos) {
			IWorld world = LOTRBiomes.getServerBiomeContextWorld();
			if (world == null) {
				if (!LOTRMod.PROXY.isClient()) {
					LOTRLog.warn("Unexpected call to biome#getTemperatureRaw (biome %s) which could not determine the current world context. This should never happen within the LOTR mod but may happen if other mods are installed. Defaulting to vanilla behaviour as fallback.", biome);
					return defaultTemperatureAtPos;
				}

				world = LOTRMod.PROXY.getClientWorld();
			}

			return LOTRBiomes.getWrapperFor(biome, world).getTemperatureRaw(defaultTemperatureAtPos, pos);
		}
	}

	public static class DataFixes {
		public static void addModFixers(DataFixerBuilder builder) {
			LOTRDataFixes.addFixers(builder);
		}
	}

	public static class Dimensions {
		public static void addModDimensionToOldWorlds(DynamicOps nbtOps, LevelSave levelSave, ServerWorldInfo serverInfo) {
			AddModDimensionToOldWorlds.operateOnWorldSave(nbtOps, levelSave, serverInfo);
		}

		public static DataResult checkDecodableModWorldKey(DataResult defaultResult, Dynamic dynamic) {
			return AddModDimensionToOldWorlds.checkDecodableModWorldKey(defaultResult, dynamic);
		}

		public static void registerDimensionTypes(MutableRegistry dimTypeReg) {
			LOTRDimensions.registerDimensionTypes(dimTypeReg);
		}

		public static void registerWorldDimensions(SimpleRegistry dimReg, Registry dimTypeReg, Registry biomeReg, Registry dimSettingsReg, long seed) {
			LOTRDimensions.registerWorldDimensions(dimReg, dimTypeReg, biomeReg, dimSettingsReg, seed);
		}

		public static List removeAddedDimensionsFromExperimentalConsideration(List inputList) {
			return (List) inputList.stream().filter(entry -> !LOTRDimensions.isAddedDimension((RegistryKey) ((Entry) entry).getKey())).collect(Collectors.toList());
		}
	}

	public static class Respawning {
		public static BlockPos getCheckedBedRespawnPosition(BlockPos bedRespawnPosition, ServerPlayerEntity player) {
			return MiddleEarthRespawning.getCheckedBedRespawnPosition(bedRespawnPosition, player);
		}

		public static ServerWorld getDefaultRespawnWorld(ServerWorld defaultRespawnWorld, ServerPlayerEntity player) {
			return MiddleEarthRespawning.getDefaultRespawnWorld(defaultRespawnWorld, player);
		}

		public static void relocatePlayerIfNeeded(Optional optBedRespawnPosition, ServerPlayerEntity newPlayer, ServerPlayerEntity deadPlayer) {
			MiddleEarthRespawning.relocatePlayerIfNeeded(optBedRespawnPosition, newPlayer, deadPlayer);
		}
	}
}
