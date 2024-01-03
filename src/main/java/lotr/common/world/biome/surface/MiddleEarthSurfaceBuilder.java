package lotr.common.world.biome.surface;

import java.util.Iterator;
import java.util.Random;

import com.mojang.serialization.Codec;

import lotr.common.init.LOTRBiomes;
import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.LOTRBiomeBase;
import lotr.common.world.map.MapSettings;
import lotr.common.world.map.MapSettingsManager;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.IChunk;
import net.minecraft.world.gen.PerlinNoiseGenerator;
import net.minecraft.world.gen.surfacebuilders.FrozenOceanSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

public class MiddleEarthSurfaceBuilder extends SurfaceBuilder<MiddleEarthSurfaceConfig> {
	private final FrozenOceanSurfaceBuilder frozenOcean;
	private final PerlinNoiseGenerator icebergBorderNoise;

	public MiddleEarthSurfaceBuilder(Codec<MiddleEarthSurfaceConfig> codec) {
		super(codec);
		frozenOcean = new FrozenOceanSurfaceBuilder(SurfaceBuilderConfig.CODEC);
		icebergBorderNoise = LOTRBiomeBase.makeSingleLayerPerlinNoise(5231241491057810726L);
	}

	@Override
	public void apply(Random rand, IChunk chunk, Biome biome, int x, int z, int startHeight, double noise, BlockState defaultBlock, BlockState defaultFluid, int seaLevel, long seed, MiddleEarthSurfaceConfig configg) {
		MiddleEarthSurfaceConfig config = (MiddleEarthSurfaceConfig) configg;
		if (isFrozenIcebergTerrain(chunk, biome, x, z)) {
			frozenOcean.apply(rand, chunk, biome, x, z, startHeight, noise, defaultBlock, defaultFluid, seaLevel, seed, SurfaceBuilder.CONFIG_GRASS);
		} else {
			int chunkX = x & 15;
			int chunkZ = z & 15;
			Mutable movingPos = new Mutable();
			BlockState top = config.getSurfaceNoiseReplacement(x, z, config.getTopMaterial(), true, rand);
			BlockState filler = config.getSurfaceNoiseReplacement(x, z, config.getUnderMaterial(), false, rand);
			BlockState underwater = config.getUnderwaterNoiseReplacement(x, z, config.getUnderwaterMaterial(), rand);
			int digDepth = -1;
			int soilDepth = (int) (noise * 0.25D + config.getFillerDepth() + rand.nextDouble() * 0.25D);
			if (soilDepth < 0) {
				soilDepth = 0;
			}

			int determinedTopTerrainY = -1;
			float pdzRand;
			int y;
			if (config.hasRockyTerrain()) {
				int topBlock = startHeight - 1;
				movingPos.set(chunkX, topBlock, chunkZ);
				if (topBlock >= 90 && chunk.getBlockState(movingPos) == defaultBlock) {
					float hFactor = (topBlock - 90) / 10.0F;
					pdzRand = 0.6F - hFactor * 0.1F;
					pdzRand = Math.max(pdzRand, 0.0F);
					double rockyNoise = MiddleEarthSurfaceConfig.getNoise1(x, z, 0.3D, 0.03D);
					if (rockyNoise > pdzRand) {
						if (rand.nextFloat() < 0.2F) {
							top = Blocks.GRAVEL.defaultBlockState();
						} else {
							top = Blocks.STONE.defaultBlockState();
						}

						y = topBlock;
						if (rand.nextInt(20) == 0) {
							++topBlock;
						}

						for (y = topBlock; y >= y; --y) {
							movingPos.set(chunkX, y, chunkZ);
							chunk.setBlockState(movingPos, top, false);
						}
					}
				}
			}

			double randNoise;
			if (config.hasPodzol() && startHeight - 1 <= config.getMaxPodzolHeight() && !config.isMarsh()) {
				float podzolMinThreshold = 0.0F;
				boolean podzolHere = false;
				if (top.getBlock() == Blocks.GRASS_BLOCK) {
					pdzRand = config.getTreeDensityForPodzol();
					if (pdzRand >= 1.5F) {
						float threshold = 0.8F;
						threshold -= pdzRand * 0.15F;
						threshold = Math.max(threshold, podzolMinThreshold);
						randNoise = MiddleEarthSurfaceConfig.getNoise2(x, z, 0.05D);
						if (randNoise > threshold) {
							podzolHere = true;
						}
					}
				}

				if (podzolHere) {
					pdzRand = rand.nextFloat();
					if (pdzRand < 0.45F) {
						top = Blocks.PODZOL.defaultBlockState();
					} else if (pdzRand < 0.6F) {
						top = Blocks.COARSE_DIRT.defaultBlockState();
					} else if (pdzRand < 0.605F) {
						top = Blocks.GRAVEL.defaultBlockState();
					}
				}
			}

			if (config.isMarsh()) {
				double marshNoiseScale1 = 0.25D;
				double marshNoiseScale2 = 0.05D;
				randNoise = (MiddleEarthSurfaceConfig.MARSH_NOISE.getValue(x * marshNoiseScale1, z * marshNoiseScale1, false) + MiddleEarthSurfaceConfig.MARSH_NOISE.getValue(x * marshNoiseScale2, z * marshNoiseScale2, false)) / 2.0D;
				if (randNoise > -0.1D) {
					for (y = startHeight; y >= 0; --y) {
						movingPos.set(chunkX, y, chunkZ);
						BlockState currentState = chunk.getBlockState(movingPos);
						if (!currentState.isAir()) {
							if (y == seaLevel - 1 && !currentState.getMaterial().isLiquid()) {
								chunk.setBlockState(movingPos, Blocks.WATER.defaultBlockState(), false);
							}
							break;
						}
					}
				}
			}

			BlockState topToUse = top;
			BlockState fillerToUse = filler;
			Iterator<MiddleEarthSurfaceConfig.SubSoilLayer> subSoilLayers = config.getSubSoilLayers();

			for (y = startHeight; y >= 0; --y) {
				movingPos.set(chunkX, y, chunkZ);
				BlockState currentState = chunk.getBlockState(movingPos);
				if (currentState.isAir()) {
					digDepth = -1;
				} else if (currentState.getBlock() == defaultBlock.getBlock()) {
					if (digDepth == -1) {
						if (soilDepth < 0) {
							topToUse = Blocks.AIR.defaultBlockState();
							fillerToUse = defaultBlock;
						} else if (y >= seaLevel - 4 && y <= seaLevel + 1) {
							topToUse = top;
							fillerToUse = filler;
						}

						if (y < seaLevel && (topToUse == null || topToUse.isAir())) {
							if (biome.getTemperature(movingPos.set(x, y, z)) < 0.15F) {
								topToUse = Blocks.ICE.defaultBlockState();
							} else {
								topToUse = defaultFluid;
							}

							movingPos.set(chunkX, y, chunkZ);
						}

						digDepth = soilDepth;
						if (y >= seaLevel - 1) {
							chunk.setBlockState(movingPos, topToUse, false);
						} else if (y < seaLevel - 7 - soilDepth) {
							topToUse = Blocks.AIR.defaultBlockState();
							fillerToUse = defaultBlock;
							chunk.setBlockState(movingPos, underwater, false);
						} else {
							chunk.setBlockState(movingPos, fillerToUse, false);
						}

						if (determinedTopTerrainY == -1) {
							determinedTopTerrainY = y;
						}
					} else if (digDepth > 0) {
						--digDepth;
						chunk.setBlockState(movingPos, fillerToUse, false);
					} else if (digDepth == 0) {
						if (subSoilLayers.hasNext()) {
							MiddleEarthSurfaceConfig.SubSoilLayer subSoilLayer = (MiddleEarthSurfaceConfig.SubSoilLayer) subSoilLayers.next();
							digDepth = subSoilLayer.getDepth(rand);
							fillerToUse = subSoilLayer.getMaterial();
						} else if (soilDepth > 1) {
							if (fillerToUse.getBlock() == Blocks.SAND) {
								digDepth = rand.nextInt(4) + Math.max(0, y - seaLevel);
								fillerToUse = Blocks.SANDSTONE.defaultBlockState();
							} else if (fillerToUse.getBlock() == Blocks.RED_SAND) {
								digDepth = rand.nextInt(4) + Math.max(0, y - seaLevel);
								fillerToUse = Blocks.RED_SANDSTONE.defaultBlockState();
							} else if (fillerToUse.getBlock() == LOTRBlocks.WHITE_SAND.get()) {
								digDepth = rand.nextInt(4) + Math.max(0, y - seaLevel);
								fillerToUse = ((Block) LOTRBlocks.WHITE_SANDSTONE.get()).defaultBlockState();
							}
						}

						if (digDepth > 0) {
							--digDepth;
							chunk.setBlockState(movingPos, fillerToUse, false);
						}
					}
				}
			}

			if (config.hasMountainTerrain()) {
				y = (int) (noise * 6.0D + 2.0D + rand.nextDouble() * 1.0D);
				boolean passedTopBlock = false;

				for (y = determinedTopTerrainY; y >= 0; --y) {
					movingPos.set(chunkX, y, chunkZ);
					BlockState currentState = chunk.getBlockState(movingPos);
					if (!currentState.getFluidState().isEmpty()) {
						break;
					}

					if (!currentState.isAir()) {
						boolean isTop = !passedTopBlock;
						BlockState mountainBlock = config.getMountainTerrain(x, z, y, currentState, defaultBlock, isTop, y);
						if (mountainBlock != currentState) {
							chunk.setBlockState(movingPos, mountainBlock, false);
						}

						if (isTop && !passedTopBlock) {
							passedTopBlock = true;
						}
					}
				}
			}

		}
	}

	@Override
	public void initNoise(long seed) {
		frozenOcean.initNoise(seed);
	}

	private boolean isFrozenIcebergTerrain(IChunk chunk, Biome biome, int x, int z) {
		if (LOTRBiomes.areBiomesEqual(biome, LOTRBiomes.SEA.getInitialisedBiome(), chunk.getWorldForge())) {
			MapSettings map = MapSettingsManager.serverInstance().getCurrentLoadedMap();
			int zBlurRange = 32;
			float icebergThreshold = 0.5F;
			int minAdjustedZ = z - zBlurRange;
			if (map.getWaterLatitudes().getIceCoverageForLatitude(minAdjustedZ) > icebergThreshold) {
				double xBlurScale = 1.0D / zBlurRange;
				int adjustedZ = z + (int) (icebergBorderNoise.getValue(x * xBlurScale, 0.0D, false) * zBlurRange);
				return map.getWaterLatitudes().getIceCoverageForLatitude(adjustedZ) > icebergThreshold;
			}
		}

		return false;
	}
}
