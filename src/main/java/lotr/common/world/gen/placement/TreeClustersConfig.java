package lotr.common.world.gen.placement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.world.gen.feature.TreeCluster;
import net.minecraft.world.gen.placement.IPlacementConfig;

public class TreeClustersConfig implements IPlacementConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.INT.fieldOf("count").orElse(0).forGetter(config -> ((TreeClustersConfig) config).count), Codec.FLOAT.fieldOf("extra_chance").orElse(0.1F).forGetter(config -> ((TreeClustersConfig) config).extraChance), Codec.INT.fieldOf("extra_count").orElse(1).forGetter(config -> ((TreeClustersConfig) config).extraCount), Codec.INT.fieldOf("cluster_scale").orElse(0).forGetter(config -> ((TreeClustersConfig) config).clusterScale), Codec.INT.fieldOf("cluster_chance").orElse(-1).forGetter(config -> ((TreeClustersConfig) config).clusterChance), Codec.INT.fieldOf("cluster_extra_count").orElse(6).forGetter(config -> ((TreeClustersConfig) config).clusterExtraCount), Codec.INT.fieldOf("cluster_random_extra_count").orElse(4).forGetter(config -> ((TreeClustersConfig) config).clusterRandomExtraCount), Codec.INT.fieldOf("layer_limit").orElse(-1).forGetter(config -> ((TreeClustersConfig) config).layerLimit), Codec.BOOL.fieldOf("is_layer_upper_limit").orElse(false).forGetter(config -> ((TreeClustersConfig) config).isLayerUpperLimit)).apply(instance, TreeClustersConfig::new));
	public final int count;
	public final float extraChance;
	public final int extraCount;
	public final int clusterScale;
	public final int clusterChance;
	public final int clusterExtraCount;
	public final int clusterRandomExtraCount;
	public final int layerLimit;
	public final boolean isLayerUpperLimit;

	private TreeClustersConfig(int count, float extraChance, int extraCount, int clusterScale, int clusterChance, int clusterExtraCount, int clusterRandomExtraCount, int layerLimit, boolean isLayerUpperLimit) {
		this.count = count;
		this.extraChance = extraChance;
		this.extraCount = extraCount;
		this.clusterScale = clusterScale;
		this.clusterChance = clusterChance;
		this.clusterExtraCount = clusterExtraCount;
		this.clusterRandomExtraCount = clusterRandomExtraCount;
		this.layerLimit = layerLimit;
		this.isLayerUpperLimit = isLayerUpperLimit;
	}

	// $FF: synthetic method
	TreeClustersConfig(int x0, float x1, int x2, int x3, int x4, int x5, int x6, int x7, boolean x8, Object x9) {
		this(x0, x1, x2, x3, x4, x5, x6, x7, x8);
	}

	public boolean hasLayerLimit() {
		return layerLimit >= 0;
	}

	public static TreeClustersConfig.Builder builder() {
		return new TreeClustersConfig.Builder();
	}

	public static class Builder {
		private int count;
		private float extraChance;
		private int extraCount;
		private int clusterScale;
		private int clusterChance;
		private int clusterExtraCount;
		private int clusterRandomExtraCount;
		private int layerLimit;
		private boolean isLayerUpperLimit;

		private Builder() {
			count = 0;
			extraChance = 0.1F;
			extraCount = 1;
			clusterScale = 0;
			clusterChance = -1;
			clusterExtraCount = 6;
			clusterRandomExtraCount = 4;
			layerLimit = -1;
			isLayerUpperLimit = false;
		}

		// $FF: synthetic method
		Builder(Object x0) {
			this();
		}

		public TreeClustersConfig build() {
			return new TreeClustersConfig(count, extraChance, extraCount, clusterScale, clusterChance, clusterExtraCount, clusterRandomExtraCount, layerLimit, isLayerUpperLimit);
		}

		public TreeClustersConfig.Builder cluster(TreeCluster cluster) {
			clusterScale = cluster.scale;
			clusterChance = cluster.chance;
			return this;
		}

		public TreeClustersConfig.Builder clusterExtraCount(int clusterExtraCount) {
			this.clusterExtraCount = clusterExtraCount;
			return this;
		}

		public TreeClustersConfig.Builder clusterRandomExtraCount(int clusterRandomExtraCount) {
			this.clusterRandomExtraCount = clusterRandomExtraCount;
			return this;
		}

		public TreeClustersConfig.Builder count(int count) {
			this.count = count;
			return this;
		}

		public TreeClustersConfig.Builder extraChance(float extraChance) {
			this.extraChance = extraChance;
			return this;
		}

		public TreeClustersConfig.Builder extraCount(int extraCount) {
			this.extraCount = extraCount;
			return this;
		}

		public TreeClustersConfig.Builder layerLimit(int layerLimit, boolean isLayerUpperLimit) {
			this.layerLimit = layerLimit;
			this.isLayerUpperLimit = isLayerUpperLimit;
			return this;
		}
	}
}
