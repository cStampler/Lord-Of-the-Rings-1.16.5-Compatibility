package lotr.common.world.biome;

import lotr.common.world.gen.feature.grassblend.GrassBlends;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.biome.Biome.Category;
import net.minecraft.world.biome.Biome.RainType;
import net.minecraft.world.biome.MobSpawnInfo.Spawners;

public class MERiverBiome extends LOTRBiomeBase {
	public MERiverBiome(boolean major) {
		super(new Builder().precipitation(RainType.RAIN).biomeCategory(Category.RIVER).depth(-0.75F).scale(0.0F).temperature(0.5F).downfall(0.5F), major);
	}

	@Override
	protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
		builder.addSpawn(EntityClassification.WATER_AMBIENT, new Spawners(EntityType.SALMON, 5, 1, 5));
	}

	@Override
	protected void addReeds(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		super.addReeds(builder);
		LOTRBiomeFeatures.addRiverRushes(builder);
	}

	@Override
	protected void addVegetation(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
		LOTRBiomeFeatures.addTrees(this, builder, 0, 0.5F, LOTRBiomeFeatures.oak(), 1000, LOTRBiomeFeatures.oakFancy(), 100);
		LOTRBiomeFeatures.addGrass(this, builder, 4, GrassBlends.STANDARD);
		LOTRBiomeFeatures.addDefaultFlowers(builder, 2);
		LOTRBiomeFeatures.addSeagrass(builder, 48, 0.4F);
	}

	@Override
	public boolean isRiver() {
		return true;
	}
}
