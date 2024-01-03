package lotr.common.world.biome.surface;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraftforge.fml.RegistryObject;

public final class MountainTerrainProvider {
	public static MountainLayer[] sus = {};
	public static final MountainTerrainProvider NONE = createMountainTerrain(sus);
	public static final Codec<MountainTerrainProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(MountainTerrainProvider.MountainLayer.CODEC.listOf().fieldOf("layers").forGetter(mixer -> ((MountainTerrainProvider) mixer).layers)).apply(instance, h1 -> new MountainTerrainProvider(h1)));
	private final List<MountainLayer> layers;
	private MountainTerrainProvider(List<MountainLayer> layers) { // Specify the type for the List parameter
		this.layers = layers;
	}

	public BlockState getReplacement(int x, int z, int y, BlockState in, BlockState stone, boolean top, int stoneNoiseDepth) {
		for (MountainLayer layer : this.layers) {
		      if (layer.passes(y, in, stone, top, stoneNoiseDepth))
		        return layer.getState(stone); 
		    } 
		    return in;
	}

	public static MountainTerrainProvider createMountainTerrain(MountainTerrainProvider.MountainLayer... layers) {
		return new MountainTerrainProvider(Arrays.asList(layers));
	}

	public static MountainTerrainProvider createMountainTerrain(MountainTerrainProvider.MountainLayer.MountainLayerBuilder... builders) {
		return new MountainTerrainProvider(Stream.of(builders).map(MountainTerrainProvider.MountainLayer.MountainLayerBuilder::build).collect(Collectors.toList()));
	}

	public static final class MountainLayer {
		public static final Codec<MountainLayer> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.INT.fieldOf("above").forGetter(MountainLayer::getAbove),
				BlockState.CODEC.optionalFieldOf("state").forGetter(MountainLayer::getState),
				Codec.BOOL.fieldOf("use_stone").orElse(false).forGetter(MountainLayer::isUseStone),
				Codec.BOOL.fieldOf("replace_stone").orElse(true).forGetter(MountainLayer::isReplaceStone),
				Codec.BOOL.fieldOf("top_only").orElse(false).forGetter(MountainLayer::isTopOnly)
		).apply(instance, MountainLayer::new));

		private final int above;
		private final Optional<BlockState> state;
		private final boolean useStone;
		private final boolean replaceStone;
		private final boolean topOnly;

		public MountainLayer(int above, Optional<BlockState> state, boolean useStone, boolean replaceStone, boolean topOnly) {
			this.above = above;
			this.state = state;
			this.useStone = useStone;
			this.replaceStone = replaceStone;
			this.topOnly = topOnly;
		}

		public int getAbove() {
			return above;
		}

		public Optional<BlockState> getState() {
			return state;
		}

		public boolean isUseStone() {
			return useStone;
		}

		public boolean isReplaceStone() {
			return replaceStone;
		}

		public boolean isTopOnly() {
			return topOnly;
		}

		private MountainLayer(MountainTerrainProvider.MountainLayer.MountainLayerBuilder builder) {
			this(builder.above, Optional.ofNullable(builder.state), builder.useStone, builder.replaceStone, builder.topOnly);
		}

		// $FF: synthetic method
		MountainLayer(MountainTerrainProvider.MountainLayer.MountainLayerBuilder x0, Object x1) {
			this(x0);
		}

		public BlockState getState(BlockState stone) {
			return useStone ? stone : (BlockState) state.get();
		}

		public boolean passes(int y, BlockState in, BlockState stone, boolean top, int stoneNoiseDepth) {
			if (topOnly && !top || !replaceStone && in.getBlock() == stone.getBlock()) {
				return false;
			}
			return y >= above - stoneNoiseDepth;
		}

		public static MountainTerrainProvider.MountainLayer.MountainLayerBuilder layerBuilder() {
			return new MountainTerrainProvider.MountainLayer.MountainLayerBuilder();
		}

		public static class MountainLayerBuilder {
			private int above;
			private BlockState state;
			private boolean useStone;
			private boolean replaceStone;
			private boolean topOnly;

			private MountainLayerBuilder() {
				above = -1;
				useStone = false;
				replaceStone = true;
				topOnly = false;
			}

			// $FF: synthetic method
			MountainLayerBuilder(Object x0) {
				this();
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder above(int above) {
				this.above = above;
				return this;
			}

			public MountainTerrainProvider.MountainLayer build() {
				if (above < 0) {
					throw new IllegalArgumentException("above y-value not set or too low");
				}
				if (state == null && !useStone) {
					throw new IllegalArgumentException("block state not set and use_stone is not enabled instead");
				}
				return new MountainTerrainProvider.MountainLayer(this);
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder excludeStone() {
				replaceStone = false;
				return this;
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder state(Block block) {
				return this.state(block.defaultBlockState());
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder state(BlockState state) {
				this.state = state;
				useStone = false;
				return this;
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder state(RegistryObject<Block> blockSup) {
				return this.state((Block) blockSup.get());
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder topOnly() {
				topOnly = true;
				return this;
			}

			public MountainTerrainProvider.MountainLayer.MountainLayerBuilder useStone() {
				state = null;
				useStone = true;
				excludeStone();
				return this;
			}
		}
	}
}
