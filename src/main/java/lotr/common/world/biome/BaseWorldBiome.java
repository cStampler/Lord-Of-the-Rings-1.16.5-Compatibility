package lotr.common.world.biome;

import lotr.common.init.LOTRBlocks;
import lotr.common.world.biome.surface.MiddleEarthSurfaceConfig;
import lotr.common.world.map.RoadBlockProvider;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome.Builder;
import net.minecraft.world.gen.blockstateprovider.WeightedBlockStateProvider;

public abstract class BaseWorldBiome extends LOTRBiomeBase {
    protected BaseWorldBiome(Builder builder, boolean major) {
        super(builder, major);
    }

    @Override
    protected void addAnimals(net.minecraft.world.biome.MobSpawnInfo.Builder builder) {
        super.addAnimals(builder);
        this.addHorsesDonkeys(builder);
        this.addBears(builder);
        this.addElk(builder);
    }

    @Override
    protected void addBoulders(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 200, 3);
        LOTRBiomeFeatures.addBoulders(builder, Blocks.STONE.defaultBlockState(), 1, 3, 200, 3);
    }

    @Override
    protected void addStoneVariants(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addGranite(builder);
        LOTRBiomeFeatures.addDiorite(builder);
        LOTRBiomeFeatures.addAndesite(builder);
    }

    @Override
    protected void addOres(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        super.addOres(builder);
        LOTRBiomeFeatures.addDiamondOre(builder, 40);
        LOTRBiomeFeatures.addGlowstoneOre(builder);
    }

    @Override
    protected void addStructures(net.minecraft.world.biome.BiomeGenerationSettings.Builder builder) {
        LOTRBiomeFeatures.addCraftingMonument(builder, ((Block) LOTRBlocks.GONDOR_CRAFTING_TABLE.get()).defaultBlockState(), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.GONDOR_BRICK.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(((Block) LOTRBlocks.STONE_WALL.get()).defaultBlockState(), 1), new WeightedBlockStateProvider().add(Blocks.TORCH.defaultBlockState(), 1));
    }

    @Override
    public RoadBlockProvider getRoadBlockProvider() {
        return RoadBlockProvider.COBBLESTONE;
    }

    @Override
    protected void setupSurface(MiddleEarthSurfaceConfig config) {
        config.addSubSoilLayer( Blocks.STONE.defaultBlockState(), 8, 10);
    }
}
