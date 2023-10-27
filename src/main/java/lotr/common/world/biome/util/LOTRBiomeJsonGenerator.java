package lotr.common.world.biome.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Function;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;

import lotr.common.LOTRLog;
import lotr.common.LOTRMod;
import lotr.common.init.LOTRBiomes;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.RegistryObject;

public class LOTRBiomeJsonGenerator {
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static void generateBiomeJsons() {
		Path rootOutputPath = new File(LOTRMod.PROXY.getGameRootDirectory(), "lotrmod-data-output").toPath();
		Function jsonEncoder = JsonOps.INSTANCE.withEncoder(Biome.DIRECT_CODEC);

		try {
			DirectoryCache cache = new DirectoryCache(rootOutputPath, "cache");
			Iterator var3 = LOTRBiomes.BIOMES.getEntries().iterator();

			while (var3.hasNext()) {
				RegistryObject regBiome = (RegistryObject) var3.next();
				Biome biome = (Biome) regBiome.get();
				ResourceLocation biomeName = biome.getRegistryName();
				Path biomePath = getPath(rootOutputPath, biomeName);

				try {
					Optional optJson = ((DataResult) jsonEncoder.apply(biome)).result();
					if (optJson.isPresent()) {
						IDataProvider.save(GSON, cache, (JsonElement) optJson.get(), biomePath);
					} else {
						LOTRLog.error("Couldn't generate biome JSON for %s - codec gave no result", biomeName);
					}
				} catch (IOException var9) {
					var9.printStackTrace();
					LOTRLog.error("Error generating biome JSON for %s", biomeName);
				}
			}
		} catch (IOException var10) {
			var10.printStackTrace();
			LOTRLog.error("Couldn't generate biome JSONs due to error");
		}

		LOTRLog.info("Generated up-to-date LOTR biome JSONs in %s upon request!", rootOutputPath);
	}

	private static Path getPath(Path rootPath, ResourceLocation biomeName) {
		return rootPath.resolve(biomeName.getNamespace() + "/biomes/" + biomeName.getPath() + ".json");
	}
}
