package lotr.common.world.biome.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.init.LOTRBiomes;
import lotr.common.world.gen.MiddleEarthBiomeGenSettings;
import lotr.common.world.gen.layer.LayerWithDataDrivenBiomes;
import lotr.common.world.gen.layer.MiddleEarthWorldLayers;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryLookupCodec;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class MiddleEarthBiomeProvider extends BiomeProvider {
	public static final Codec<MiddleEarthBiomeProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.LONG.fieldOf("seed").stable().forGetter(provider -> provider.seed),
			Codec.BOOL.fieldOf("classic_biomes").forGetter(provider -> provider.classicBiomes),
			RegistryLookupCodec.create(Registry.BIOME_REGISTRY).forGetter(provider -> provider.lookupRegistry)
	).apply(instance, MiddleEarthBiomeProvider::new));

	private final long seed;
	private final Registry<Biome> lookupRegistry;
	private boolean classicBiomes;
	private boolean alreadySetWorldTypeClassicBiomes = false;
	private LayerWithDataDrivenBiomes genBiomes;

	public MiddleEarthBiomeProvider(long seed, boolean classicBiomes, Registry<Biome> lookupRegistry) {
		super(LOTRBiomes.listAllBiomesForProvider(lookupRegistry));
		this.seed = seed;
		this.lookupRegistry = lookupRegistry;
		this.classicBiomes = classicBiomes;
		genBiomes = createOrRecreateBiomeGenLayers();
	}

	@Override
	protected Codec<? extends BiomeProvider> codec() {
		return CODEC;
	}

	private LayerWithDataDrivenBiomes createOrRecreateBiomeGenLayers() {
		return MiddleEarthWorldLayers.create(seed, classicBiomes, new MiddleEarthBiomeGenSettings());
	}

	@Override
	public Biome getNoiseBiome(int x, int y, int z) {
		return genBiomes.getLayerBiome(lookupRegistry, x, z);
	}

	public void hackySetWorldTypeClassicBiomes(boolean flag) {
		if (alreadySetWorldTypeClassicBiomes) {
			throw new IllegalStateException("Already set the world type value of classicBiomes to " + classicBiomes + "!");
		}
		if (flag != classicBiomes) {
			alreadySetWorldTypeClassicBiomes = true;
			classicBiomes = flag;
			genBiomes = createOrRecreateBiomeGenLayers();
		}
	}

	public boolean isClassicBiomes() {
		return classicBiomes;
	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public BiomeProvider withSeed(long seed) {
		return new MiddleEarthBiomeProvider(seed, classicBiomes, lookupRegistry);
	}
}
