package lotr.common.world.gen;

import java.util.*;
import java.util.function.*;
import java.util.stream.IntStream;

import javax.annotation.Nullable;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import it.unimi.dsi.fastutil.objects.*;
import lotr.common.init.*;
import lotr.common.world.biome.LOTRBiomeWrapper;
import lotr.common.world.biome.provider.MiddleEarthBiomeProvider;
import lotr.common.world.map.*;
import net.minecraft.block.*;
import net.minecraft.entity.EntityClassification;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.*;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.gen.feature.jigsaw.JigsawJunction;
import net.minecraft.world.gen.feature.jigsaw.JigsawPattern.PlacementBehaviour;
import net.minecraft.world.gen.feature.structure.*;
import net.minecraft.world.gen.settings.NoiseSettings;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.spawner.WorldEntitySpawner;
import net.minecraftforge.api.distmarker.*;



public class MiddleEarthChunkGenerator extends ChunkGenerator {
	public static final Codec<MiddleEarthChunkGenerator> CODEC = RecordCodecBuilder.create(instance ->
			instance.group(
					BiomeProvider.CODEC.fieldOf("biome_source").forGetter(chunkGen -> chunkGen.biomeSource),
					Codec.LONG.fieldOf("seed").stable().forGetter(chunkGen -> chunkGen.seed),
					DimensionSettings.CODEC.fieldOf("settings").forGetter(chunkGen -> chunkGen.dimensionSettings), // Use get() to obtain DimensionSettings
					Codec.BOOL.optionalFieldOf("instant_middle_earth").forGetter(chunkGen -> chunkGen.isInstantMiddleEarth)
			).apply(instance, (biomeSource, seed, settings, isInstantMiddleEarth) ->
					new MiddleEarthChunkGenerator(biomeSource, biomeSource, seed, () -> settings.get(), isInstantMiddleEarth)
			)
	);

	private static final float[] CUBIC_SAMPLE = Util.make(new float[13824], array -> {
		for (int i = 0; i < 24; ++i) {
			for (int j = 0; j < 24; ++j) {
				for (int k = 0; k < 24; ++k) {
					array[i * 576 + j * 24 + k] = (float) computeContribution(j - 12, k - 12, i - 12);
				}
			}
		}

	});
	private static final float[] BIOME_SAMPLING_SIGNIFICANCE = Util.make(new float[169], array -> {
		for (int z = -6; z <= 6; ++z) {
			for (int x = -6; x <= 6; ++x) {
				float f = 10.0F / MathHelper.sqrt(z * z + x * x + 0.2F);
				array[z + 6 + (x + 6) * 13] = f;
			}
		}

	});
	private static final BlockState AIR;
	static {
		AIR = Blocks.AIR.defaultBlockState();
	}
	private final BlockState defaultBlock;
	private final BlockState defaultFluid;
	private final int verticalNoiseGranularity;
	private final int horizontalNoiseGranularity;
	private final int noiseSizeX;
	private final int noiseSizeY;
	private final int noiseSizeZ;
	private final OctavesNoiseGenerator minLimitPerlinNoise;
	private final OctavesNoiseGenerator maxLimitPerlinNoise;
	private final OctavesNoiseGenerator mainPerlinNoise;
	private final INoiseGenerator surfaceDepthNoise;
	private final OctavesNoiseGenerator depthNoise;
	private final long seed;
	private final Supplier<DimensionSettings> dimensionSettings; // Specify the generic type for Supplier
	private final boolean isAmplified;
	private final int chunkGenHeight;
	private final RoadGenerator roadGenerator;

	private Optional<Boolean> isInstantMiddleEarth;


	public MiddleEarthChunkGenerator(BiomeProvider biomeProvider, BiomeProvider secondBreakfastBiomeProvider, long seed, Supplier<DimensionSettings> dimSettings, Optional<Boolean> isInstantMiddleEarth) {
		super(biomeProvider, secondBreakfastBiomeProvider, ((DimensionSettings) dimSettings.get()).structureSettings(), seed);
		roadGenerator = new RoadGenerator();
		this.seed = seed;
		DimensionSettings dimensionSettings = (DimensionSettings) dimSettings.get();
		this.dimensionSettings = (Supplier<DimensionSettings>) dimSettings;
		NoiseSettings noiseSettings = dimensionSettings.noiseSettings();
		chunkGenHeight = noiseSettings.height();
		verticalNoiseGranularity = noiseSettings.noiseSizeVertical() * 4;
		horizontalNoiseGranularity = noiseSettings.noiseSizeHorizontal() * 4;
		defaultBlock = dimensionSettings.getDefaultBlock();
		defaultFluid = dimensionSettings.getDefaultFluid();
		noiseSizeX = 16 / horizontalNoiseGranularity;
		noiseSizeY = noiseSettings.height() / verticalNoiseGranularity;
		noiseSizeZ = 16 / horizontalNoiseGranularity;
		SharedSeedRandom randomSeed = new SharedSeedRandom(seed);
		minLimitPerlinNoise = new OctavesNoiseGenerator(randomSeed, IntStream.rangeClosed(-15, 0));
		maxLimitPerlinNoise = new OctavesNoiseGenerator(randomSeed, IntStream.rangeClosed(-15, 0));
		mainPerlinNoise = new OctavesNoiseGenerator(randomSeed, IntStream.rangeClosed(-7, 0));
		surfaceDepthNoise = noiseSettings.useSimplexSurfaceNoise() ? new PerlinNoiseGenerator(randomSeed, IntStream.rangeClosed(-3, 0)) : new OctavesNoiseGenerator(randomSeed, IntStream.rangeClosed(-3, 0));
		randomSeed.consumeCount(2620);
		depthNoise = new OctavesNoiseGenerator(randomSeed, IntStream.rangeClosed(-15, 0));
		isAmplified = noiseSettings.isAmplified();
		this.isInstantMiddleEarth = isInstantMiddleEarth;
	}

	public MiddleEarthChunkGenerator(BiomeProvider biomeProvider, long seed, Supplier<DimensionSettings> dimSettings, Optional<Boolean> isInstantMiddleEarth) {
		this(biomeProvider, biomeProvider, seed, dimSettings, isInstantMiddleEarth);
	}

	@Override
	public void buildSurfaceAndBedrock(WorldGenRegion region, IChunk chunk) {
		ChunkPos chunkPos = chunk.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		SharedSeedRandom rand = new SharedSeedRandom();
		rand.setBaseChunkSeed(chunkX, chunkZ);
		int xStart = chunkPos.getMinBlockX();
		int zStart = chunkPos.getMinBlockZ();
		double noiseScale = 0.0625D;
		Mutable movingPos = new Mutable();

		for (int x = 0; x < 16; ++x) {
			for (int z = 0; z < 16; ++z) {
				int posX = xStart + x;
				int posZ = zStart + z;
				int topY = chunk.getHeight(Type.WORLD_SURFACE_WG, x, z) + 1;
				movingPos.set(xStart + x, topY, zStart + z);
				double noise = surfaceDepthNoise.getSurfaceNoiseValue(posX * noiseScale, posZ * noiseScale, noiseScale, x * noiseScale) * 15.0D;
				Biome biome = region.getBiome(movingPos);
				biome.buildSurfaceAt(rand, chunk, posX, posZ, topY, noise, defaultBlock, defaultFluid, getSeaLevel(), region.getSeed());
				ServerWorld world = region.getLevel();
				if (LOTRWorldTypes.hasMapFeatures(world)) {
					roadGenerator.generateRoad(region, chunk, rand, LOTRBiomes.getWrapperFor(biome, world), movingPos.below(), getSeaLevel());
				}
			}
		}

		makeBedrock(chunk, rand);
	}

	protected void calcNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ, double p_222546_4_, double p_222546_6_, double p_222546_8_, double p_222546_10_, int p_222546_12_, int p_222546_13_) {
		double[] depthAndScale = getBiomeNoiseColumn(noiseX, noiseZ);
		double avgDepth = depthAndScale[0];
		double avgScale = depthAndScale[1];
		double d2 = noiseSizeY - 4;
		double d3 = 0.0D;

		for (int i = 0; i < noiseSizeY; ++i) {
			double d4 = sampleAndClampNoise(noiseX, i, noiseZ, p_222546_4_, p_222546_6_, p_222546_8_, p_222546_10_);
			d4 -= func_222545_a(avgDepth, avgScale, i);
			if (i > d2) {
				d4 = MathHelper.clampedLerp(d4, p_222546_13_, (i - d2) / p_222546_12_);
			} else if (i < d3) {
				d4 = MathHelper.clampedLerp(d4, -30.0D, (d3 - i) / (d3 - 1.0D));
			}

			noiseColumn[i] = d4;
		}

	}

	@Override
	protected Codec codec() {
		return CODEC;
	}

	@Override
	public void fillFromNoise(IWorld world, StructureManager strManager, IChunk chunk) {
		ObjectList objectlist = new ObjectArrayList(10);
		ObjectList objectlist1 = new ObjectArrayList(32);
		ChunkPos chunkPos = chunk.getPos();
		int chunkX = chunkPos.x;
		int chunkZ = chunkPos.z;
		int blockX = chunkX << 4;
		int blockZ = chunkZ << 4;
		Iterator var11 = Structure.NOISE_AFFECTING_FEATURES.iterator();

		while (var11.hasNext()) {
			Structure structure = (Structure) var11.next();
			strManager.startsForFeature(SectionPos.of(chunkPos, 0), structure).forEach(p_236089_5_ -> {
				Iterator var6 = p_236089_5_.getPieces().iterator();

				while (true) {
					while (true) {
						StructurePiece structurepiece1;
						do {
							if (!var6.hasNext()) {
								return;
							}

							structurepiece1 = (StructurePiece) var6.next();
						} while (!structurepiece1.isCloseToChunk(chunkPos, 12));

						if (structurepiece1 instanceof AbstractVillagePiece) {
							AbstractVillagePiece abstractvillagepiece = (AbstractVillagePiece) structurepiece1;
							PlacementBehaviour jigsawpattern$placementbehaviour = abstractvillagepiece.getElement().getProjection();
							if (jigsawpattern$placementbehaviour == PlacementBehaviour.RIGID) {
								objectlist.add(abstractvillagepiece);
							}

							Iterator var10 = abstractvillagepiece.getJunctions().iterator();

							while (var10.hasNext()) {
								JigsawJunction jigsawjunction1 = (JigsawJunction) var10.next();
								int l5 = jigsawjunction1.getSourceX();
								int i6 = jigsawjunction1.getSourceZ();
								if (l5 > blockX - 12 && i6 > blockZ - 12 && l5 < blockX + 15 + 12 && i6 < blockZ + 15 + 12) {
									objectlist1.add(jigsawjunction1);
								}
							}
						} else {
							objectlist.add(structurepiece1);
						}
					}
				}
			});
		}

		double[][][] adouble = new double[2][noiseSizeZ + 1][noiseSizeY + 1];

		for (int i5 = 0; i5 < noiseSizeZ + 1; ++i5) {
			adouble[0][i5] = new double[noiseSizeY + 1];
			fillNoiseColumn(adouble[0][i5], chunkX * noiseSizeX, chunkZ * noiseSizeZ + i5);
			adouble[1][i5] = new double[noiseSizeY + 1];
		}

		ChunkPrimer chunkprimer = (ChunkPrimer) chunk;
		Heightmap heightmap = chunkprimer.getOrCreateHeightmapUnprimed(Type.OCEAN_FLOOR_WG);
		Heightmap heightmap1 = chunkprimer.getOrCreateHeightmapUnprimed(Type.WORLD_SURFACE_WG);
		Mutable blockpos$mutable = new Mutable();
		ObjectListIterator objectlistiterator = objectlist.iterator();
		ObjectListIterator objectlistiterator1 = objectlist1.iterator();

		for (int i1 = 0; i1 < noiseSizeX; ++i1) {
			int j5;
			for (j5 = 0; j5 < noiseSizeZ + 1; ++j5) {
				fillNoiseColumn(adouble[1][j5], chunkX * noiseSizeX + i1 + 1, chunkZ * noiseSizeZ + j5);
			}

			for (j5 = 0; j5 < noiseSizeZ; ++j5) {
				ChunkSection chunksection = chunkprimer.getOrCreateSection(15);
				chunksection.acquire();

				for (int k1 = noiseSizeY - 1; k1 >= 0; --k1) {
					double d0 = adouble[0][j5][k1];
					double d1 = adouble[0][j5 + 1][k1];
					double d2 = adouble[1][j5][k1];
					double d3 = adouble[1][j5 + 1][k1];
					double d4 = adouble[0][j5][k1 + 1];
					double d5 = adouble[0][j5 + 1][k1 + 1];
					double d6 = adouble[1][j5][k1 + 1];
					double d7 = adouble[1][j5 + 1][k1 + 1];

					for (int l1 = verticalNoiseGranularity - 1; l1 >= 0; --l1) {
						int i2 = k1 * verticalNoiseGranularity + l1;
						int j2 = i2 & 15;
						int k2 = i2 >> 4;
						if (chunksection.bottomBlockY() >> 4 != k2) {
							chunksection.release();
							chunksection = chunkprimer.getOrCreateSection(k2);
							chunksection.acquire();
						}

						double d8 = (double) l1 / (double) verticalNoiseGranularity;
						double d9 = MathHelper.lerp(d8, d0, d4);
						double d10 = MathHelper.lerp(d8, d2, d6);
						double d11 = MathHelper.lerp(d8, d1, d5);
						double d12 = MathHelper.lerp(d8, d3, d7);

						for (int l2 = 0; l2 < horizontalNoiseGranularity; ++l2) {
							int i3 = blockX + i1 * horizontalNoiseGranularity + l2;
							int j3 = i3 & 15;
							double d13 = (double) l2 / (double) horizontalNoiseGranularity;
							double d14 = MathHelper.lerp(d13, d9, d10);
							double d15 = MathHelper.lerp(d13, d11, d12);

							for (int k3 = 0; k3 < horizontalNoiseGranularity; ++k3) {
								int l3 = blockZ + j5 * horizontalNoiseGranularity + k3;
								int i4 = l3 & 15;
								double d16 = (double) k3 / (double) horizontalNoiseGranularity;
								double d17 = MathHelper.lerp(d16, d14, d15);
								double d18 = MathHelper.clamp(d17 / 200.0D, -1.0D, 1.0D);

								int j4;
								int k4;
								int l4;
								for (d18 = d18 / 2.0D - d18 * d18 * d18 / 24.0D; objectlistiterator.hasNext(); d18 += getContribution(j4, k4, l4) * 0.8D) {
									StructurePiece structurepiece = (StructurePiece) objectlistiterator.next();
									MutableBoundingBox mutableboundingbox = structurepiece.getBoundingBox();
									j4 = Math.max(0, Math.max(mutableboundingbox.x0 - i3, i3 - mutableboundingbox.x1));
									k4 = i2 - (mutableboundingbox.y0 + (structurepiece instanceof AbstractVillagePiece ? ((AbstractVillagePiece) structurepiece).getGroundLevelDelta() : 0));
									l4 = Math.max(0, Math.max(mutableboundingbox.z0 - l3, l3 - mutableboundingbox.z1));
								}

								objectlistiterator.back(objectlist.size());

								while (objectlistiterator1.hasNext()) {
									JigsawJunction jigsawjunction = (JigsawJunction) objectlistiterator1.next();
									int k5 = i3 - jigsawjunction.getSourceX();
									j4 = i2 - jigsawjunction.getSourceGroundY();
									k4 = l3 - jigsawjunction.getSourceZ();
									d18 += getContribution(k5, j4, k4) * 0.4D;
								}

								objectlistiterator1.back(objectlist1.size());
								BlockState blockstate = getDefaultStateForHeight(d18, i2);
								if (blockstate != AIR) {
									blockpos$mutable.set(i3, i2, l3);
									if (blockstate.getLightValue(chunkprimer, blockpos$mutable) != 0) {
										chunkprimer.addLight(blockpos$mutable);
									}

									chunksection.setBlockState(j3, j2, i4, blockstate, false);
									heightmap.update(j3, i2, i4, blockstate);
									heightmap1.update(j3, i2, i4, blockstate);
								}
							}
						}
					}
				}

				chunksection.release();
			}

			double[][] adouble1 = adouble[0];
			adouble[0] = adouble[1];
			adouble[1] = adouble1;
		}

	}

	private void fillNoiseColumn(double[] noiseColumn, int noiseX, int noiseZ) {
		double coordScaleXZ = 484.412D;
		double coordScaleY = 1.0D;
		Biome biome = biomeSource.getNoiseBiome(noiseX, getSeaLevel(), noiseZ);
		double xzNoiseScale = LOTRBiomes.getWrapperFor(biome, LOTRBiomes.getServerBiomeContextWorld()).getHorizontalNoiseScale();
		double yNoiseScale = 5000.0D;
		double scaledNoiseXZ = coordScaleXZ / xzNoiseScale;
		double scaledNoiseY = coordScaleY / yNoiseScale;
		int i = -10;
		int j = 3;
		calcNoiseColumn(noiseColumn, noiseX, noiseZ, coordScaleXZ, coordScaleY, scaledNoiseXZ, scaledNoiseY, i, j);
	}

	private double func_222545_a(double baseHeight, double heightVariation, int yIndex) {
		double heightStretch = 12.0D;
		double d1 = (yIndex - (8.5D + baseHeight * 8.5D / 8.0D * 4.0D)) * heightStretch * 128.0D / 256.0D / heightVariation;
		if (d1 < 0.0D) {
			d1 *= 4.0D;
		}

		return d1;
	}

	@Override
	public IBlockReader getBaseColumn(int x, int z) {
		BlockState[] states = new BlockState[noiseSizeY * verticalNoiseGranularity];
		makeBasicTerrain(x, z, states, (Predicate) null);
		return new Blockreader(states);
	}

	@Override
	public int getBaseHeight(int x, int z, Type heightmapType) {
		return makeBasicTerrain(x, z, (BlockState[]) null, heightmapType.isOpaque());
	}

	private double[] getBiomeNoiseColumn(int noiseX, int noiseZ) {
		int blockX = noiseX * noiseSizeX + noiseSizeX / 2;
		int blockZ = noiseZ * noiseSizeZ + noiseSizeZ / 2;
		int seaLevel = getSeaLevel();
		double[] depthAndScale = new double[2];
		float totalScale = 0.0F;
		float totalDepth = 0.0F;
		float totalDepthWithoutVariants = 0.0F;
		float totalAddedDepthNoiseStrength = 0.0F;
		float totalModifiedSignificance = 0.0F;
		float totalSignificance = 0.0F;
		float centralDepth = biomeSource.getNoiseBiome(noiseX, seaLevel, noiseZ).getDepth();
		ServerWorld world = LOTRBiomes.getServerBiomeContextWorld();

		float depth;
		float addedDepthNoiseStrength;
		for (int k = -6; k <= 6; ++k) {
			for (int l = -6; l <= 6; ++l) {
				Biome biome = biomeSource.getNoiseBiome(noiseX + k, seaLevel, noiseZ + l);
				depth = biome.getDepth();
				LOTRBiomeWrapper biomeWrapper = LOTRBiomes.getWrapperFor(biome, world);
				addedDepthNoiseStrength = biomeWrapper.getStrengthOfAddedDepthNoise();
				float scaleSignificance = biomeWrapper.getBiomeScaleSignificanceForChunkGen();
				float scale = biome.getScale() * scaleSignificance + (1.0F - scaleSignificance);
				if (scale == 0.0F) {
					scale = 1.0E-7F;
				}

				if (isAmplified && depth > 0.0F) {
					depth = 1.0F + depth * 2.0F;
					scale = 1.0F + scale * 4.0F;
				}

				int arrayIndex = k + 6 + (l + 6) * 13;
				float significance = BIOME_SAMPLING_SIGNIFICANCE[arrayIndex];
				float modifiedSignificance = significance / (depth + 2.0F);
				if (biome.getDepth() > centralDepth) {
					modifiedSignificance /= 2.0F;
				}

				if (biomeWrapper.isRiver()) {
					float affectingFactor = 5.0F;
					if (centralDepth < 0.0F) {
						affectingFactor *= 2.0F;
					}

					modifiedSignificance *= affectingFactor;
				}

				totalScale += scale * modifiedSignificance;
				totalDepth += depth * modifiedSignificance;
				totalDepthWithoutVariants += depth * modifiedSignificance;
				totalModifiedSignificance += modifiedSignificance;
				totalAddedDepthNoiseStrength += addedDepthNoiseStrength * significance;
				totalSignificance += significance;
			}
		}

		float avgScale = totalScale / totalModifiedSignificance;
		float avgDepth = totalDepth / totalModifiedSignificance;
		float avgDepthWithoutVariants = totalDepthWithoutVariants / totalModifiedSignificance;
		depth = totalAddedDepthNoiseStrength / totalSignificance;
		float lerpFactor;
		if (LOTRWorldTypes.hasMapFeatures(world)) {
			lerpFactor = MapSettingsManager.serverInstance().getCurrentLoadedMap().getRoadPointCache().getRoadCentreCloseness(blockX, blockZ, 64);
			if (lerpFactor >= 0.0F) {
				addedDepthNoiseStrength = Math.min(lerpFactor + 0.15F, 1.0F) * 0.85F;
				avgDepth += (avgDepthWithoutVariants - avgDepth) * addedDepthNoiseStrength;
				avgScale *= 1.0F - addedDepthNoiseStrength;
			}
		}

		if (centralDepth < 0.0F && avgDepth >= 0.0F) {
			lerpFactor = 0.5F;
			avgDepth = MathHelper.lerp(lerpFactor, avgDepth, centralDepth / 2.0F);
		}

		double depthNoiseAdd = ((DimensionSettings) dimensionSettings.get()).noiseSettings().randomDensityOffset() ? getNoiseDepthAt(noiseX, noiseZ) : 0.0D;
		depthNoiseAdd = (depthNoiseAdd * 8.0D + 1.0D) / 4.0D;
		avgDepth = (float) (avgDepth + depthNoiseAdd * depth);
		avgDepth = (avgDepth * 4.0F - 1.0F) / 8.0F;
		depthAndScale[0] = avgDepth;
		depthAndScale[1] = avgScale;
		return depthAndScale;
	}

	protected BlockState getDefaultStateForHeight(double terrainNoise, int height) {
		if (terrainNoise > 0.0D) {
			return defaultBlock;
		}
		return height < getSeaLevel() ? defaultFluid : AIR;
	}

	@Override
	public int getGenDepth() {
		return chunkGenHeight;
	}

	@Override
	public List getMobsAt(Biome biome, StructureManager strManager, EntityClassification creatureType, BlockPos pos) {
		return LOTRBiomes.getWrapperFor(biome, LOTRBiomes.getServerBiomeContextWorld()).getSpawnsAtLocation(creatureType, pos);
	}

	private double getNoiseDepthAt(int noiseX, int noiseZ) {
		double d0 = depthNoise.getValue(noiseX * 200, 10.0D, noiseZ * 200, 1.0D, 0.0D, true) * 65535.0D / 8000.0D;
		double d1;
		if (d0 < 0.0D) {
			d1 = -d0 * 0.3D;
		} else {
			d1 = d0;
		}

		double d2 = d1 * 3.0D - 2.0D;
		return d2 < 0.0D ? d2 / 28.0D : Math.min(d2, 1.0D) / 40.0D;
	}

	@Override
	public int getSeaLevel() {
		return ((DimensionSettings) dimensionSettings.get()).seaLevel();
	}

	public void hackySetWorldTypeClassicBiomes(boolean flag) {
		if (biomeSource instanceof MiddleEarthBiomeProvider) {
			((MiddleEarthBiomeProvider) biomeSource).hackySetWorldTypeClassicBiomes(flag);
		}

	}

	public void hackySetWorldTypeInstantMiddleEarth(boolean flag) {
		if (!isInstantMiddleEarth.isPresent()) {
			isInstantMiddleEarth = Optional.of(flag);
		}
	}
	public boolean isClassicBiomes() {
		return biomeSource instanceof MiddleEarthBiomeProvider && ((MiddleEarthBiomeProvider) biomeSource).isClassicBiomes();
	}

	public boolean isInstantMiddleEarth() {
		return (Boolean) isInstantMiddleEarth.orElse(false);
	}

	private double[] makeAndFillNoiseColumn(int x, int z) {
		double[] noise = new double[noiseSizeY + 1];
		fillNoiseColumn(noise, x, z);
		return noise;
	}

	private int makeBasicTerrain(int x, int z, @Nullable BlockState[] stateArray, @Nullable Predicate heightmapTest) {
		int i = Math.floorDiv(x, horizontalNoiseGranularity);
		int j = Math.floorDiv(z, horizontalNoiseGranularity);
		int k = Math.floorMod(x, horizontalNoiseGranularity);
		int l = Math.floorMod(z, horizontalNoiseGranularity);
		double d0 = (double) k / (double) horizontalNoiseGranularity;
		double d1 = (double) l / (double) horizontalNoiseGranularity;
		double[][] adouble = { makeAndFillNoiseColumn(i, j), makeAndFillNoiseColumn(i, j + 1), makeAndFillNoiseColumn(i + 1, j), makeAndFillNoiseColumn(i + 1, j + 1) };

		for (int i1 = noiseSizeY - 1; i1 >= 0; --i1) {
			double d2 = adouble[0][i1];
			double d3 = adouble[1][i1];
			double d4 = adouble[2][i1];
			double d5 = adouble[3][i1];
			double d6 = adouble[0][i1 + 1];
			double d7 = adouble[1][i1 + 1];
			double d8 = adouble[2][i1 + 1];
			double d9 = adouble[3][i1 + 1];

			for (int j1 = verticalNoiseGranularity - 1; j1 >= 0; --j1) {
				double d10 = (double) j1 / (double) verticalNoiseGranularity;
				double d11 = MathHelper.lerp3(d10, d0, d1, d2, d6, d4, d8, d3, d7, d5, d9);
				int k1 = i1 * verticalNoiseGranularity + j1;
				BlockState blockstate = getDefaultStateForHeight(d11, k1);
				if (stateArray != null) {
					stateArray[k1] = blockstate;
				}

				if (heightmapTest != null && heightmapTest.test(blockstate)) {
					return k1 + 1;
				}
			}
		}

		return 0;
	}

	private void makeBedrock(IChunk chunkIn, Random rand) {
		Mutable movingPos = new Mutable();
		int i = chunkIn.getPos().getMinBlockX();
		int j = chunkIn.getPos().getMinBlockZ();
		DimensionSettings dimensionsettings = (DimensionSettings) dimensionSettings.get();
		int k = dimensionsettings.getBedrockFloorPosition();
		int l = chunkGenHeight - 1 - dimensionsettings.getBedrockRoofPosition();
		boolean flag = l + 4 >= 0 && l < chunkGenHeight;
		boolean flag1 = k + 4 >= 0 && k < chunkGenHeight;
		if (flag || flag1) {
			Iterator var12 = BlockPos.betweenClosed(i, 0, j, i + 15, 0, j + 15).iterator();

			while (true) {
				BlockPos blockpos;
				int k1;
				do {
					if (!var12.hasNext()) {
						return;
					}

					blockpos = (BlockPos) var12.next();
					if (flag) {
						for (k1 = 0; k1 < 5; ++k1) {
							if (k1 <= rand.nextInt(5)) {
								chunkIn.setBlockState(movingPos.set(blockpos.getX(), l - k1, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
							}
						}
					}
				} while (!flag1);

				for (k1 = 4; k1 >= 0; --k1) {
					if (k1 <= rand.nextInt(5)) {
						chunkIn.setBlockState(movingPos.set(blockpos.getX(), k + k1, blockpos.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
					}
				}
			}
		}
	}

	private double sampleAndClampNoise(int p_222552_1_, int p_222552_2_, int p_222552_3_, double p_222552_4_, double p_222552_6_, double p_222552_8_, double p_222552_10_) {
		double d0 = 0.0D;
		double d1 = 0.0D;
		double d2 = 0.0D;
		double d3 = 1.0D;

		for (int i = 0; i < 16; ++i) {
			double d4 = OctavesNoiseGenerator.wrap(p_222552_1_ * p_222552_4_ * d3);
			double d5 = OctavesNoiseGenerator.wrap(p_222552_2_ * p_222552_6_ * d3);
			double d6 = OctavesNoiseGenerator.wrap(p_222552_3_ * p_222552_4_ * d3);
			double d7 = p_222552_6_ * d3;
			ImprovedNoiseGenerator improvednoisegenerator = minLimitPerlinNoise.getOctaveNoise(i);
			if (improvednoisegenerator != null) {
				d0 += improvednoisegenerator.noise(d4, d5, d6, d7, p_222552_2_ * d7) / d3;
			}

			ImprovedNoiseGenerator improvednoisegenerator1 = maxLimitPerlinNoise.getOctaveNoise(i);
			if (improvednoisegenerator1 != null) {
				d1 += improvednoisegenerator1.noise(d4, d5, d6, d7, p_222552_2_ * d7) / d3;
			}

			if (i < 8) {
				ImprovedNoiseGenerator improvednoisegenerator2 = mainPerlinNoise.getOctaveNoise(i);
				if (improvednoisegenerator2 != null) {
					d2 += improvednoisegenerator2.noise(OctavesNoiseGenerator.wrap(p_222552_1_ * p_222552_8_ * d3), OctavesNoiseGenerator.wrap(p_222552_2_ * p_222552_10_ * d3), OctavesNoiseGenerator.wrap(p_222552_3_ * p_222552_8_ * d3), p_222552_10_ * d3, p_222552_2_ * p_222552_10_ * d3) / d3;
				}
			}

			d3 /= 2.0D;
		}

		return MathHelper.clampedLerp(d0 / 512.0D, d1 / 512.0D, (d2 / 10.0D + 1.0D) / 2.0D);
	}

	@Override
	public void spawnOriginalMobs(WorldGenRegion region) {
		if (!((DimensionSettings) dimensionSettings.get()).disableMobGeneration()) {
			int i = region.getCenterX();
			int j = region.getCenterZ();
			Biome biome = region.getBiome(new ChunkPos(i, j).getWorldPosition());
			SharedSeedRandom sharedseedrandom = new SharedSeedRandom();
			sharedseedrandom.setDecorationSeed(region.getSeed(), i << 4, j << 4);
			WorldEntitySpawner.spawnMobsForChunkGeneration(region, biome, i, j, sharedseedrandom);
		}

	}

	@Override
	@OnlyIn(Dist.CLIENT)
	public ChunkGenerator withSeed(long otherSeed) {
		return new MiddleEarthChunkGenerator(biomeSource.withSeed(otherSeed), otherSeed, dimensionSettings, Optional.of(isInstantMiddleEarth()));
	}

	private static double computeContribution(int x, int y, int z) {
		double hSq = x * x + z * z;
		double v = y + 0.5D;
		double vSq = v * v;
		double d3 = Math.pow(2.718281828459045D, -(vSq / 16.0D + hSq / 16.0D));
		double d4 = -v * MathHelper.fastInvSqrt(vSq / 2.0D + hSq / 2.0D) / 2.0D;
		return d4 * d3;
	}

	private static double getContribution(int p_222556_0_, int p_222556_1_, int p_222556_2_) {
		int sample = 24;
		int hSample = sample / 2;
		int i = p_222556_0_ + hSample;
		int j = p_222556_1_ + hSample;
		int k = p_222556_2_ + hSample;
		if (i < 0 || i >= sample) {
			return 0.0D;
		}
		if (j >= 0 && j < sample) {
			return k >= 0 && k < sample ? (double) CUBIC_SAMPLE[k * sample * sample + i * sample + j] : 0.0D;
		}
		return 0.0D;
	}
}
