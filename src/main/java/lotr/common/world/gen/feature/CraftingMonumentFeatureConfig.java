package lotr.common.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.BlockState;
import net.minecraft.world.gen.blockstateprovider.BlockStateProvider;
import net.minecraft.world.gen.feature.IFeatureConfig;

public class CraftingMonumentFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BlockState.CODEC.fieldOf("table_state").forGetter(config -> ((CraftingMonumentFeatureConfig) config).craftingTable), BlockStateProvider.CODEC.fieldOf("base_provider").forGetter(config -> ((CraftingMonumentFeatureConfig) config).baseBlockProvider), BlockStateProvider.CODEC.fieldOf("post_provider").forGetter(config -> ((CraftingMonumentFeatureConfig) config).postProvider), BlockStateProvider.CODEC.fieldOf("torch_provider").forGetter(config -> ((CraftingMonumentFeatureConfig) config).torchProvider)).apply(instance, CraftingMonumentFeatureConfig::new));
	public final BlockState craftingTable;
	public final BlockStateProvider baseBlockProvider;
	public final BlockStateProvider postProvider;
	public final BlockStateProvider torchProvider;

	public CraftingMonumentFeatureConfig(BlockState table, BlockStateProvider base, BlockStateProvider post, BlockStateProvider torch) {
		craftingTable = table;
		baseBlockProvider = base;
		postProvider = post;
		torchProvider = torch;
	}
}
