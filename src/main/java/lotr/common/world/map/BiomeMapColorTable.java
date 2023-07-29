package lotr.common.world.map;

import java.util.*;
import java.util.Map.Entry;

import com.google.common.collect.ImmutableSet;
import com.google.gson.*;

import lotr.common.LOTRLog;
import lotr.common.init.LOTRBiomes;
import lotr.common.resources.CombinableMappingsResource;
import lotr.common.util.LOTRUtil;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;

public class BiomeMapColorTable extends CombinableMappingsResource {
	public BiomeMapColorTable(Map colors, int loadOrder, int numCombinedFrom) {
		super(colors, loadOrder, numCombinedFrom);
	}

	public Biome getBiome(int color, IWorld world) {
		ResourceLocation biomeName = (ResourceLocation) mappings.get(color);
		if (biomeName == null) {
			return null;
		}
		Biome biome = LOTRBiomes.getBiomeByRegistryName(biomeName, world);
		if (biome == null) {
			LOTRLog.error("Biome %s (mapped to map color %s) does not exist in the biome registry!", biomeName, LOTRUtil.toPaddedHexString(color));
		}

		return biome;
	}

	public void write(PacketBuffer buf) {
		buf.writeVarInt(mappings.size());
		mappings.forEach((color, biomeName) -> {
			buf.writeInt((int) color);
			buf.writeResourceLocation((ResourceLocation) biomeName);
		});
		buf.writeVarInt(getNumCombinedFrom());
	}

	public static BiomeMapColorTable combine(List colorTables) {
		return (BiomeMapColorTable) combine(colorTables, BiomeMapColorTable::new);
	}

	public static BiomeMapColorTable read(PacketBuffer buf) {
		Map biomeColors = new HashMap();
		int numColors = buf.readVarInt();

		int numCombinedFrom;
		for (numCombinedFrom = 0; numCombinedFrom < numColors; ++numCombinedFrom) {
			int color = buf.readInt();
			ResourceLocation biomeName = buf.readResourceLocation();
			biomeColors.put(color, biomeName);
		}

		numCombinedFrom = buf.readVarInt();
		return new BiomeMapColorTable(biomeColors, 0, numCombinedFrom);
	}

	public static BiomeMapColorTable read(ResourceLocation colorTableName, JsonObject json) {
		int loadOrder = json.get("load_order").getAsInt();
		Map biomeColors = new HashMap();
		JsonObject mappingsJson = json.get("biome_colors").getAsJsonObject();
		Iterator var5 = mappingsJson.entrySet().iterator();

		while (var5.hasNext()) {
			Entry entry = (Entry) var5.next();
			String key = (String) entry.getKey();
			JsonElement value = (JsonElement) entry.getValue();

			try {
				ResourceLocation biomeName = new ResourceLocation(key);
				Object hexColors;
				Iterator var11;
				if (value.isJsonPrimitive()) {
					hexColors = ImmutableSet.of(value.getAsString());
				} else {
					if (!value.isJsonArray()) {
						LOTRLog.warn("Couldn't parse a biome color mappings line in table %s (key = %s, value = %s) - expected value to be either a hex string or an array of hex strings", colorTableName, key, value);
						continue;
					}

					hexColors = new HashSet();
					var11 = value.getAsJsonArray().iterator();

					while (var11.hasNext()) {
						JsonElement elem = (JsonElement) var11.next();
						((Set) hexColors).add(elem.getAsString());
					}
				}

				var11 = ((Set) hexColors).iterator();

				while (var11.hasNext()) {
					String hexColor = (String) var11.next();
					int color = 0;

					try {
						color = Integer.parseInt(hexColor, 16);
					} catch (NumberFormatException var15) {
						LOTRLog.warn("Biome color mapping for %s has invalid color code %s - must be in hex color format (e.g. FFAA33)", biomeName, hexColor);
					}

					if (biomeColors.containsKey(color)) {
						ResourceLocation alreadyMappedBiomeName = (ResourceLocation) biomeColors.get(color);
						LOTRLog.warn("Biome %s is already mapped to color %s - mappings' colors must be unique!", alreadyMappedBiomeName, hexColor);
					} else {
						biomeColors.put(color, biomeName);
					}
				}
			} catch (Exception var16) {
				LOTRLog.warn("Couldn't parse a biome color mappings line in table %s: key = %s, value = %s", colorTableName, key, value);
				var16.printStackTrace();
			}
		}

		return new BiomeMapColorTable(biomeColors, loadOrder, 0);
	}
}
