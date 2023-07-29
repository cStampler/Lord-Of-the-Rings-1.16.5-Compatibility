package lotr.common.dim;

import java.io.*;
import java.util.*;

import org.apache.commons.io.FileUtils;

import com.mojang.serialization.*;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRDimensions;
import net.minecraft.util.*;
import net.minecraft.util.registry.*;
import net.minecraft.world.gen.settings.DimensionGeneratorSettings;
import net.minecraft.world.storage.*;
import net.minecraft.world.storage.SaveFormat.LevelSave;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class AddModDimensionToOldWorlds {
	public static ResourceLocation displayRelocatingDimensionFolder;

	public static DataResult checkDecodableModWorldKey(DataResult defaultResult, Dynamic dynamic) {
		Optional optIntId = dynamic.asNumber().result();
		if (optIntId.isPresent()) {
			int dimId = ((Number) optIntId.get()).intValue();
			if (dimId == 2) {
				return DataResult.success(LOTRDimensions.MIDDLE_EARTH_WORLD_KEY);
			}
		}

		return defaultResult;
	}

	public static void operateOnWorldSave(DynamicOps nbtOps, LevelSave levelSave, ServerWorldInfo serverInfo) {
		if (!(nbtOps instanceof WorldSettingsImport)) {
			LOTRLog.error("Could not operate on world save - the DynamicOps was not an instance of WorldSettingsImport");
		} else {
			WorldSettingsImport worldSettingsImport = (WorldSettingsImport) nbtOps;
			DynamicRegistries dynRegs = (DynamicRegistries) ObfuscationReflectionHelper.getPrivateValue(WorldSettingsImport.class, worldSettingsImport, "registryHolder");
			if (dynRegs == null) {
				LOTRLog.error("Failed to fetch the dynamic registries from WorldSettingsImport");
			} else {
				DimensionGeneratorSettings dimGenSettings = serverInfo.worldGenSettings();
				SimpleRegistry dimReg = dimGenSettings.dimensions();
				Registry dimTypeReg = dynRegs.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
				Registry biomeReg = dynRegs.registryOrThrow(Registry.BIOME_REGISTRY);
				Registry dimSettingsReg = dynRegs.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
				long seed = dimGenSettings.seed();
				Iterator var12 = LOTRDimensions.viewAddedDimensions().iterator();

				while (var12.hasNext()) {
					RegistryKey modDimension = (RegistryKey) var12.next();
					ResourceLocation dimName = modDimension.location();
					if (!dimReg.keySet().contains(dimName)) {
						LOTRDimensions.addSpecificDimensionToWorldRegistry(modDimension, dimReg, dimTypeReg, biomeReg, dimSettingsReg, seed);
						LOTRLog.info("Injected dimension %s into the registry for a pre-1.16 lotrmod world, or pre-existing lotrmodless 1.16 world", dimName);
						relocateOldFolder(levelSave, modDimension, dimName);
					}
				}

			}
		}
	}

	private static void relocateOldFolder(LevelSave levelSave, RegistryKey modDimension, ResourceLocation dimName) {
		FolderName oldDimFolderName = new FolderName(String.format("%s/%s", dimName.getNamespace(), dimName.getPath()));
		File oldDimFolder = levelSave.getLevelPath(oldDimFolderName).toFile();
		if (oldDimFolder.exists()) {
			RegistryKey dimWorldKey = RegistryKey.create(Registry.DIMENSION_REGISTRY, modDimension.location());
			File newDimFolder = levelSave.getDimensionPath(dimWorldKey);
			if (!newDimFolder.exists()) {
				LOTRLog.info("Copying dimension data for %s from pre-1.16 dimension folder structure to new location...", dimName);
				displayRelocatingDimensionFolder = dimName;

				try {
					FileUtils.copyDirectory(oldDimFolder, newDimFolder);
					LOTRLog.info("Copied");
				} catch (IOException var8) {
					LOTRLog.warn("Copying failed!");
					var8.printStackTrace();
				}

				displayRelocatingDimensionFolder = null;
				File oldDimFolderRename = new File(oldDimFolder.getParent(), String.format("PRE_MC_116_BACKUP_%s", oldDimFolder.getName()));
				oldDimFolder.renameTo(oldDimFolderRename);
				LOTRLog.info("...and renamed old folder as a backup");
			}
		}

	}
}
