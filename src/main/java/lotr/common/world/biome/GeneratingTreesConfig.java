package lotr.common.world.biome;

import lotr.common.world.gen.feature.LatitudeBasedFeatureConfig;
import lotr.common.world.gen.placement.TreeClustersConfig;

public class GeneratingTreesConfig {
	public final Object[] weightedTrees;
	public final TreeClustersConfig clusterConfig;
	public final LatitudeBasedFeatureConfig.LatitudeConfiguration latitudeConfig;

	private GeneratingTreesConfig(GeneratingTreesConfig.Builder builder) {
		weightedTrees = builder.weightedTrees;
		clusterConfig = builder.clusterConfig;
		latitudeConfig = builder.latitudeConfig;
	}

	// $FF: synthetic method
	GeneratingTreesConfig(GeneratingTreesConfig.Builder x0, Object x1) {
		this(x0);
	}

	public int getCount() {
		return clusterConfig.count;
	}

	public float getExtraChance() {
		return clusterConfig.extraChance;
	}

	public int getExtraCount() {
		return clusterConfig.extraCount;
	}

	public float getTreeCountApproximation() {
		return getCount() + getExtraChance() * getExtraCount();
	}

	public int getTreeLayerUpperLimit() {
		return clusterConfig.hasLayerLimit() && clusterConfig.isLayerUpperLimit ? clusterConfig.layerLimit : Integer.MAX_VALUE;
	}

	public TreeClustersConfig makePlacementForFallenLeaves() {
		return TreeClustersConfig.builder().count(getCount() / 2).extraChance(getExtraChance()).extraCount(getExtraCount()).layerLimit(clusterConfig.layerLimit, clusterConfig.isLayerUpperLimit).build();
	}

	public TreeClustersConfig makePlacementForLeafBushes() {
		return TreeClustersConfig.builder().count(getCount() / 2).extraChance(getExtraChance()).extraCount(getExtraCount()).layerLimit(clusterConfig.layerLimit, clusterConfig.isLayerUpperLimit).build();
	}

	public boolean shouldUpdateBiomeTreeAmount() {
		return !clusterConfig.hasLayerLimit() || clusterConfig.isLayerUpperLimit;
	}

	public static GeneratingTreesConfig.Builder builder() {
		return new GeneratingTreesConfig.Builder();
	}

	public static class Builder {
		private Object[] weightedTrees;
		private TreeClustersConfig clusterConfig;
		private LatitudeBasedFeatureConfig.LatitudeConfiguration latitudeConfig;

		private Builder() {
		}

		// $FF: synthetic method
		Builder(Object x0) {
			this();
		}

		public GeneratingTreesConfig build() {
			return new GeneratingTreesConfig(this);
		}

		public GeneratingTreesConfig.Builder clusterConfig(TreeClustersConfig clusterConfig) {
			this.clusterConfig = clusterConfig;
			return this;
		}

		public GeneratingTreesConfig.Builder latitudeConfig(LatitudeBasedFeatureConfig.LatitudeConfiguration latitudeConfig) {
			this.latitudeConfig = latitudeConfig;
			return this;
		}

		public GeneratingTreesConfig.Builder weightedTrees(Object[] weightedTrees) {
			this.weightedTrees = weightedTrees;
			return this;
		}
	}
}
