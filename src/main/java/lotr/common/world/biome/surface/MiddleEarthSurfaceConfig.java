package lotr.common.world.biome.surface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.ArrayUtils;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.world.biome.LOTRBiomeBase;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.ISurfaceBuilderConfig;

public class MiddleEarthSurfaceConfig implements ISurfaceBuilderConfig {
	public static final Codec<MiddleEarthSurfaceConfig> CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockState.CODEC.fieldOf("top_material").forGetter(prop -> prop.topMaterial), 
    		BlockState.CODEC.fieldOf("under_material").forGetter(prop -> prop.fillerMaterial), 
    		Codec.DOUBLE.fieldOf("under_depth").orElse(Double.valueOf(5.0D)).forGetter(prop -> prop.fillerDepth), 
    		BlockState.CODEC.fieldOf("underwater_material").forGetter(prop -> prop.underwaterMaterial), 
    		SubSoilLayer.SUB_SOIL_LAYER_CODEC.listOf().fieldOf("sub_soil_layers").forGetter(prop -> prop.subSoilLayers), 
    		Codec.BOOL.fieldOf("rocky").orElse(Boolean.valueOf(true)).forGetter(prop -> prop.rocky), 
    		Codec.BOOL.fieldOf("podzol").orElse(Boolean.valueOf(true)).forGetter(prop -> prop.podzol), 
    		Codec.FLOAT.fieldOf("tree_density_for_podzol").orElse(Float.valueOf(0.0F)).forGetter(prop -> prop.treeDensityForPodzol), 
    		Codec.INT.fieldOf("max_podzol_height").orElse(Integer.valueOf(2147483647)).forGetter(prop -> prop.maxPodzolHeight), 
    		Codec.BOOL.fieldOf("marsh").orElse(Boolean.valueOf(false)).forGetter(prop -> prop.marsh), 
    		SurfaceNoiseMixer.CODEC.fieldOf("surface_noise_mixer").orElse(SurfaceNoiseMixer.NONE).forGetter(prop -> prop.surfaceNoiseMixer), 
    		Codec.BOOL.fieldOf("surface_noise_paths").orElse(Boolean.valueOf(false)).forGetter(prop -> prop.hasSurfaceNoisePaths), 
    		UnderwaterNoiseMixer.CODEC.fieldOf("underwater_noise_mixer").orElse(UnderwaterNoiseMixer.NONE).forGetter(prop -> prop.underwaterNoiseMixer), 
    		MountainTerrainProvider.CODEC.fieldOf("mountain_terrain_provider").orElse(MountainTerrainProvider.NONE).forGetter(prop -> prop.mountainTerrainProvider))
    		.apply(instance, MiddleEarthSurfaceConfig::new));
	protected static final PerlinNoiseGenerator MARSH_NOISE = new PerlinNoiseGenerator(new SharedSeedRandom(444L), ImmutableList.of(0));
	private static final PerlinNoiseGenerator noiseGen1 = LOTRBiomeBase.makeSingleLayerPerlinNoise(1954L);
	private static final PerlinNoiseGenerator noiseGen2 = LOTRBiomeBase.makeSingleLayerPerlinNoise(10420914965337148L);
	private static final PerlinNoiseGenerator noiseGen3 = LOTRBiomeBase.makeSingleLayerPerlinNoise(2274201084179107L);
	private static final PerlinNoiseGenerator noiseGen4 = LOTRBiomeBase.makeSingleLayerPerlinNoise(259632637571778808L);
	private BlockState topMaterial;
	private BlockState fillerMaterial;
	private double fillerDepth;
	private BlockState underwaterMaterial;
	private List<SubSoilLayer> subSoilLayers;
	private boolean rocky;
	private boolean podzol;
	private float treeDensityForPodzol;
	private int maxPodzolHeight;
	private boolean marsh;
	private SurfaceNoiseMixer surfaceNoiseMixer;
	private boolean hasSurfaceNoisePaths;
	private UnderwaterNoiseMixer underwaterNoiseMixer;
	private MountainTerrainProvider mountainTerrainProvider;

	public MiddleEarthSurfaceConfig(BlockState top, BlockState filler, BlockState underwater) {
		this(top, filler, 5.0D, underwater, new ArrayList<>(), true, true, 0.0F, Integer.MAX_VALUE, false, SurfaceNoiseMixer.NONE, false, UnderwaterNoiseMixer.NONE, MountainTerrainProvider.NONE);
	}

	public MiddleEarthSurfaceConfig(BlockState top, BlockState filler, double fillerDepth, BlockState underwater, List<SubSoilLayer> subSoilLayers, boolean rocky, boolean podzol, float treeDensityForPodzol, int maxPodzolHeight, boolean marsh, SurfaceNoiseMixer surfaceNoiseMixer, boolean hasSurfaceNoisePaths, UnderwaterNoiseMixer underwaterNoiseMixer, MountainTerrainProvider mountainTerrainProvider) {
		this.fillerDepth = 5.0D;
		this.subSoilLayers = new ArrayList<>();
		this.rocky = true;
		this.podzol = true;
		this.treeDensityForPodzol = 0.0F;
		this.maxPodzolHeight = Integer.MAX_VALUE;
		this.marsh = false;
		this.surfaceNoiseMixer = SurfaceNoiseMixer.NONE;
		this.hasSurfaceNoisePaths = false;
		this.underwaterNoiseMixer = UnderwaterNoiseMixer.NONE;
		this.mountainTerrainProvider = MountainTerrainProvider.NONE;
		topMaterial = top;
		fillerMaterial = filler;
		this.fillerDepth = fillerDepth;
		underwaterMaterial = underwater;
		this.subSoilLayers = subSoilLayers;
		this.rocky = rocky;
		this.podzol = podzol;
		this.treeDensityForPodzol = treeDensityForPodzol;
		this.maxPodzolHeight = maxPodzolHeight;
		this.marsh = marsh;
		this.surfaceNoiseMixer = surfaceNoiseMixer;
		this.hasSurfaceNoisePaths = hasSurfaceNoisePaths;
		this.underwaterNoiseMixer = underwaterNoiseMixer;
		this.mountainTerrainProvider = mountainTerrainProvider;
	}

	public void addSubSoilLayer(BlockState state, int depth) {
		this.addSubSoilLayer(state, depth, depth);
	}

	public void addSubSoilLayer(BlockState state, int min, int max) {
		subSoilLayers.add(new MiddleEarthSurfaceConfig.SubSoilLayer(state, min, max));
	}

	public double getFillerDepth() {
		return fillerDepth;
	}

	public int getMaxPodzolHeight() {
		return maxPodzolHeight;
	}

	public BlockState getMountainTerrain(int x, int z, int y, BlockState in, BlockState stone, boolean top, int stoneNoiseDepth) {
		return mountainTerrainProvider.getReplacement(x, z, y, in, stone, top, stoneNoiseDepth);
	}

	public Iterator<SubSoilLayer> getSubSoilLayers() {
		return subSoilLayers.iterator();
	}

	public BlockState getSurfaceNoiseReplacement(int x, int z, BlockState in, boolean top, Random rand) {
		BlockState state = surfaceNoiseMixer.getReplacement(x, z, in, top, rand);
		if (hasSurfaceNoisePaths) {
			state = SurfaceNoisePaths.getReplacement(x, z, state, top, rand);
		}

		return state;
	}

	@Override
	public BlockState getTopMaterial() {
		return topMaterial;
	}

	public float getTreeDensityForPodzol() {
		return treeDensityForPodzol;
	}

	@Override
	public BlockState getUnderMaterial() {
		return fillerMaterial;
	}

	public BlockState getUnderwaterMaterial() {
		return underwaterMaterial;
	}

	public BlockState getUnderwaterNoiseReplacement(int x, int z, BlockState in, Random rand) {
		return underwaterNoiseMixer.getReplacement(x, z, in, rand);
	}

	public boolean hasMountainTerrain() {
		return mountainTerrainProvider != MountainTerrainProvider.NONE;
	}

	public boolean hasPodzol() {
		return podzol;
	}

	public boolean hasRockyTerrain() {
		return rocky;
	}

	public boolean isMarsh() {
		return marsh;
	}

	public boolean isSurfaceBlockForNPCSpawning(BlockState state) {
		return state.getBlock() == getTopMaterial().getBlock() || state.getBlock() == Blocks.PODZOL && hasPodzol() || surfaceNoiseMixer.isSurfaceBlock(state);
	}

	public void resetFillerDepthAndSubSoilLayers() {
		setFillerDepth(5.0D);
		subSoilLayers.clear();
	}

	public void setFiller(BlockState state) {
		fillerMaterial = state;
	}

	public void setFillerDepth(double d) {
		fillerDepth = d;
	}

	public MiddleEarthSurfaceConfig setMarsh(boolean flag) {
		marsh = flag;
		return this;
	}

	public MiddleEarthSurfaceConfig setMaxPodzolHeight(int h) {
		maxPodzolHeight = h;
		return this;
	}

	public void setMountainTerrain(MountainTerrainProvider provider) {
		mountainTerrainProvider = provider;
	}

	public MiddleEarthSurfaceConfig setPodzol(boolean flag) {
		podzol = flag;
		return this;
	}

	public MiddleEarthSurfaceConfig setRockyTerrain(boolean flag) {
		rocky = flag;
		return this;
	}

	public void setSurfaceNoiseMixer(SurfaceNoiseMixer mixer) {
		surfaceNoiseMixer = mixer;
	}

	public void setSurfaceNoisePaths(boolean flag) {
		hasSurfaceNoisePaths = flag;
	}

	public void setTop(BlockState state) {
		topMaterial = state;
	}

	public MiddleEarthSurfaceConfig setTreeDensityForPodzol(float f) {
		treeDensityForPodzol = f;
		return this;
	}

	public void setUnderwater(BlockState state) {
		underwaterMaterial = state;
	}

	public void setUnderwaterNoiseMixer(UnderwaterNoiseMixer mixer) {
		underwaterNoiseMixer = mixer;
	}

	public static MiddleEarthSurfaceConfig createDefault() {
		return new MiddleEarthSurfaceConfig(Blocks.GRASS_BLOCK.defaultBlockState(), Blocks.DIRT.defaultBlockState(), Blocks.GRAVEL.defaultBlockState());
	}

	private static double getIteratedNoise(PerlinNoiseGenerator noiseGen, int x, int z, double[] scales, double[] xScales, double[] zScales, int[] weights) {
		if (ArrayUtils.isEmpty(xScales)) {
			xScales = Util.make(new double[scales.length], arr -> {
				Arrays.fill(arr, 1.0D);
			});
		}

		if (ArrayUtils.isEmpty(zScales)) {
			zScales = Util.make(new double[scales.length], arr -> {
				Arrays.fill(arr, 1.0D);
			});
		}

		if (ArrayUtils.isEmpty(weights)) {
			weights = Util.make(new int[scales.length], arr -> {
				Arrays.fill(arr, 1);
			});
		}

		double noise = 0.0D;
		int totalWeight = 0;

		for (int i = 0; i < scales.length; ++i) {
			double coordScale = scales[i];
			int weight = weights[i];
			noise += noiseGen.getValue(x * xScales[i] * coordScale, z * zScales[i] * coordScale, false) * weight;
			totalWeight += weight;
		}

		return noise / totalWeight;
	}

	public static double getNoise1(int x, int z, double... scales) {
		return getNoise1(x, z, scales, (double[]) null, (double[]) null, (int[]) null);
	}

	public static double getNoise1(int x, int z, double[] scales, double[] xScales, double[] zScales, int[] weights) {
		return getIteratedNoise(noiseGen1, x, z, scales, xScales, zScales, weights);
	}

	public static double getNoise2(int x, int z, double... scales) {
		return getNoise2(x, z, scales, (double[]) null, (double[]) null, (int[]) null);
	}

	public static double getNoise2(int x, int z, double[] scales, double[] xScales, double[] zScales, int[] weights) {
		return getIteratedNoise(noiseGen2, x, z, scales, xScales, zScales, weights);
	}

	public static double getNoise3(int x, int z, double[] scales, double[] xScales, double[] zScales, int[] weights) {
		return getIteratedNoise(noiseGen3, x, z, scales, xScales, zScales, weights);
	}

	public static double getNoise4(int x, int z, double[] scales, double[] xScales, double[] zScales, int[] weights) {
		return getIteratedNoise(noiseGen4, x, z, scales, xScales, zScales, weights);
	}

	public static class SubSoilLayer {
		public static final Codec<SubSoilLayer> SUB_SOIL_LAYER_CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockState.CODEC.fieldOf("material").forGetter(config -> ((SubSoilLayer) config).material), Codec.INT.fieldOf("min_depth").orElse(0).forGetter(config -> ((SubSoilLayer) config).minDepth), Codec.INT.fieldOf("max_depth").orElse(0).forGetter(config -> ((SubSoilLayer) config).maxDepth)).apply(instance, MiddleEarthSurfaceConfig.SubSoilLayer::new));
		private final BlockState material;
		private final int minDepth;
		private final int maxDepth;

		public SubSoilLayer(BlockState state, int min, int max) {
			material = state;
			minDepth = min;
			maxDepth = max;
		}

		public int getDepth(Random rand) {
			return MathHelper.nextInt(rand, minDepth, maxDepth);
		}

		public BlockState getMaterial() {
			return material;
		}
	}
}
