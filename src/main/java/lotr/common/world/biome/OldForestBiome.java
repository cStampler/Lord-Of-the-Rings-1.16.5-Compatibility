package lotr.common.world.biome;

import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;

public class OldForestBiome extends LOTRBiomeBase {
	public OldForestBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.FOREST).depth(0.2F).scale(0.3F).temperature(0.6F).downfall(0.9F), major);
		biomeColors.setGrass(5477193).setFoliage(3172394).setFog(3627845).setFoggy(true);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
	}

	@Override
	protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addCommonGranite(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 18, 0.0F, LOTRBiomeFeatures.oak(), 3000, LOTRBiomeFeatures.oakVines(), 1000, LOTRBiomeFeatures.oakTall(), 6000, LOTRBiomeFeatures.oakTallVines(), 2000, LOTRBiomeFeatures.oakFancy(), 4000, LOTRBiomeFeatures.oakBees(), 3, LOTRBiomeFeatures.oakBeesVines(), 1, LOTRBiomeFeatures.oakTallBees(), 6, LOTRBiomeFeatures.oakTallBeesVines(), 2, LOTRBiomeFeatures.oakFancyBees(), 4, LOTRBiomeFeatures.oakDead(), 1000, LOTRBiomeFeatures.oakParty(), 300, LOTRBiomeFeatures.oakShrub(), 2000, LOTRBiomeFeatures.darkOak(), 8000, LOTRBiomeFeatures.darkOakParty(), 500, LOTRBiomeFeatures.darkOakShrub(), 1000, LOTRBiomeFeatures.fir(), 2000, LOTRBiomeFeatures.firShrub(), 200, LOTRBiomeFeatures.shirePine(), 1000, LOTRBiomeFeatures.pineShrub(), 100);
		LOTRBiomeFeatures.addGrass(this, builder, 12, GrassBlends.WITH_FERNS);
		LOTRBiomeFeatures.addDoubleGrass(builder, 5, GrassBlends.DOUBLE_WITH_FERNS);
		LOTRBiomeFeatures.addForestFlowers(builder, 1);
		LOTRBiomeFeatures.addMoreMushroomsFreq(builder, 2);
		LOTRBiomeFeatures.addFallenLogs(builder, 1);
	}
}
