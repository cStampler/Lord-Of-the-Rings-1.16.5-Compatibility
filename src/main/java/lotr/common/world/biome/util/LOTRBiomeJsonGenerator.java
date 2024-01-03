package lotr.common.world.biome.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
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
	  private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();
	  
	  public static void generateBiomeJsons() {
	    Path rootOutputPath = (new File(LOTRMod.PROXY.getGameRootDirectory(), "lotrmod-data-output")).toPath();
	    Function<Biome, DataResult<JsonElement>> jsonEncoder = JsonOps.INSTANCE.withEncoder(Biome.DIRECT_CODEC);
	    try {
	      DirectoryCache cache = new DirectoryCache(rootOutputPath, "cache");
	      for (RegistryObject<Biome> regBiome : (Iterable<RegistryObject<Biome>>)LOTRBiomes.BIOMES.getEntries()) {
	        Biome biome = (Biome)regBiome.get();
	        ResourceLocation biomeName = biome.getRegistryName();
	        Path biomePath = getPath(rootOutputPath, biomeName);
	        try {
	          Optional<JsonElement> optJson = (jsonEncoder.apply(biome)).result();
	          if (optJson.isPresent()) {
	            IDataProvider.save(GSON, cache, optJson.get(), biomePath);
	            continue;
	          } 
	          LOTRLog.error("Couldn't generate biome JSON for %s - codec gave no result", new Object[] { biomeName });
	        } catch (IOException e) {
	          e.printStackTrace();
	          LOTRLog.error("Error generating biome JSON for %s", new Object[] { biomeName });
	        } 
	      } 
	    } catch (IOException e) {
	      e.printStackTrace();
	      LOTRLog.error("Couldn't generate biome JSONs due to error");
	    } 
	    LOTRLog.info("Generated up-to-date LOTR biome JSONs in %s upon request!", new Object[] { rootOutputPath });
	  }
	  
	  private static Path getPath(Path rootPath, ResourceLocation biomeName) {
	    return rootPath.resolve(biomeName.getNamespace() + "/biomes/" + biomeName.getPath() + ".json");
	  }
	}
