package lotr.common.world.biome;

import java.util.List;

import lotr.common.init.LOTRBiomes;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.Biomes;

public class VanillaPlaceholderLOTRBiome implements LOTRBiomeWrapper {
	private final ResourceLocation biomeName;
	private final Biome biome;

	private VanillaPlaceholderLOTRBiome(ResourceLocation name, Biome b) {
		biomeName = name;
		biome = b;
	}

	@Override
	public final Biome getActualBiome() {
		return biome;
	}

	@Override
	public final ResourceLocation getBiomeRegistryName() {
		return biomeName;
	}

	@Override
	public RainType getPrecipitationVisually() {
		return biome.getPrecipitation();
	}

	@Override
	public Biome getRiver(IWorld world) {
		return LOTRBiomes.getBiomeByRegistryName(Biomes.RIVER.location(), world);
	}

	@Override
	public LOTRBiomeBase getShore() {
		return LOTRBiomes.BEACH.getInitialisedBiomeWrapper();
	}

	@Override
	public List getSpawnsAtLocation(EntityClassification creatureType, BlockPos pos) {
		return biome.getMobSettings().getMobs(creatureType);
	}

	@Override
	public float getTemperatureForSnowWeatherRendering(IWorld world, BlockPos pos) {
		return biome.getBaseTemperature();
	}

	@Override
	public boolean isRiver() {
		return biome.getBiomeCategory() == Category.RIVER;
	}

	@Override
	public boolean isSurfaceBlockForNPCSpawn(BlockState state) {
		return state.getBlock() == biome.getGenerationSettings().getSurfaceBuilderConfig().getTopMaterial().getBlock();
	}

	public static VanillaPlaceholderLOTRBiome makeWrapperFor(IWorld world, Biome biome) {
		ResourceLocation biomeName = LOTRBiomes.getBiomeRegistryName(biome, world);
		if (LOTRBiomes.isDefaultLOTRBiome(biomeName)) {
			throw new IllegalArgumentException(String.format("Cannot wrap a default LOTR mod biome (%s) in a vanilla placeholder wrapper!", biomeName));
		}
		return new VanillaPlaceholderLOTRBiome(biomeName, biome);
	}
}
