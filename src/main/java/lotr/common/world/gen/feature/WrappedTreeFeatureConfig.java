package lotr.common.world.gen.feature;

import java.util.function.Predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import lotr.common.init.LOTRTags;
import net.minecraft.block.AbstractBlock.AbstractBlockState;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.gen.feature.BaseTreeFeatureConfig;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraftforge.common.Tags.Blocks;

public class WrappedTreeFeatureConfig implements IFeatureConfig {
	public static final Codec CODEC = RecordCodecBuilder.create(instance -> instance.group(BaseTreeFeatureConfig.CODEC.fieldOf("tree_config").forGetter(config -> ((WrappedTreeFeatureConfig) config).treeConfig), WrappedTreeFeatureConfig.AlternativeTreeSoil.CODEC.fieldOf("alternative_soil_type").forGetter(config -> ((WrappedTreeFeatureConfig) config).alternativeSoilType)).apply(instance, (h1, h2) -> new WrappedTreeFeatureConfig((BaseTreeFeatureConfig) h1, (AlternativeTreeSoil) h2)));
	public final BaseTreeFeatureConfig treeConfig;
	public final WrappedTreeFeatureConfig.AlternativeTreeSoil alternativeSoilType;

	public WrappedTreeFeatureConfig(BaseTreeFeatureConfig tree, WrappedTreeFeatureConfig.AlternativeTreeSoil soil) {
		treeConfig = tree;
		alternativeSoilType = soil;
	}

	public enum AlternativeTreeSoil implements IStringSerializable {
		DESERT("desert", state -> (((AbstractBlockState) state).is(BlockTags.SAND) || ((AbstractBlockState) state).is(Blocks.SANDSTONE) || ((AbstractBlockState) state).getBlock() == net.minecraft.block.Blocks.STONE)), CHARRED("mordor", state -> (((AbstractBlockState) state).is(LOTRTags.Blocks.MORDOR_PLANT_SURFACES) || ((AbstractBlockState) state).getBlock() == net.minecraft.block.Blocks.STONE)), SNOWY("snowy", state -> (((AbstractBlockState) state).getBlock() == net.minecraft.block.Blocks.SNOW_BLOCK || ((AbstractBlockState) state).getBlock() == net.minecraft.block.Blocks.STONE));

		public static final Codec CODEC = IStringSerializable.fromEnum(WrappedTreeFeatureConfig.AlternativeTreeSoil::values, WrappedTreeFeatureConfig.AlternativeTreeSoil::forCode);
		private final String code;
		private final Predicate blockStateTest;

		AlternativeTreeSoil(String s, Predicate test) {
			code = s;
			blockStateTest = test;
		}

		@Override
		public String getSerializedName() {
			return code;
		}

		public boolean testTerrain(BlockState state) {
			return blockStateTest.test(state);
		}

		public static WrappedTreeFeatureConfig.AlternativeTreeSoil forCode(String code) {
			WrappedTreeFeatureConfig.AlternativeTreeSoil[] var1 = values();
			int var2 = var1.length;

			for (int var3 = 0; var3 < var2; ++var3) {
				WrappedTreeFeatureConfig.AlternativeTreeSoil type = var1[var3];
				if (type.getSerializedName().equals(code)) {
					return type;
				}
			}

			return null;
		}
	}
}
