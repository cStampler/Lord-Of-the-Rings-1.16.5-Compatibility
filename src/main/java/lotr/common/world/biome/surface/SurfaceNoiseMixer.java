package lotr.common.world.biome.surface;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.WeightedList;
import net.minecraftforge.fml.RegistryObject;

public final class SurfaceNoiseMixer {
	public static Condition[] sus = {};
	public static final SurfaceNoiseMixer NONE = SurfaceNoiseMixer.createNoiseMixer(sus);

	public static final Codec<SurfaceNoiseMixer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			SurfaceNoiseMixer.Condition.CODEC.listOf().fieldOf("conditions").forGetter(mixer -> mixer.conditions)
	).apply(instance, SurfaceNoiseMixer::new));
	private final List<Condition> conditions;
	private final Set<Block> cachedSurfaceBlocks;

	private SurfaceNoiseMixer(List<Condition> conditions) {
		this.conditions = conditions;
		cachedSurfaceBlocks = conditions.stream()
				.flatMap(c -> c.weightedStates.stream().map(hummel -> hummel.getBlock()))
				.distinct()
				.collect(Collectors.toSet());
	}

	public BlockState getReplacement(int x, int z, BlockState in, boolean top, Random rand) {
		Iterator<Condition> var6 = conditions.iterator();

		SurfaceNoiseMixer.Condition condition;
		do {
			if (!var6.hasNext()) {
				return in;
			}

			condition = var6.next();
		} while (!condition.passes(x, z, top));

		return condition.getState(rand);
	}

	public boolean isSurfaceBlock(BlockState state) {
		return cachedSurfaceBlocks.contains(state.getBlock());
	}

	public static SurfaceNoiseMixer createNoiseMixer(SurfaceNoiseMixer.Condition... conditions) {
		return new SurfaceNoiseMixer(Arrays.asList(conditions));
	}

	public static SurfaceNoiseMixer createNoiseMixer(SurfaceNoiseMixer.Condition.ConditionBuilder... builders) {
		return new SurfaceNoiseMixer(Stream.of(builders).map(SurfaceNoiseMixer.Condition.ConditionBuilder::build).collect(Collectors.toList()));
	}

	public static final class Condition {
		public static final Codec<Condition> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.intRange(1, 4).fieldOf("noise_index").forGetter(cond -> cond.noiseIndex),
				Codec.DOUBLE.listOf().fieldOf("scales").forGetter(cond -> toDoubleList(cond.scales)),
				Codec.DOUBLE.listOf().fieldOf("x_scales").orElse(ImmutableList.of()).forGetter(cond -> toDoubleList(cond.xScales)),
				Codec.DOUBLE.listOf().fieldOf("z_scales").orElse(ImmutableList.of()).forGetter(cond -> toDoubleList(cond.zScales)),
				Codec.intRange(1, Integer.MAX_VALUE).listOf().fieldOf("weights").orElse(ImmutableList.of()).forGetter(cond -> toIntList(cond.weights)),
				Codec.DOUBLE.fieldOf("threshold").forGetter(cond -> cond.threshold),
				WeightedList.codec(BlockState.CODEC).fieldOf("states").forGetter(cond -> cond.weightedStates),
				Codec.BOOL.fieldOf("top_only").orElse(false).forGetter(cond -> cond.topOnly)
		).apply(instance, SurfaceNoiseMixer.Condition::new));
		private int noiseIndex;
		private double[] scales;
		private double[] xScales;
		private double[] zScales;
		private int[] weights;
		private double threshold;
		private WeightedList<BlockState> weightedStates;
		private boolean topOnly;

		private Condition(int noiseIndex, double[] scales, double[] xScales, double[] zScales, int[] weights, double threshold, WeightedList<BlockState> weightedStates, boolean topOnly) {
			this.noiseIndex = noiseIndex;
			this.scales = scales;
			this.xScales = xScales != null ? xScales : new double[0];
			this.zScales = zScales != null ? zScales : new double[0];
			this.weights = weights != null ? weights : new int[0];
			this.threshold = threshold;
			this.weightedStates = weightedStates;
			this.topOnly = topOnly;
		}

		private Condition(int noiseIndex, List<Double> scales, List<Double> xScales, List<Double> zScales, List<Integer> weights, double threshold, WeightedList<BlockState> weightedStates, boolean topOnly) {
			this(noiseIndex, toDoubleArray(scales), toDoubleArray(xScales), toDoubleArray(zScales), toIntArray(weights), threshold, weightedStates, topOnly);
		}

		private Condition(SurfaceNoiseMixer.Condition.ConditionBuilder builder) {
			this(builder.noiseIndex, builder.scales, builder.xScales, builder.zScales, builder.weights, builder.threshold, builder.weightedStates, builder.topOnly);
		}

		public BlockState getState(Random rand) {
			return weightedStates.getOne(rand);
		}

		public boolean passes(int x, int z, boolean top) {
			if (topOnly && !top) {
				return false;
			}
			switch (noiseIndex) {
				case 1:
					return MiddleEarthSurfaceConfig.getNoise1(x, z, scales, xScales, zScales, weights) > threshold;
				case 2:
					return MiddleEarthSurfaceConfig.getNoise2(x, z, scales, xScales, zScales, weights) > threshold;
				case 3:
					return MiddleEarthSurfaceConfig.getNoise3(x, z, scales, xScales, zScales, weights) > threshold;
				case 4:
					return MiddleEarthSurfaceConfig.getNoise4(x, z, scales, xScales, zScales, weights) > threshold;
				default:
					throw new IllegalStateException("Noise index " + noiseIndex + " does not correspond to a predefined noise generator");
			}
		}

		public static SurfaceNoiseMixer.Condition.ConditionBuilder conditionBuilder() {
			return new SurfaceNoiseMixer.Condition.ConditionBuilder();
		}

		private static double[] toDoubleArray(List<Double> list) {
			return list.stream().mapToDouble(Double::doubleValue).toArray();
		}

		private static int[] toIntArray(List<Integer> list) {
			return list.stream().mapToInt(Integer::intValue).toArray();
		}

		private static List<Double> toDoubleList(double[] array) {
			return Arrays.stream(array).boxed().collect(Collectors.toList());
		}

		private static List<Integer> toIntList(int[] array) {
			return Arrays.stream(array).boxed().collect(Collectors.toList());
		}

		public static class ConditionBuilder {
			private int noiseIndex;
			private double[] scales;
			private double[] xScales;
			private double[] zScales;
			private int[] weights;
			private double threshold;
			private WeightedList<BlockState> weightedStates;
			private boolean topOnly;

			private ConditionBuilder() {
				threshold = Double.NEGATIVE_INFINITY;
				topOnly = false;
			}


			public SurfaceNoiseMixer.Condition build() {
				if (noiseIndex < 1 || noiseIndex > 4) {
					throw new IllegalArgumentException("noiseIndex out of supported range");
				}
				if (scales == null) {
					throw new IllegalArgumentException("scales not set");
				}
				if (xScales != null && xScales.length != scales.length) {
					throw new IllegalArgumentException("number of custom xScales does not match number of scales");
				}
				if (zScales != null && zScales.length != scales.length) {
					throw new IllegalArgumentException("number of custom zScales does not match number of scales");
				}
				if (weights != null && weights.length != scales.length) {
					throw new IllegalArgumentException("number of custom weights does not match number of scales");
				}
				if (threshold == Double.NEGATIVE_INFINITY) {
					throw new IllegalArgumentException("threshold not set");
				}
				if (weightedStates != null && !weightedStates.isEmpty()) {
					return new SurfaceNoiseMixer.Condition(this);
				}
				throw new IllegalArgumentException("block state(s) not set");
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder noiseIndex(int noiseIndex) {
				this.noiseIndex = noiseIndex;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder scales(double... scales) {
				this.scales = scales;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder state(Block block) {
				return this.state(block.defaultBlockState());
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder state(BlockState state) {
				weightedStates = new WeightedList<BlockState>().add(state, 1);
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder state(RegistryObject<Block> blockSup) {
				return this.state(blockSup.get());
			}

			@SuppressWarnings("rawtypes")
			public SurfaceNoiseMixer.Condition.ConditionBuilder states(Object... entries) {
				weightedStates = new WeightedList<BlockState>();

				for (int i = 0; i < entries.length; i += 2) {
					Object obj1 = entries[i];
					BlockState state;
					if (obj1 instanceof BlockState) {
						state = (BlockState) obj1;
					} else if (obj1 instanceof Block) {
						state = ((Block) obj1).defaultBlockState();
					} else {
						if (!(obj1 instanceof RegistryObject)) {
							throw new IllegalArgumentException("Surface noise mixer cannot convert object " + obj1.toString() + " to a weighted blockstate");
						}

						state = ((Block) ((RegistryObject) obj1).get()).defaultBlockState();
					}

					int weight = (Integer) entries[i + 1];
					weightedStates.add(state, weight);
				}

				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder threshold(double threshold) {
				this.threshold = threshold;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder topOnly() {
				topOnly = true;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder weights(int... weights) {
				this.weights = weights;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder xScales(double... xScales) {
				this.xScales = xScales;
				return this;
			}

			public SurfaceNoiseMixer.Condition.ConditionBuilder zScales(double... zScales) {
				this.zScales = zScales;
				return this;
			}
		}
	}
}