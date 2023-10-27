package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class ShireWoodlandsBiome extends ShireBiome {
	public ShireWoodlandsBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.3F).scale(0.5F).temperature(0.8F).downfall(0.9F), major);
		biomeColors.resetGrass();
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		super.addAnimals(builder);
		this.addFoxes(builder, 2);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 9, 0.5F, LOTRBiomeFeatures.shirePine(), 25000, LOTRBiomeFeatures.oak(), 10000, LOTRBiomeFeatures.oakFancy(), 4000, LOTRBiomeFeatures.oakBees(), 20, LOTRBiomeFeatures.oakFancyBees(), 8, LOTRBiomeFeatures.birch(), 2500, LOTRBiomeFeatures.birchFancy(), 100, LOTRBiomeFeatures.birchBees(), 1, LOTRBiomeFeatures.birchFancyBees(), 1, LOTRBiomeFeatures.aspen(), 3000, LOTRBiomeFeatures.aspenLarge(), 1000, LOTRBiomeFeatures.apple(), 50, LOTRBiomeFeatures.appleBees(), 1, LOTRBiomeFeatures.pear(), 50, LOTRBiomeFeatures.pearBees(), 1, LOTRBiomeFeatures.cherry(), 20, LOTRBiomeFeatures.cherryBees(), 1);
		LOTRBiomeFeatures.addGrass(this, builder, 10, GrassBlends.SHIRE_WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 2, GrassBlends.DOUBLE_WITH_FERNS);
		LOTRBiomeFeatures.addPlainsFlowers(builder, 6, LOTRBlocks.SHIRE_HEATHER.get(), 20);
		LOTRBiomeFeatures.addDefaultDoubleFlowers(builder, 2);
		LOTRBiomeFeatures.addWildPipeweedChance(builder, 6);
		LOTRBiomeFeatures.addFoxBerryBushes(builder);
	}

	@Override
	public RoadBlockProvider getRoadBlockProvider() {
		return RoadBlockProvider.PATH;
	}
}
