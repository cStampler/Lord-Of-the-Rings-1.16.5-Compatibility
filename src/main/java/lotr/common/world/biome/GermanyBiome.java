package lotr.common.world.biome;

import lotr.common.init.*;
import lotr.common.world.biome.surface.*;
import lotr.common.world.gen.feature.TreeCluster;
import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.*;

public class GermanyBiome extends BaseWorldBiome {
    public GermanyBiome(boolean major) {
        super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.PLAINS).depth(0.08F).scale(0.2F).temperature(1.0F).downfall(1.0F), major);
    }

    @Override
    protected void addBiomeSandSediments(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addWhiteSandSediments(builder);
    }

    @Override
    protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 24, 3);
    }

    @Override
    protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        super.addReeds(builder);
        LOTRBiomeFeatures.addSugarCane(builder);
    }

    @Override
    protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addTreesWithClusters(this, builder, 0, 0.05F, TreeCluster.of(6, 50), LOTRBiomeFeatures.oak(), 5000, LOTRBiomeFeatures.oakFancy(), 1000, LOTRBiomeFeatures.oakBees(), 50, LOTRBiomeFeatures.oakFancyBees(), 10, LOTRBiomeFeatures.birch(), 1000, LOTRBiomeFeatures.birchFancy(), 500, LOTRBiomeFeatures.birchBees(), 10, LOTRBiomeFeatures.birchFancyBees(), 5, LOTRBiomeFeatures.cypress(), 5000, LOTRBiomeFeatures.cedar(), 4000, LOTRBiomeFeatures.cedarLarge(), 500);
        LOTRBiomeFeatures.addGrass(this, builder, 8, GrassBlends.STANDARD);
        LOTRBiomeFeatures.addDoubleGrass(builder, 1, GrassBlends.DOUBLE_STANDARD);
        LOTRBiomeFeatures.addPlainsFlowers(builder, 4, LOTRBlocks.MALLOS.get(), 50);
        LOTRBiomeFeatures.addAthelasChance(builder);
        LOTRBiomeFeatures.addWildPipeweedChance(builder, 24);
    }

    @Override
    public LOTRBiomeBase getShore() {
        return LOTRBiomes.BEACH.getInitialisedBiomeWrapper();
    }

    @Override
    protected void setupSurface(MiddleEarthSurfaceConfig config) {
        super.setupSurface(config);
        config.setSurfaceNoiseMixer(SurfaceNoiseMixer.createNoiseMixer(SurfaceNoiseMixer.Condition.conditionBuilder().noiseIndex(1).scales(0.3D, 0.015D).zScales(2.333D, 4.0D).weights(4, 10).threshold(0.5D).state(LOTRBlocks.WHITE_SAND)));
    }
}
