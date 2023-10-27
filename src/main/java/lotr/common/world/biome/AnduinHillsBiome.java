package lotr.common.world.biome;

import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Blocks;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class AnduinHillsBiome extends AnduinValeBiome {
	public AnduinHillsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.6F).scale(0.4F).temperature(0.6F).downfall(0.7F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
	}

	@Override
	protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 2, 4, 32, 3);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.15F, TreeCluster.of(4, 20), anduinTrees());
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 2);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 1);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}
}
