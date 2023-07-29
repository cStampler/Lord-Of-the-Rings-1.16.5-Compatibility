package lotr.common.world.biome;

import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.entity.*;
import net.minecraft.world.IWorld;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biome.*;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class LakeBiome extends LOTRBiomeBase {
	public LakeBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.RIVER).depth(-0.9F).scale(0.15F).temperature(0.5F).downfall(0.5F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		builder.addSpawn(EntityClassification.WATER_AMBIENT, new Spawners(EntityType.SALMON, 5, 1, 5));
	}

	@Override
	protected void addSedimentDisks(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addClayGravelSediments(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.2F, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakFancy(), 100);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 2);
		LOTRBiomeFeatures.addSeagrass(builder, 48, 0.4F);
	}

	@Override
	public Biome getRiver(IWorld world) {
		return null;
	}
}